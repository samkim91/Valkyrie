package com.example.teamproject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitService {

    @FormUrlEncoded
    @POST("Login/requestLogin.php")
    Call<ResponseBody> requestLogin(
            @Field("id") String id, @Field("pwd") String pwd
    );

    @FormUrlEncoded
    @POST("Login/requestSignup.php")
    Call<ResponseBody> requestSignup(
            @Field("id") String id, @Field("pwd") String pwd, @Field("name") String name
    );

    @FormUrlEncoded
    @POST("afterauth.php")
    Call<ResponseBody> authPatient(
            @FieldMap HashMap<String, String> hashMap
            );

    @GET("getopinion.php")
    Call<ResponseBody> getOpinion(
            @Query("paperId") String paperId
    );

    //동의서 입력
    @FormUrlEncoded
    @POST("update_agreement.php")
    Call<ResponseBody> updateConsentForm(
            @FieldMap HashMap<String, String> hashMap
    );

    //동의서(양식) 내용 가져오기
    @GET("getconsentform.php")
    Call<ResponseBody> getConsent(
            @Query("surgeryName") String surgeryName
    );

    //동의서(작성된) 내용 가져오기
    @GET("getagreement.php")
    Call<ResponseBody> getAgreement(
            @Query("userid") String userid
    );

    //유저의 documnetkey가져오기
    @GET("getdocumentkey.php")
    Call<ResponseBody> getDocumentKey(
            @Query("userid") String userid
    );

    //블록체인 로그인
    @FormUrlEncoded
    @POST("users")
    Call<ResponseBody> loginRequest(
            @FieldMap HashMap<String, String> hashMap
    );

    //블록체인 문서저장
    @FormUrlEncoded
    @POST("channels/mychannel/chaincodes/mycc")
    Call<ResponseBody> createDocument(
            @Field("fcn") String fcn , @Field("peers") String peers, @Field("args") String args
            //@FieldMap HashMap<String, JSONArray> hashMap
    );

    //블록체인 동의서 가져오기
    @GET("channels/mychannel/chaincodes/mycc")
    Call<ResponseBody> getDocument(
            @Query(value = "fcn", encoded = true) String fcn , @Query(value = "peer", encoded = true) String peers, @Query(value = "args", encoded = true) String args
            //@FieldMap HashMap<String, JSONArray> hashMap
    );

    //블록체인 양식 가져오기
    @GET("channels/mychannel/chaincodes/mycc")
    Call<ResponseBody> getOriginal(
            @Query(value = "fcn", encoded = true) String fcn , @Query(value = "peer", encoded = true) String peers, @Query(value = "args", encoded = true) String args
            //@FieldMap HashMap<String, JSONArray> hashMap
    );

    // 사진을 보내기 위해 POST 방식에서 FormUrlEncoded 가 아닌 Multipart 방식을 사용. post가 여러가지 파트로 이루어져있다는 뜻.
    @Multipart
    @POST("uploadImage.php")
    Call<ResponseBody> uploadImage(@Part ArrayList<MultipartBody.Part> files);

    @GET("uploadDoctorKey.php")
    Call<ResponseBody> uploadDoctorKey(
            @Query("doctorname") String doctorName, @Query("uniquekey") String uniquekey
    );

    @GET("getDoctorKey.php")
    Call<ResponseBody> getDoctorKey(
            @Query("doctorname") String doctorName
    );

    //블록체인 양식 가져오기
    @GET("channels/mychannel/chaincodes/mycc")
    Call<ResponseBody> pushToBlock(
            @Query(value = "fcn", encoded = true) String fcn , @Query(value = "peer", encoded = true) String peers, @Query(value = "args", encoded = true) String args
    );

}
