package com.gof.ICNBack.DataSources.Entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Email")
public class EmailRecordEntity {
    @Id
    private String _id;
    private String code;
    private String email;
    @CreatedDate
    private Date createdDate;

    public EmailRecordEntity() {
    }

    public EmailRecordEntity(String _id, String code, String email, Date createdDate) {
        this._id = _id;
        this.code = code;
        this.email = email;
        this.createdDate = createdDate;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedTime() {
        return createdDate;
    }
}
