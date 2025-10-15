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
}
