package com.gof.ICNBack.Entity;

import com.gof.ICNBack.DataSources.Entity.OrganisationEntity;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.ArrayList;
import java.util.List;

public class Organisation {
    private String _id;
    private String name;
    private ArrayList<Item> items;
    private String street;
    private String city;
    private String state;
    private String zip;
    private GeoJsonPoint coord;

    public Organisation(){}

    public Organisation(String _id, String name, ArrayList<Item> items, String street, String city, String state, String zip, GeoJsonPoint coord) {
        this._id = _id;
        this.name = name;
        this.items = items;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.coord = coord;
    }

    public OrganisationEntityBuilder entityBuilder(){
        return new OrganisationEntityBuilder(
                name,
                _id,
                street,
                city,
                state,
                zip,
                coord
        );
    }

    public OrganisationCard toCard(){
        return new OrganisationCard(
                this.name,
                this.street,
                this.city,
                this.state,
                this.zip,
                this.items,
                this.coord.getX(),
                this.coord.getY()
                );
    }

    public String buildAddress() {
        return street + " " + city + " " + state + " " + zip;
    }

    public double getLatitude(){
        return coord.getY();
    }
    public double getLongitude(){
        return coord.getX();
    }


    public static class OrganisationCard{
        private String name;
        private String street;
        private String city;
        private String state;
        private String zip;
        private ArrayList<Item> items;
        private Double longitude;
        private Double latitude;


        public OrganisationCard(String name, String street, String city, String state, String zip, ArrayList<Item> items, Double longitude, Double latitude) {
            this.name = name;
            this.street = street;
            this.city = city;
            this.state = state;
            this.zip = zip;
            this.items = items;
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public String getName() {
            return name;
        }

        public String getStreet() {
            return street;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getZip() {
            return zip;
        }

        public ArrayList<Item> getItems() {
            return items;
        }

        public Double getLongitude() {
            return longitude;
        }

        public Double getLatitude() {
            return latitude;
        }
    }

    public static class OrganisationEntityBuilder{
        private String organisationCapability;
        private String organisationName;
        private String organisationId;
        private String capabilityType;
        private String validationDate;
        private String billingStreet;
        private String billingCity;
        private String billingStateProvince;
        private String billingZipPostalCode;
        private GeoJsonPoint coord;

        public OrganisationEntityBuilder(String organisationName, String organisationId, String billingStreet, String billingCity, String billingStateProvince, String billingZipPostalCode, GeoJsonPoint coord) {
            this.organisationName = organisationName;
            this.organisationId = organisationId;
            this.billingStreet = billingStreet;
            this.billingCity = billingCity;
            this.billingStateProvince = billingStateProvince;
            this.billingZipPostalCode = billingZipPostalCode;
            this.coord = coord;
        }

        public OrganisationEntityBuilder setOrganisationCapability(String organisationCapability) {
            this.organisationCapability = organisationCapability;
            return this;
        }

        public OrganisationEntityBuilder setCapabilityType(String capabilityType) {
            this.capabilityType = capabilityType;
            return this;
        }

        public OrganisationEntityBuilder setValidationDate(String validationDate) {
            this.validationDate = validationDate;
            return this;
        }

        public OrganisationEntity build(){
            return new OrganisationEntity(
                    organisationCapability,
                    organisationName,
                    organisationId,
                    capabilityType,
                    validationDate,
                    billingStreet,
                    billingCity,
                    billingStateProvince,
                    billingZipPostalCode,
                    coord
            );
        }
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String get_id() {
        return _id;
    }
    public List<Item> getItems() {
        return items;
    }
    public void setCoord(GeoJsonPoint coord) {
        this.coord = coord;
    }
    public GeoJsonPoint buildCoord() {
        return coord;
    }
}