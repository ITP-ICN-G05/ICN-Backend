package com.gof.ICNBack.DataSources.Entity;

import com.gof.ICNBack.Entity.Item;
import org.springframework.data.mongodb.core.mapping.Document;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "Organisation")
public class ItemEntity {

    @Id
    private String id;

    @Field("Detailed Item ID")
    private String detailedItemId;

    @Field("Item Name")
    private String itemName;

    @Field("Item ID")
    private String itemId;

    @Field("Detailed Item Name")
    private String detailedItemName;

    @Field("Sector Mapping ID")
    private String sectorMappingId;

    @Field("Sector Name")
    private String sectorName;

    @Field("Subtotal")
    private Integer subtotal;

    @Field("Organizations")
    private List<OrganisationEntity> organizations;

    @Field("Geocoded")
    private boolean geocoded;

    public ItemEntity() {}

    public ItemEntity(String id, String detailedItemId, String itemName, String itemId,
                String detailedItemName, String sectorMappingId, String sectorName,
                Integer subtotal, List<OrganisationEntity> organizations) {
        this.id = id;
        this.detailedItemId = detailedItemId;
        this.itemName = itemName;
        this.itemId = itemId;
        this.detailedItemName = detailedItemName;
        this.sectorMappingId = sectorMappingId;
        this.sectorName = sectorName;
        this.subtotal = subtotal;
        this.organizations = organizations;
    }

    // Getter & Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetailedItemId() {
        return detailedItemId;
    }

    public void setDetailedItemId(String detailedItemId) {
        this.detailedItemId = detailedItemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDetailedItemName() {
        return detailedItemName;
    }

    public void setDetailedItemName(String detailedItemName) {
        this.detailedItemName = detailedItemName;
    }

    public String getSectorMappingId() {
        return sectorMappingId;
    }

    public void setSectorMappingId(String sectorMappingId) {
        this.sectorMappingId = sectorMappingId;
    }

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public Integer getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Integer subtotal) {
        this.subtotal = subtotal;
    }

    public List<OrganisationEntity> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<OrganisationEntity> organizations) {
        this.organizations = organizations;
    }

    public boolean isGeocoded() {
        return geocoded;
    }

    public void setGeocoded(boolean geocoded) {
        this.geocoded = geocoded;
    }

    public ItemBuilder domainBuilder(){
        return new ItemBuilder(
            id,
            detailedItemId,
            itemName,
            itemId,
            detailedItemName,
            sectorMappingId,
            sectorName,
            subtotal);
    }

    public static class ItemBuilder{
        private String id;
        private String detailedItemId;
        private String itemName;
        private String itemId;
        private String detailedItemName;
        private String sectorMappingId;
        private String sectorName;
        private Integer subtotal;
        private String capabilityType;
        private String validationDate;
        private String organisationCapability;

        public ItemBuilder(String id, String detailedItemId, String itemName, String itemId, String detailedItemName, String sectorMappingId, String sectorName, Integer subtotal) {
            this.id = id;
            this.detailedItemId = detailedItemId;
            this.itemName = itemName;
            this.itemId = itemId;
            this.detailedItemName = detailedItemName;
            this.sectorMappingId = sectorMappingId;
            this.sectorName = sectorName;
            this.subtotal = subtotal;
        }

        public ItemBuilder setCapabilityType(String capabilityType) {
            this.capabilityType = capabilityType;
            return this;
        }

        public ItemBuilder setValidationDate(String validationDate) {
            this.validationDate = validationDate;
            return this;
        }

        public ItemBuilder setOrganisationCapability(String organisationCapability) {
            this.organisationCapability = organisationCapability;
            return this;
        }

        public Item build(){
            return new Item(
                    id,
                    detailedItemId,
                    itemName,
                    itemId,
                    detailedItemName,
                    sectorMappingId,
                    sectorName,
                    subtotal,
                    capabilityType,
                    validationDate,
                    organisationCapability);
        }
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", detailedItemId='" + detailedItemId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemId='" + itemId + '\'' +
                ", detailedItemName='" + detailedItemName + '\'' +
                ", sectorMappingId='" + sectorMappingId + '\'' +
                ", sectorName='" + sectorName + '\'' +
                ", subtotal=" + subtotal +
                ", organizations=" + organizations +
                '}';
    }
}
