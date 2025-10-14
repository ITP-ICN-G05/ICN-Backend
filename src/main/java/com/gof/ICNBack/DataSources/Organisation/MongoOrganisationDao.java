package com.gof.ICNBack.DataSources.Organisation;

import com.gof.ICNBack.DataSources.Entity.ItemEntity;
import com.gof.ICNBack.DataSources.Entity.OrganisationEntity;
import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Repositories.MongoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.*;

import static com.gof.ICNBack.DataSources.Utils.MongoUtils.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class MongoOrganisationDao extends OrganisationDao {

    @Autowired
    MongoTemplate template;

    @Autowired
    MongoItemRepository repo;

    @Override
    public List<Organisation> searchOrganisations(
            double locationX,
            double locationY,
            double endX,
            double endY,
            Map<String, String> filterParameters,
            String searchString,
            Integer skip,
            Integer limit) {

        List<Criteria> itemCriteriaList = new ArrayList<>();
        List<Criteria> orgCriteriaList = new ArrayList<>();

        if (filterParameters != null && !filterParameters.isEmpty()) {
            for (Map.Entry<String, String> entry : filterParameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (value != null && !value.trim().isEmpty()) {
                    List<Criteria> list;
                    if (isItemField(key)) {
                        list = itemCriteriaList;
                    } else {
                        list = orgCriteriaList;
                    }
                    if (isNumericField(key)) {
                        try {
                            list.add(Criteria.where(key).is(Integer.parseInt(value)));
                        } catch (NumberFormatException e) {
                            list.add(Criteria.where(key).is(value));
                        }
                    } else if (isBooleanField(value)) {
                        list.add(Criteria.where(key).is(Boolean.parseBoolean(value)));
                    } else {
                        list.add(Criteria.where(key).is(value));
                    }
                }
            }
        }

        if (searchString != null && !searchString.trim().isEmpty()) {
            Criteria textSearchCriteria = new Criteria().orOperator(
                    Criteria.where("Item Name").regex(searchString, "i"),
                    Criteria.where("Detailed Item Name").regex(searchString, "i"),
                    Criteria.where("Sector Name").regex(searchString, "i")
            );
            itemCriteriaList.add(textSearchCriteria);
            orgCriteriaList.add(Criteria.where("Organisation: Organisation Name").regex(searchString, "i"));
        }

        // combine criteria
        Criteria combinedListCriteria = null;
        if (!itemCriteriaList.isEmpty()) {
            combinedListCriteria = new Criteria().andOperator(itemCriteriaList.toArray(new Criteria[0]));
        }
        Criteria combinedOrgCriteria = null;
        if (!orgCriteriaList.isEmpty()) {
            combinedOrgCriteria = new Criteria().andOperator(orgCriteriaList.toArray(new Criteria[0]));
        }

        List<AggregationOperation> ops = new ArrayList<>();

        if (locationX != 0 && locationY != 0 && endX != 0 && endY != 0){
            // 2. reorder box
            double lowerLeftX = Math.min(locationX, endX);
            double lowerLeftY = Math.min(locationY, endY);
            double upperRightX = Math.max(locationX, endX);
            double upperRightY = Math.max(locationY, endY);

            // 3. geo match +match(Criteria) -> sort/skip/limit
            ops.add(geoWithinBoxMatch("Coord.coordinates", lowerLeftX, lowerLeftY, upperRightX, upperRightY));

        }

        if (combinedOrgCriteria != null) {
            ops.add(Aggregation.match(combinedOrgCriteria));
        }

        // sorting
        ops.add(Aggregation.sort(Sort.by(Sort.Direction.ASC, "Item Name")));

        // paging
        if (skip != null && skip > 0) {
            ops.add(Aggregation.skip(skip.longValue()));
        }
        if (limit != null && limit > 0) {
            ops.add(Aggregation.limit(limit.longValue()));
        }

        Aggregation agg = Aggregation.newAggregation(ops);

        // 4. query
        String collectionName = template.getCollectionName(ItemEntity.class);
        AggregationResults<ItemEntity> aggResults = template.aggregate(agg, collectionName, ItemEntity.class);
        List<ItemEntity> items = aggResults.getMappedResults();

        // 5. return organisation
        return processToOrganisations(items);
    }

    private boolean isNumericField(String fieldName) {
        Set<String> numericFields = Set.of("Subtotal");
        return numericFields.contains(fieldName);
    }

    private boolean isBooleanField(String fieldName) {
        Set<String> booleanFields = Set.of();
        return booleanFields.contains(fieldName);
    }

    private boolean isItemField(String fieldName){
        Set<String> itemFields = Set.of("Sector Name");
        return itemFields.contains(fieldName);
    }

    private void setAggregation(Criteria criteria){

    }
    @Override
    public Organisation getOrganisationById(String organisationId) {
        List<ItemEntity> items = repo.findByOrganisationId(organisationId);
        if (items.isEmpty()) return null;
        Organisation organisation = null;
        for (String o : items.get(0).getOrganizations()){
            if (o.getOrganisationId().equals(organisationId)){
                organisation = o.toDomain();
                Organisation finalOrganisation = organisation;
                items.forEach(i -> {
                    finalOrganisation.getItems().add(
                            i.domainBuilder()
                                    .setCapabilityType(o.getCapabilityType())
                                    .setValidationDate(o.getValidationDate())
                                    .setOrganisationCapability(o.getOrganisationCapability())
                                    .build());
                });
            }
        }
        return organisation;
    }

    @Override
    public List<Organisation.OrganisationCard> getOrgCardsByIds(List<String> orgIds) {
        List<Organisation.OrganisationCard> org = new ArrayList<>();
        for (String id : orgIds){
            Organisation o = getOrganisationById(id);
            if (o != null){
                org.add(o.toCard());
            }
        }
        return org;
    }

    @Override
    public List<Organisation> getOrganisationsWithoutGeocode() {
        Query query = new Query((new Criteria().orOperator(where("Geocoded").exists(false), where("Geocoded").is(false))));
        return processToOrganisations(template.find(query, ItemEntity.class));
    }

    @Override
    public void updateGeocode(List<Organisation> orgs) {
        List<ItemEntity> entity = processToItemEntity(orgs);
        entity.forEach(a -> {
            for (OrganisationEntity o : a.getOrganizations()){
                if(o.getCoord() == null){
                    return;
                }
            }
            a.setGeocoded(true);
        });
        repo.saveAll(entity);
    }
}
