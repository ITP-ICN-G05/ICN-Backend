package com.gof.ICNBack.DataSources.Utils;

import com.gof.ICNBack.DataSources.Entity.ItemEntity;
import com.gof.ICNBack.Entity.Organisation;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoUtils {
    public static Criteria createBoundingBoxCriteria(String fieldName,
                                                     Double minX, Double minY,
                                                     Double maxX, Double maxY) {
        // validation
        if (minX == null || minY == null || maxX == null || maxY == null) {
            throw new IllegalArgumentException("all coords should not be null");
        }

        // rearrange
        double lowerLeftX = Math.min(minX, maxX);
        double lowerLeftY = Math.min(minY, maxY);
        double upperRightX = Math.max(minX, maxX);
        double upperRightY = Math.max(minY, maxY);

        Box boundingBox = new Box(
                new Point(lowerLeftX, lowerLeftY),
                new Point(upperRightX, upperRightY)
        );

        return Criteria.where(fieldName).within(boundingBox);
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
