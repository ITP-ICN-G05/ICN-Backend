package com.gof.ICNBack.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class Organisation {

    @Id
    private String _id;

    @Field("Detailed Item ID")
    private String detailedItemID;

    @Field("Item Name")
    private String itemName;

    @Field("Item ID")
    private String itemID;

    @Field("Detailed Item Name")
    private String detailedItemName;

    @Field("Sector Mapping ID")
    private String sectorMappingID;

    @Field("Sector Name")
    private String sectorName;

    @Field("Organisation: Billing Street")
    private String street;
    @Field("Organisation: Billing City")
    private String city;

    @Field("Organisation: Billing State/Province")
    private String state;

    @Field("Organisation: Billing Zip/Postal Code")
    private String zip;

    private int Subtotal;

    public Organisation(){}

    public Organisation(String _id,
                        String detailedItemID,
                        String itemName,
                        String itemID,
                        String detailedItemName,
                        String sectorMappingID,
                        String sectorName,
                        String street,
                        String city,
                        String state,
                        String zip,
                        int subtotal) {
        this._id = _id;
        this.detailedItemID = detailedItemID;
        this.itemName = itemName;
        this.itemID = itemID;
        this.detailedItemName = detailedItemName;
        this.sectorMappingID = sectorMappingID;
        this.sectorName = sectorName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        Subtotal = subtotal;
    }

    public OrganisationCard toCard(){
        return new OrganisationCard();
    }

    public String getAddress() {
        return city + street + state + zip;
    }

    public static class OrganisationCard{

    }


    public String get_id() {
        return _id;
    }

    public String getDetailedItemID() {
        return detailedItemID;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemID() {
        return itemID;
    }

    public String getDetailedItemName() {
        return detailedItemName;
    }

    public String getSectorMappingID() {
        return sectorMappingID;
    }

    public String getSectorName() {
        return sectorName;
    }

    public int getSubtotal() {
        return Subtotal;
    }
}
