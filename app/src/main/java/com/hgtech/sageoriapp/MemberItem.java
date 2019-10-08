package com.hgtech.sageoriapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemberItem {
    @SerializedName("MEMBER_ID")
    @Expose
    public int No;

    @SerializedName("NAME")
    @Expose
    public String Name;

    @SerializedName("HP")
    @Expose
    public String HP;
}
