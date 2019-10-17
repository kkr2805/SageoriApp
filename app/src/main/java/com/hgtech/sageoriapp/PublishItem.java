package com.hgtech.sageoriapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PublishItem {
    @SerializedName("PUBLISH_ID")
    @Expose
    public int ID;

    @SerializedName("MACHINE_ID")
    @Expose
    public int MachineID;

    @SerializedName("MEMBER_ID")
    @Expose
    public int MemberID;

    @SerializedName("MEMBER_NAME")
    @Expose
    public String MemberName;

    @SerializedName("CREDIT")
    @Expose
    public int Credit;

    @SerializedName("BANK")
    @Expose
    public int Bank;

    @SerializedName("CREATED_DATE")
    @Expose
    public java.util.Date Date;
}
