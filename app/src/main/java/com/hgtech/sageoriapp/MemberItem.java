package com.hgtech.sageoriapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MemberItem {
    @SerializedName("MEMBER_ID")
    @Expose
    public int ID;

    @SerializedName("NAME")
    @Expose
    public String Name;

    @SerializedName("HP")
    @Expose
    public String HP;
    
    @SerializedName("CREATED_DATE")
    @Expose
    public Date Date;

    @Override
    public String toString(){
        return Name;
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof MemberItem))
            return false;

        MemberItem other = (MemberItem)o;
        return other.ID == ID;
    }
}
