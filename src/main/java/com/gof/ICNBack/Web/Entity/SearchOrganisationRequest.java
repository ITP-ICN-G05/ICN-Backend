package com.gof.ICNBack.Web.Entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Validated
public class SearchOrganisationRequest {
    @Min(-180) @Max(180)
    private double startLongitude;

    @Min(-90) @Max(90)
    private double startLatitude;

    @Min(-180) @Max(180)
    private double endLongitude;

    @Min(-90) @Max(90)
    private double endLatitude;

    @Size(max = 1000)
    private String filterParameters;

    @Size(max = 100)
    private String searchString;

    @Min(0)
    private Integer skip = 0;

    @Min(0) @Max(1000)
    private Integer limit = 100;

    public SearchOrganisationRequest() {
    }

    public SearchOrganisationRequest(double startLongitude, double startLatitude, double endLongitude, double endLatitude, String filterParameters, String searchString, Integer skip, Integer limit) {
        this.startLongitude = startLongitude;
        this.startLatitude = startLatitude;
        this.endLongitude = endLongitude;
        this.endLatitude = endLatitude;
        this.filterParameters = filterParameters;
        this.searchString = searchString;
        this.skip = skip;
        this.limit = limit;
    }

    public Map<String, String> getFilter() throws JsonProcessingException {
        if (filterParameters == null || filterParameters.trim().isEmpty()) {
            return new HashMap<>();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(filterParameters, new TypeReference<>() {
        });
    }

    public SearchOrganisationRequest copy(){
        return new SearchOrganisationRequest(
            this.startLongitude,
            this.startLatitude,
            this.endLongitude,
            this.endLatitude,
            this.filterParameters,
            this.searchString,
            this.skip,
            this.limit
        );
    }


    public double getStartLongitude() {
        return startLongitude;
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public String getFilterParameters() {
        return filterParameters;
    }

    public String getSearchString() {
        return searchString;
    }

    public Integer getSkip() {
        return skip;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public void setFilterParameters(String filterParameters) {
        this.filterParameters = filterParameters;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
