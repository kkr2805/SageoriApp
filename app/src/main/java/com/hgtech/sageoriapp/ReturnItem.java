package com.hgtech.sageoriapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReturnItem {
    @SerializedName("RETURN_ID")
    @Expose
    public int ID;

    @SerializedName("MACHINE_ID1")
    @Expose
    public int MachineID1;

    @SerializedName("MACHINE_ID2")
    @Expose
    public int MachineID2;

    @SerializedName("MEMBER_ID")
    @Expose
    public int MemberID;

    @SerializedName("MEMBER_NAME")
    @Expose
    public String MemberName;

    @SerializedName("RETURN_POINT")
    @Expose
    public int Retrun;

    @SerializedName("SERVICE")
    @Expose
    public int Service;

    @SerializedName("ONE_P_ONE")
    @Expose
    public int OnePone;

    @SerializedName("IMAGE_FILE")
    @Expose
    public String ImageFile;

    @SerializedName("CREATED_DATE")
    @Expose
    public java.util.Date Date;
}
