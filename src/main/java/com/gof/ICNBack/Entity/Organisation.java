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

    public OrganisationEntity toEntity(){
        return new OrganisationEntity(
                name,
                _id,
                street,
                city,
                state,
                zip,
                items.stream().map(i ->
                        new OrganisationEntity.AssociatedItem(
                                i.getDetailedItemId(),
                                i.getOrganisationCapability(),
                                i.getCapabilityType(),
                                i.getValidationDate())
                ).toList(),
                coord
        );
    }

    public OrganisationCard toCard(){
        return new OrganisationCard(
                this._id,
                this.name,
                this.street,
                this.city,
                this.state,
                this.zip
                );
    }

    public String getAddress() {
        return street + " " + city + " " + state + " " + zip;
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

    public GeoJsonPoint getCoord() {
        return coord;
    }

    public static class OrganisationCard{
        private String id;
        private String name;
        private String street;
        private String city;
        private String state;
        private String zip;


        public OrganisationCard(String id, String name, String street, String city, String state, String zip) {
            this.id = id;
            this.name = name;
            this.street = street;
            this.city = city;
            this.state = state;
            this.zip = zip;
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

        public String getId() {
            return id;
        }
    }
}
