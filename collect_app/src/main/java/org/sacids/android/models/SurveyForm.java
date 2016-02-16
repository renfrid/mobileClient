package org.sacids.android.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Godluck Akyoo on 1/29/2016.
 */

public class SurveyForm implements Parcelable {

    private long id;
    private String displayName;
    private String submissionUri;
    private String instanceFilePath;
    private String jrFormId;
    private String jrVersion;
    private String status;
    private boolean canEditWhenComplete;
    private Date lastStatusChangeDate;
    private String displaySubText;

    public SurveyForm() {
    }


    public SurveyForm(Parcel in) {
        id = in.readLong();
        displayName = in.readString();
        submissionUri = in.readString();
        instanceFilePath = in.readString();
        jrFormId = in.readString();
        jrVersion = in.readString();
        status = in.readString();
        canEditWhenComplete = in.readByte() != 0;
        lastStatusChangeDate = new Date(in.readLong());
        displaySubText = in.readString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSubmissionUri() {
        return submissionUri;
    }

    public void setSubmissionUri(String submissionUri) {
        this.submissionUri = submissionUri;
    }

    public String getInstanceFilePath() {
        return instanceFilePath;
    }

    public void setInstanceFilePath(String instanceFilePath) {
        this.instanceFilePath = instanceFilePath;
    }

    public String getJrFormId() {
        return jrFormId;
    }

    public void setJrFormId(String jrFormId) {
        this.jrFormId = jrFormId;
    }

    public String getJrVersion() {
        return jrVersion;
    }

    public void setJrVersion(String jrVersion) {
        this.jrVersion = jrVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCanEditWhenComplete() {
        return canEditWhenComplete;
    }

    public void setCanEditWhenComplete(boolean canEditWhenComplete) {
        this.canEditWhenComplete = canEditWhenComplete;
    }

    public Date getLastStatusChangeDate() {
        return lastStatusChangeDate;
    }

    public void setLastStatusChangeDate(Date lastStatusChangeDate) {
        this.lastStatusChangeDate = lastStatusChangeDate;
    }

    public String getDisplaySubText() {
        return displaySubText;
    }

    public void setDisplaySubText(String displaySubText) {
        this.displaySubText = displaySubText;
    }

    @Override
    public String toString() {
        return "SurveyForm{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", submissionUri='" + submissionUri + '\'' +
                ", instanceFilePath='" + instanceFilePath + '\'' +
                ", jrFormId='" + jrFormId + '\'' +
                ", jrVersion='" + jrVersion + '\'' +
                ", status='" + status + '\'' +
                ", canEditWhenComplete=" + canEditWhenComplete +
                ", lastStatusChangeDate=" + lastStatusChangeDate +
                ", displaySubText='" + displaySubText + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(displayName);
        dest.writeString(submissionUri);
        dest.writeString(instanceFilePath);
        dest.writeString(jrFormId);
        dest.writeString(jrVersion);
        dest.writeString(status);
        dest.writeByte((byte) (canEditWhenComplete ? 1 : 0));
        dest.writeLong((lastStatusChangeDate != null) ? lastStatusChangeDate.getTime() : 0);
        dest.writeString(displaySubText);
    }

    public static final Parcelable.Creator<SurveyForm> CREATOR = new Parcelable.Creator<SurveyForm>() {

        @Override
        public SurveyForm createFromParcel(Parcel source) {
            return new SurveyForm(source);
        }

        @Override
        public SurveyForm[] newArray(int size) {
            return new SurveyForm[size];
        }
    };
}
