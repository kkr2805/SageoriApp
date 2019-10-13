package com.hgtech.sageoriapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SageoriResult {
    
    @SerializedName("result_code")
    @Expose
    public int resultCode;
    
    public SageoriResult() {
        this.resultCode = 0; 
    }

    public boolean isSuccess() {
        return (resultCode == 0); 
    }

}
