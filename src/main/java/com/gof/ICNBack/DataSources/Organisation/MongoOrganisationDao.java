package com.gof.ICNBack.DataSources.Organisation;

import com.gof.ICNBack.DataSources.Entity.ItemEntity;
import com.gof.ICNBack.DataSources.Entity.OrganisationEntity;
import com.gof.ICNBack.Entity.Organisation;
import com.gof.ICNBack.Repositories.MongoItemRepository;
import com.gof.ICNBack.Repositories.MongoOrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

import static com.gof.ICNBack.DataSources.Utils.MongoUtils.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class MongoOrganisationDao extends OrganisationDao {

    @Autowired
    MongoTemplate template;

    @Autowired
    MongoItemRepository itemRepository;

    @Autowired
    MongoOrganisationRepository organisationRepository;

    @Override
    public List<Organisation.OrganisationCard> searchOrganisationCards(
            double locationX,
            double locationY,
            double endX,
            double endY,
            Map<String, String> filterParameters,
            String searchString,
            Integer skip,
            Integer limit) {
        List<AggregationOperation> ops = new ArrayList<>();

        // 1. 首先从商品集合开始，处理商品相关的筛选条件
        Criteria itemCriteria = new Criteria();
        List<Criteria> itemCriteriaList = new ArrayList<>();

        // 处理商品搜索字符串
        if (searchString != null && !searchString.trim().isEmpty()) {
            Criteria textSearchCriteria = new Criteria().orOperator(
                    Criteria.where("Item Name").regex(searchString, "i"),
                    Criteria.where("Detailed Item Name").regex(searchString, "i"),
                    Criteria.where("Sector Name").regex(searchString, "i")
            );
            itemCriteriaList.add(textSearchCriteria);
        }

        // 处理商品过滤参数
        if (filterParameters != null && !filterParameters.isEmpty()) {
            for (Map.Entry<String, String> entry : filterParameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (value != null && !value.trim().isEmpty() && isItemField(key)) {
                    if (isNumericField(key)) {
                        try {
                            itemCriteriaList.add(itemCriteria.where(key).is(Integer.parseInt(value)));
                        } catch (NumberFormatException e) {
                            itemCriteriaList.add(itemCriteria.where(key).is(value));
                        }
                    } else if (isBooleanField(key)) {
                        itemCriteriaList.add(itemCriteria.where(key).is(Boolean.parseBoolean(value)));
                    } else {
                        itemCriteriaList.add(itemCriteria.where(key).is(value));
                    }
                }
            }
        }

        // 如果有商品筛选条件，添加到聚合管道
        if (!itemCriteriaList.isEmpty()) {
            itemCriteria = new Criteria().andOperator(itemCriteriaList.toArray(new Criteria[0]));
            ops.add(Aggregation.match(itemCriteria));
        }

        // 2. 关联公司集合
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("organisation") // 公司集合名称，根据实际情况调整
                .localField("Organisation IDs") // 商品中关联公司的字段
                .foreignField("Organisation ID") // 公司的主键字段
                .as("orgInfo");
        ops.add(lookupOperation);

        // 3. 展开公司信息
        ops.add(Aggregation.unwind("orgInfo"));

        // 4. 处理公司相关的筛选条件和地理位置
        List<Criteria> orgCriteriaList = new ArrayList<>();

        // 处理公司过滤参数（非商品相关的参数）
        if (filterParameters != null && !filterParameters.isEmpty()) {
            for (Map.Entry<String, String> entry : filterParameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (value != null && !value.trim().isEmpty() && !isItemField(key)) {
                    if (isNumericField(key)) {
                        try {
                            orgCriteriaList.add(Criteria.where(key).is(Integer.parseInt(value)));
                        } catch (NumberFormatException e) {
                            orgCriteriaList.add(Criteria.where(key).is(value));
                        }
                    } else if (isBooleanField(key)) {
                        orgCriteriaList.add(Criteria.where(key).is(Boolean.parseBoolean(value)));
                    } else {
                        orgCriteriaList.add(Criteria.where(key).is(value));
                    }
                }
            }
        }

        // 处理地理位置筛选
        if (locationX != 0 && locationY != 0 && endX != 0 && endY != 0) {
            double lowerLeftX = Math.min(locationX, endX);
            double lowerLeftY = Math.min(locationY, endY);
            double upperRightX = Math.max(locationX, endX);
            double upperRightY = Math.max(locationY, endY);

            // 对公司位置进行筛选
            ops.add(geoWithinBoxMatch("orgInfo.Coord.coordinates", lowerLeftX, lowerLeftY, upperRightX, upperRightY));
        }

        // 合并公司筛选条件
        if (!orgCriteriaList.isEmpty()) {
            Criteria combinedCriteria = new Criteria().andOperator(orgCriteriaList.toArray(new Criteria[0]));
            ops.add(Aggregation.match(combinedCriteria));
        }

        // 5. 投影字段，只返回需要的公司信息
        ProjectionOperation projectOperation = Aggregation.project()
                .and("orgInfo.Organisation ID").as("Organisation ID")
                .and("orgInfo.Organisation Name").as("Organisation Name")
                .and("orgInfo.Billing Street").as("Billing Street")
                .and("orgInfo.Billing City").as("Billing City")
                .and("orgInfo.Billing State/Province").as("Billing State/Province")
                .and("orgInfo.Billing Zip/Postal Code").as("Billing Zip/Postal Code")
                .and("orgInfo.Coord").as("Coord");
        ops.add(projectOperation);

        // 6. 分页处理
        if (skip != null && skip > 0) {
            ops.add(Aggregation.skip(skip.longValue()));
        }
        if (limit != null && limit > 0) {
            ops.add(Aggregation.limit(limit.longValue()));
        }

        // 7. 执行聚合查询
        Aggregation agg = Aggregation.newAggregation(ops);

        // 注意：这里从商品集合开始查询
        String collectionName = template.getCollectionName(ItemEntity.class); // 假设有 ProductEntity
        AggregationResults<OrganisationEntity> aggResults = template.aggregate(agg, collectionName, OrganisationEntity.class);
        List<OrganisationEntity> orgs = aggResults.getMappedResults();

        return orgs.stream().map(o -> o.toDomain().toCard()).toList();
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
        Set<String> itemFields = Set.of("Sector Name", "Item Name", "", "");
        return itemFields.contains(fieldName);
    }

    @Override
    public Organisation getOrganisationById(String organisationId) {
        OrganisationEntity org = organisationRepository.findByOrganisationId(organisationId);
        return fillOrganisations(List.of(org)).get(0);
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
        Query query = new Query(
                new Criteria().orOperator(
                        where("Geocoded").exists(false),
                        where("Geocoded").is(false)
                )
        );

        return fillOrganisations(
                template.find(query, OrganisationEntity.class)
        );
    }

    @Override
    public void updateGeocode(List<Organisation> orgs) {
        organisationRepository.saveAll(
            orgs.stream().map(o -> {
                OrganisationEntity e = o.toEntity();
                if(o.getCoord() != null){
                    e.setGeocoded(true);
                }
                return e;
            }).toList()
        );
    }


    private List<Organisation> fillOrganisations(List<OrganisationEntity> organisations){
        if (organisations == null) return null;

        return organisations.stream().map(o ->
                {
                    Map<String, ItemEntity> items = new HashMap<>();

                    itemRepository
                            .findByOrganisationId(o.getOrganisationId())
                            .forEach(i -> items.put(i.getDetailedItemId(), i));

                    return o.toDomain(
                            new ArrayList<>(
                                    o.getItems().stream().map(i -> items
                                            .get(i.getId())
                                            .domainBuilder()
                                            .setValidationDate(i.getValidationDate())
                                            .setOrganisationCapability(i.getCapability())
                                            .setCapabilityType(i.getCapabilityType())
                                            .build()).toList()
                            )
                    );
                }
        ).toList();
    }
}
