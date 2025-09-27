package com.gof.ICNBack.DataSources.Organisation;

import com.gof.ICNBack.DataSources.Entity.ItemEntity;
import com.gof.ICNBack.DataSources.Entity.OrganisationEntity;
import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Repositories.MongoOrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

import static com.gof.ICNBack.DataSources.Utils.MongoUtils.*;

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

        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        // process locations
        criteriaList.add(createBoundingBoxCriteria("Coord", locationX, locationY, endX, endY));

        // dynamic filter
        if (filterParameters != null && !filterParameters.isEmpty()) {
            for (Map.Entry<String, String> entry : filterParameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (value != null && !value.trim().isEmpty()) {
                    // construction basing in type of params
                    if (isNumericField(key)) {
                        // numeric
                        try {
                            criteriaList.add(Criteria.where(key).is(Integer.parseInt(value)));
                        } catch (NumberFormatException e) {
                            // turn to string matching
                            criteriaList.add(Criteria.where(key).is(value));
                        }
                    } else if (isBooleanField(value)) {
                        // boolean
                        criteriaList.add(Criteria.where(key).is(Boolean.parseBoolean(value)));
                    } else {
                        // string
                        criteriaList.add(Criteria.where(key).is(value));
                    }
                }
            }
        }

        // process keywords
        if (searchString != null && !searchString.trim().isEmpty()) {
            Criteria textSearchCriteria = new Criteria().orOperator(
                    Criteria.where("name").regex(searchString, "i"),
                    Criteria.where("description").regex(searchString, "i"),
                    Criteria.where("tags").regex(searchString, "i"),
                    Criteria.where("address").regex(searchString, "i")
            );
            criteriaList.add(textSearchCriteria);
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        // process page
        if (skip != null && skip > 0) {
            query.skip(skip);
        }

        if (limit != null && limit > 0) {
            query.limit(limit);
        }

        // sorting
        query.with(Sort.by(Sort.Direction.ASC, "name"));

        // join backward
        List<ItemEntity> items = template.find(query, ItemEntity.class);

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
        return processToOrganisations(repo.findByGeocoded(false));
    }

    @Override
    public void updateGeocode(List<Organisation> orgs) {
        List<ItemEntity> entity = processToItemEntity(orgs);
        repo.saveAll(entity);
    }

}
