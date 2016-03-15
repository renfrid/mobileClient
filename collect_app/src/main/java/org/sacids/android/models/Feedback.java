package org.sacids.android.models;

import java.util.Date;

/**
 * Created by Renfrid Ngolongolo on 2/2/2016.
 */
public class Feedback {

    private long id;
    private long userId;
    private String userName;
    private String formId;
    private String message;
    private String dateCreated;
    private String viewedBy;

    //empty Constructor
    public Feedback(){

    }


    //Another constructor
    public Feedback(long _id, String _formId, String _message, String _date){
        this.id = _id;
        this.formId = _formId;
        this.message = _message;
        this.dateCreated = _date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getViewedBy() {
        return viewedBy;
    }

    public void setViewedBy(String viewedBy) {
        this.viewedBy = viewedBy;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", userId=" + userId +
                ", formId='" + formId + '\'' +
                ", message='" + message + '\'' +
                ", dateCreated=" + dateCreated +
                ", viewedBy='" + viewedBy + '\'' +
                '}';
    }
}
