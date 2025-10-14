package com.gof.ICNBack.DataSources.Utils;

import com.gof.ICNBack.DataSources.Entity.ItemEntity;
import com.gof.ICNBack.Entity.Organisation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.bson.Document;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoUtils {
    public static AggregationOperation geoWithinBoxMatch(String fieldName,
                                                         double lowerLeftX, double lowerLeftY,
                                                         double upperRightX, double upperRightY) {
        return context -> new Document("$match",
                new Document(fieldName,
                        new Document("$geoWithin",
                                new Document("$box", Arrays.asList(
                                        Arrays.asList(lowerLeftX, lowerLeftY),
                                        Arrays.asList(upperRightX, upperRightY)
                                ))
                        )
                )
        );
    }

    public static List<Organisation> processToOrganisations(List<ItemEntity> items){
        Map<String, Organisation> orgs = new HashMap<>();
        items.forEach(i -> i.getOrganizations().forEach(o -> {
            if (!orgs.containsKey(o.getOrganisationId())){
                orgs.put(o.getOrganisationId(), o.toDomain());
            }
            orgs.get(o.getOrganisationId()).getItems().add(
                    i.domainBuilder()
                        .setCapabilityType(o.getCapabilityType())
                        .setValidationDate(o.getValidationDate())
                        .setOrganisationCapability(o.getOrganisationCapability())
                        .build()
            );
        }));

        return orgs.values().stream().toList();
    }

    public static List<ItemEntity> processToItemEntity (List<Organisation> orgs){
        Map<String, ItemEntity> items = new HashMap<>();
        orgs.forEach(o -> o.getItems().forEach(i -> {
            if (!items.containsKey(i.getDetailedItemId())){
                items.put(i.getDetailedItemId(), i.toEntity());
            }
            items.get(i.getDetailedItemId()).getOrganizations().add(
                    o.entityBuilder()
                            .setCapabilityType(i.getCapabilityType())
                            .setOrganisationCapability(i.getOrganisationCapability())
                            .setValidationDate(i.getValidationDate())
                            .build()
            );
        }));

        return items.values().stream().toList();
    }

}
