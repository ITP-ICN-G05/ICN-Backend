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

    private int Subtotal;

    public Organisation(){}

    public Organisation(String id, String detailedItemID, String itemName, String itemID, String detailedItemName, String sectorMappingID, String sectorName, int subtotal) {
        _id = id;
        this.detailedItemID = detailedItemID;
        this.itemName = itemName;
        this.itemID = itemID;
        this.detailedItemName = detailedItemName;
        this.sectorMappingID = sectorMappingID;
        this.sectorName = sectorName;
        Subtotal = subtotal;
    }


    public OrganisationCard toCard(){
        return new OrganisationCard();
    }
    public static class OrganisationCard{

    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setDetailedItemID(String detailedItemID) {
        this.detailedItemID = detailedItemID;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public void setDetailedItemName(String detailedItemName) {
        this.detailedItemName = detailedItemName;
    }

    public void setSectorMappingID(String sectorMappingID) {
        this.sectorMappingID = sectorMappingID;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public void setSubtotal(int subtotal) {
        Subtotal = subtotal;
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
