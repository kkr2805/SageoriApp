package com.hgtech.sageoriapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ScoreItem {

    @SerializedName("MEMBER_ID")
    @Expose
    public int MemberID;

    @SerializedName("MEMBER_NAME")
    @Expose
    public String MemberName;

    @SerializedName("SCORE")
    @Expose
    public int Score;

    @SerializedName("RETURN_POINT")
    @Expose
    public int ReturnValue;

    @SerializedName("EXCHANGE")
    @Expose
    public int Exchange;

    @SerializedName("PUBLISH")
    @Expose
    public int Publish;

    public List<ExchageItem> exchageItemList;

    public int getRemains() {
        return Score + Publish - Exchange - ReturnValue;
    }
}
