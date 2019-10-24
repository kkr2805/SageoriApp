package com.hgtech.sageoriapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExchageItem {
    @SerializedName("EXCHAGE_ID")
    @Expose
    public int ID;

    @SerializedName("MEMBER_ID")
    @Expose
    public int MemberID;

    @SerializedName("MEMBER_NAME")
    @Expose
    public String MemberName;

    @SerializedName("EXCHAGE_VALUE")
    @Expose
    public int ExchageValue;

    @SerializedName("CREATED_DATE")
    @Expose
    public java.util.Date Date;
}
