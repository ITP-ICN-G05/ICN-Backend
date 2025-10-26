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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        // 2. geo match +match(Criteria) -> sort/skip/limit
        List<AggregationOperation> ops = new ArrayList<>();

        boolean usingPosition = true;
        // 3. reorder box
        if(!(locationX == 0 || locationY == 0 || endX==0 || endY==0)){
            double lowerLeftX = Math.min(locationX, endX);
            double lowerLeftY = Math.min(locationY, endY);
            double upperRightX = Math.max(locationX, endX);
            double upperRightY = Math.max(locationY, endY);
            ops.add(geoWithinBoxMatch("Organizations.Organisation: Coord.coordinates", lowerLeftX, lowerLeftY, upperRightX, upperRightY));
            usingPosition = false;
        }

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
        List<Organisation> result = processToOrganisations(items);

        return applySecondaryFiltering(result, filterParameters, searchString, locationX, locationY, endX, endY);
    }

    /**
     * secondary filtering to Organisation
     */
    private List<Organisation> applySecondaryFiltering(List<Organisation> organisations,
                                                       Map<String, String> filterParameters,
                                                       String searchString,
                                                       double locationX, double locationY,
                                                       double endX, double endY) {
        if (organisations == null || organisations.isEmpty()) {
            return organisations;
        }

        Stream<Organisation> stream = organisations.stream();

        //location
        if (!(locationX == 0 || locationY == 0 || endX == 0 || endY == 0)) {
            double minLat = Math.min(locationY, endY);
            double maxLat = Math.max(locationY, endY);
            double minLng = Math.min(locationX, endX);
            double maxLng = Math.max(locationX, endX);

            stream = stream.filter(org -> isWithinBoundingBox(org, minLat, maxLat, minLng, maxLng));
        }


        //searchString
        if (searchString != null && !searchString.trim().isEmpty()) {
            final String finalSearchString = searchString.toLowerCase().trim();
            stream = stream.filter(org ->
                    matchesOrganisationSearch(org, finalSearchString)
            );
        }

        //distinct
        stream = stream.filter(distinctByKey(org ->
                org.get_id() != null ? org.get_id() : org.getName()
        ));

        return stream.collect(Collectors.toList());
    }

    private boolean isWithinBoundingBox(Organisation org, double minLat, double maxLat, double minLng, double maxLng) {
        if (org == null || org.buildCoord() == null) {
            return false;
        }

        double latitude = org.getLatitude();
        double longitude = org.getLongitude();

        boolean withinLat = latitude >= minLat && latitude <= maxLat;
        boolean withinLng = longitude >= minLng && longitude <= maxLng;

        return withinLat && withinLng;
    }


    private boolean matchesOrganisationSearch(Organisation org, String searchString) {
        if (searchString == null || searchString.isEmpty()) {
            return true;
        }

        return (org.getName() != null && org.getName().toLowerCase().contains(searchString));
    }


    /**
     * distinction
     */
    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
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
        if(orgIds == null) return org;
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
