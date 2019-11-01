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

    public void updateExchange() {

        // Exchange Item 이 쿼리되었을 때에만 update 한다.
        // 쿼리되지 않은 상태에서는 서버에서 계산한 Score 값을 사용한다.
        if(exchageItemList.size() > 0) {
            int sum = 0;
            for (ExchageItem item: exchageItemList) {
                sum = sum + item.ExchageValue;
            }

            this.Exchange = sum;
        }

    }
}
