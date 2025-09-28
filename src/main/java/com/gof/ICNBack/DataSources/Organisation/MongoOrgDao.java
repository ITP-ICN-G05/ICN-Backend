package com.gof.ICNBack.DataSources.Organisation;

import com.gof.ICNBack.DataSources.Entity.ItemEntity;
import com.gof.ICNBack.DataSources.Entity.OrganisationEntity;
import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Repositories.MongoOrganisationRepository;
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

public class MongoOrgDao extends OrganisationDao {

    @Autowired
    MongoTemplate template;

    @Autowired
    MongoOrganisationRepository repo;

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

        List<Criteria> criteriaList = new ArrayList<>();

        if (filterParameters != null && !filterParameters.isEmpty()) {
            for (Map.Entry<String, String> entry : filterParameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (value != null && !value.trim().isEmpty()) {
                    if (isNumericField(key)) {
                        try {
                            criteriaList.add(Criteria.where(key).is(Integer.parseInt(value)));
                        } catch (NumberFormatException e) {
                            criteriaList.add(Criteria.where(key).is(value));
                        }
                    } else if (isBooleanField(value)) {
                        criteriaList.add(Criteria.where(key).is(Boolean.parseBoolean(value)));
                    } else {
                        criteriaList.add(Criteria.where(key).is(value));
                    }
                }
            }
        }

        if (searchString != null && !searchString.trim().isEmpty()) {
            Criteria textSearchCriteria = new Criteria().orOperator(
                    Criteria.where("Item Name").regex(searchString, "i"),
                    Criteria.where("Detailed Item Name").regex(searchString, "i"),
                    Criteria.where("Sector Name").regex(searchString, "i"),
                    Criteria.where("Organisation: Organisation Name").regex(searchString, "i")
            );
            criteriaList.add(textSearchCriteria);
        }

        // combine criteria
        Criteria combinedCriteria = null;
        if (!criteriaList.isEmpty()) {
            combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }

        // 2. reorder box
        double lowerLeftX = Math.min(locationX, endX);
        double lowerLeftY = Math.min(locationY, endY);
        double upperRightX = Math.max(locationX, endX);
        double upperRightY = Math.max(locationY, endY);

        // 3. geo match +match(Criteria) -> sort/skip/limit
        List<AggregationOperation> ops = new ArrayList<>();
        ops.add(geoWithinBoxMatch("Organizations.Organisation: Coord.coordinates", lowerLeftX, lowerLeftY, upperRightX, upperRightY));

        if (combinedCriteria != null) {
            ops.add(Aggregation.match(combinedCriteria));
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

    @Override
    public Organisation getOrganisationById(String organisationId) {
        List<ItemEntity> items = repo.findByOrganisationId(organisationId);
        if (items.isEmpty()) return null;
        Organisation organisation = null;
        for (OrganisationEntity o : items.get(0).getOrganizations()){
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
        repo.saveAll(entity);
    }

}
