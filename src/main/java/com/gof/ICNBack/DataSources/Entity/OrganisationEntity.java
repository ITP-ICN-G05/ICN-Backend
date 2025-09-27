package com.gof.ICNBack.DataSources.Entity;

import com.gof.ICNBack.Entity.Organisation;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

public class OrganisationEntity {
    @Field("Organisation Capability")
    private String organisationCapability;

    @Field("Organisation: Organisation Name")
    private String organisationName;

    @Field("Organisation: Organisation ID")
    private String organisationId;

    @Field("Capability Type")
    private String capabilityType;

    @Field("Validation Date")
    private String validationDate;

    @Field("Organisation: Billing Street")
    private String billingStreet;

    @Field("Organisation: Billing City")
    private String billingCity;

    @Field("Organisation: Billing State/Province")
    private String billingStateProvince;

    @Field("Organisation: Billing Zip/Postal Code")
    private String billingZipPostalCode;

    @Field("Organisation: Coord")
    private GeoJsonPoint coord;


    public OrganisationEntity() {}

    public OrganisationEntity(String organisationCapability,
                              String organisationName,
                              String organisationId,
                              String capabilityType,
                              String validationDate,
                              String billingStreet,
                              String billingCity,
                              String billingStateProvince,
                              String billingZipPostalCode,
                              GeoJsonPoint coord) {
        this.organisationCapability = organisationCapability;
        this.organisationName = organisationName;
        this.organisationId = organisationId;
        this.capabilityType = capabilityType;
        this.validationDate = validationDate;
        this.billingStreet = billingStreet;
        this.billingCity = billingCity;
        this.billingStateProvince = billingStateProvince;
        this.billingZipPostalCode = billingZipPostalCode;
        this.coord = coord;
    }

    public String getOrganisationCapability() {
        return organisationCapability;
    }

    public void setOrganisationCapability(String organisationCapability) {
        this.organisationCapability = organisationCapability;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    public String getCapabilityType() {
        return capabilityType;
    }

    public void setCapabilityType(String capabilityType) {
        this.capabilityType = capabilityType;
    }

    public String getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(String validationDate) {
        this.validationDate = validationDate;
    }

    public String getBillingStreet() {
        return billingStreet;
    }

    public void setBillingStreet(String billingStreet) {
        this.billingStreet = billingStreet;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public String getBillingStateProvince() {
        return billingStateProvince;
    }

    public void setBillingStateProvince(String billingStateProvince) {
        this.billingStateProvince = billingStateProvince;
    }

    public String getBillingZipPostalCode() {
        return billingZipPostalCode;
    }

    public void setBillingZipPostalCode(String billingZipPostalCode) {
        this.billingZipPostalCode = billingZipPostalCode;
    }

    public Organisation toDomain(){
        return new Organisation(
                this.organisationName,
                this.organisationId,
                new ArrayList<>(),
                this.billingStreet,
                this.billingCity,
                this.billingStateProvince,
                this.billingZipPostalCode,
                this.coord
        );
    }

    @Override
    public String toString() {
        return "Organization{" +
                "organisationCapability='" + organisationCapability + '\'' +
                ", organisationName='" + organisationName + '\'' +
                ", organisationId='" + organisationId + '\'' +
                ", capabilityType='" + capabilityType + '\'' +
                ", validationDate='" + validationDate + '\'' +
                ", billingStreet='" + billingStreet + '\'' +
                ", billingCity='" + billingCity + '\'' +
                ", billingStateProvince='" + billingStateProvince + '\'' +
                ", billingZipPostalCode='" + billingZipPostalCode + '\'' +
                '}';
    }
}
