package com.gof.ICNBack.DataSources.Organisation;

import com.gof.ICNBack.Entity.Organisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MongoOrgDao extends OrganisationDao {

    @Autowired
    MongoTemplate template;


    @Override
    public List<Organisation> searchOrganisations(int locationX, int locationY, int lenX, int lenY, Map<String, String> filterParameters, String searchString, Integer skip, Integer limit) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        // process locations


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

        // 7. 执行查询
        return template.find(query, Organisation.class);
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
        return null;
    }

    @Override
    public List<Organisation.OrganisationCard> getOrgCardsByIds(List<String> orgIds) {
        return null;
    }
}
