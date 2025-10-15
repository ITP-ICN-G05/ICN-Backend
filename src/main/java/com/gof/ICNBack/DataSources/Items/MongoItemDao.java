package com.gof.ICNBack.DataSources.Items;

import com.gof.ICNBack.DataSources.Entity.ItemEntity;
import com.gof.ICNBack.DataSources.Entity.OrganisationEntity;
import com.gof.ICNBack.Entity.Item;
import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Repositories.MongoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.gof.ICNBack.DataSources.Utils.MongoUtils.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class MongoItemDao extends ItemDao {

    @Autowired
    MongoTemplate template;

    @Autowired
    MongoItemRepository repo;

    @Override
    public List<Organisation.OrganisationCard> searchOrganisationCardsByItem(
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
                    Criteria.where("Sector Name").regex(searchString, "i")
            );
            criteriaList.add(textSearchCriteria);
        }

        // combine criteria
        Criteria combinedCriteria = null;
        if (!criteriaList.isEmpty()) {
            combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        }

        // 3. geo match +match(Criteria) -> sort/skip/limit
        List<AggregationOperation> ops = new ArrayList<>();

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
    public Item getItemById(String detailedItemId) {
        ItemEntity item = repo.findByDetailedItemId(detailedItemId);
        //TODO: organisation related information not inserted as this function only returns item object
        return item.domainBuilder().build();
    }
}
