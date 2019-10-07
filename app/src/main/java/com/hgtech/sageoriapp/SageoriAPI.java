package com.hgtech.sageoriapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

import java.util.List;

public interface SageoriAPI {
    @GET("/api/get_members")
    Call<List<MemberItem>> getMembers();
}
