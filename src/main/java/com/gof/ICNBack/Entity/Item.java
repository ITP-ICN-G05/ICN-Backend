package com.gof.ICNBack.Entity;

import com.gof.ICNBack.DataSources.Entity.ItemEntity;

import java.util.ArrayList;

public class Item {
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


    public Item(String id,
                String detailedItemId,
                String itemName,
                String itemId,
                String detailedItemName,
                String sectorMappingId,
                String sectorName,
                Integer subtotal,
                String capabilityType,
                String validationDate,
                String organisationCapability) {
        this.id = id;
        this.detailedItemId = detailedItemId;
        this.itemName = itemName;
        this.itemId = itemId;
        this.detailedItemName = detailedItemName;
        this.sectorMappingId = sectorMappingId;
        this.sectorName = sectorName;
        this.subtotal = subtotal;
        this.capabilityType = capabilityType;
        this.validationDate = validationDate;
        this.organisationCapability = organisationCapability;
    }

    public Item(){}

    public ItemEntity toEntity(){
        return new ItemEntity(
                id,
                detailedItemId,
                itemName,
                itemId,
                detailedItemName,
                sectorMappingId,
                sectorName,
                subtotal,
                new ArrayList<>()
        );
    }

    public String getId() {
        return id;
    }

    public String getDetailedItemId() {
        return detailedItemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public String getDetailedItemName() {
        return detailedItemName;
    }

    public String getSectorMappingId() {
        return sectorMappingId;
    }

    public String getSectorName() {
        return sectorName;
    }

    public Integer getSubtotal() {
        return subtotal;
    }

    public String getCapabilityType() {
        return capabilityType;
    }

    public String getValidationDate() {
        return validationDate;
    }

    public String getOrganisationCapability() {
        return organisationCapability;
    }
}
