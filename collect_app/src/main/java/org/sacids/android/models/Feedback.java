package org.sacids.android.models;

import java.util.Date;

/**
 * Created by Godluck Akyoo on 2/2/2016.
 */
public class Feedback {

    private long id;
    private long userId;
    private String formId;
    private String message;
    private Date date;
    private String viewedBy;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
                ", date=" + date +
                ", viewedBy='" + viewedBy + '\'' +
                '}';
    }
}
