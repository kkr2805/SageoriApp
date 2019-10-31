package com.hgtech.sageoriapp;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.FieldMap;
import retrofit2.http.Url;

import java.util.List;
import java.util.HashMap;

public interface SageoriAPI {
    @GET("/api/get_members")
    Call<List<MemberItem>> getMembers();

    @POST("/api/create_member") 
    Call<SageoriResult> createMember(@Body HashMap<String, String> param);

    @POST("/api/update_member") 
    Call<SageoriResult> updateMember(@Body HashMap<String, String> param);

    @POST("/api/delete_member") 
    Call<SageoriResult> deleteMember(@Body HashMap<String, String> param);

    @GET("/api/get_machines")
    Call<List<Integer>> getMachines();

    @GET("/api/get_publishes")
    Call<List<PublishItem>> getPublishes();

    @Multipart
    @POST("/api/create_publish") 
    Call<SageoriResult> createPublishItem(@PartMap HashMap<String, RequestBody> param);

    @Multipart
    @POST("/api/update_publish") 
    Call<SageoriResult> updatePublishItem(@PartMap HashMap<String, RequestBody> param);

    @POST("/api/delete_publish") 
    Call<SageoriResult> deletePublishItem(@Body HashMap<String, String> param);

    @GET
    Call<ResponseBody> getImageFile(@Url String url);

    @GET("/api/get_return_items")
    Call<List<ReturnItem>> getReturnItems();

    @POST("/api/create_return_item")
    Call<SageoriResult> createReturnItem(@Body HashMap<String, String> param);

    @POST("/api/update_return_item")
    Call<SageoriResult> updateReturnItem(@Body HashMap<String, String> param);

    @POST("/api/delete_return_item")
    Call<SageoriResult> deleteReturnItem(@Body HashMap<String, String> param);

    @GET("/api/get_score_items")
    Call<List<ScoreItem>> getScoreItems();

    @GET("/api/get_exchanges")
    Call<List<ExchageItem>> getExchageItems(@Query("id") int memberID);

    @POST("/api/create_exchange")
    Call<SageoriResult> createExchageItem(@Body HashMap<String, String> param);

    @POST("/api/update_exchange")
    Call<SageoriResult> updateExchageItem(@Body HashMap<String, String> param);

    @POST("/api/delete_exchange")
    Call<SageoriResult> deleteExchangeItem(@Body HashMap<String, String> param);
}
