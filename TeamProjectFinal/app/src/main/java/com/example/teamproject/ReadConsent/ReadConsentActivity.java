package com.example.teamproject.ReadConsent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamproject.MyToken;
import com.example.teamproject.R;
import com.example.teamproject.RetrofitService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReadConsentActivity extends AppCompatActivity {

    private String TAG = "AgreementList";

    private ArrayList <ConsentData> mArrayList;
    private ReadConsentAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private String myURL = "http://34.64.150.54/";
    private String blockchainURL = "http://34.97.209.205:4000/";
    RetrofitService retrofitService;
    Retrofit retrofit;

    JSONArray jsonArray1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_consent);

        // 레트로핏 객체 선언
        retrofit = new Retrofit.Builder()
                .baseUrl(myURL)
                .build();

        // 레트로핏 서비스 객체 선언
        retrofitService = retrofit.create(RetrofitService.class);

        mArrayList = new ArrayList<>();

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_consent);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter= new ReadConsentAdapter(this,mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        jsonArray1 = new JSONArray();

        getUserkey();   //유저의 documentkey 가져오기.

        //getConsent();   //해당 유저의 동의서 내역 가져오기
        clickListener();        //클릭리스너

    }


    private void clickListener(){
        mAdapter.setOnItemClickListener(new ReadConsentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(ReadConsentActivity.this, ViewConsentActivity.class);
                intent.putExtra("patname", mArrayList.get(position).getPatName());
                intent.putExtra("time", mArrayList.get(position).getTime());
                intent.putExtra("docname", mArrayList.get(position).getDocName());
                intent.putExtra("papername", mArrayList.get(position).getPapaername());
                startActivity(intent);

            }
        });
    }


    private void getUserkey(){

        //userid를 인텐트로 받아온다.
        SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
        String userid = sharedPreferences.getString("UID","");
        Log.d("쉐어드 저장", userid);

        //retrofit으로 아이디를 넘겨 documentKey를 받아온다. JSON으로
        getDocumentkey(userid);

    }


    private void getDocumentkey(String userid){


        Call<ResponseBody> call = retrofitService.getDocumentKey(userid);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "Response: "+response);
                //동의서 내용 받아 온 후 파싱
                String result = null;
                try {
                    result = response.body().string();
                    Log.d(TAG, "GETagreement: "+result);

                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("documentkey");
                    for(int i=0;i<jsonArray.length();i++){

                        JSONObject item = jsonArray.getJSONObject(i);
                        String documentkey = item.getString("documentkey");
                        Log.d("두개 가져와집니까?! : ", documentkey);

                        //여기서 해당 인물의 agreement 하나씩 가져오기.(블록체인에서)

                        //받아 온 데이터 리사이클러뷰에 추가. 단 +1 에러 때문에 에러 처리함.
                        int length = jsonArray.length();
                        int lengthMinus = jsonArray.length()-1;
                        if(length == 1){
                            jsonArray1.put(documentkey);
                        }else{
                            if(i == 0){
                                jsonArray1.put(documentkey);
                            }
                            if(i == lengthMinus) {
                                jsonArray1.put(documentkey + i);
                            }
                        }

                    }

                    //key 모두 넣으면 실행
                    getConsentFromBlockchain();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }



    private void getConsentFromBlockchain(){

//        JSONArray jsonArray = new JSONArray();
//        jsonArray.put("peer0.org1.example.com");


        // 헤더에 토큰을 넣으려고 레트로핏을 네트워크 층에서 가로챈 다음에 수정해줘야함.
        // okhttp 클라이언트 빌더를 선언해주고 인터셉트 함수 안에서 필요한 헤더를 추가한다. 메소드나 바디는 기존 요청의 것을 사용한다.
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("authorization", "Bearer "+ MyToken.getmToken())
                        .header("content-type", "application/json")
                        .method(original.method(), original.body())
                        .build();

                Log.d("토큰!! : ", MyToken.getmToken());

                return chain.proceed(request);
            }
        });
        OkHttpClient client = httpClient.build();

        // 레트로핏 객체와 서비스 객체 선언은 기존과 같다. 한가지 추가된 것은 헤더 변경을 위해 위에서 만들어준 통신 클라이언트를 레트로핏에 넣어주는 것
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(blockchainURL)
                .client(client)
                .build();

        String json = String.valueOf(jsonArray1);
        String jsonCommaEdit = json.replace(",","%2C");


        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        String fcn = null;
        if(jsonArray1.length()==1){
            fcn = "query";
        }else{
            fcn = "queryRange";
        }

        Call<ResponseBody> call = retrofitService.getDocument(fcn, "peer0.org1.example.com", String.valueOf(jsonArray1));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: "+response);
                try {
                    // 서버에서 보낸 값을 스트링에 담음.
                    String result = response.body().string();
                    if(response.isSuccessful()){
                        Log.d(TAG, "result is successful: "+result);

                        if(jsonArray1.length() == 1){

                            JSONObject jsonObject = new JSONObject(result);
                            String docname= jsonObject.getString("doctor");
                            String patname= jsonObject.getString("patient");
                            String papername= jsonObject.getString("document_name");
                            String time= jsonObject.getString("timestamp");

                            //데이터 넣어주기
                            ConsentData consentData =new ConsentData();
                            consentData.setDocName(docname);
                            consentData.setPatName(patname);
                            consentData.setTime(time);
                            consentData.setPapaername(papername);
                            mArrayList.add(consentData);

                            mAdapter.notifyDataSetChanged();

                        }else{

                            JSONArray jsonArray = new JSONArray(result);
                            for(int i=0;i<jsonArray.length();i++) {

                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                JSONObject jsonObject2 = jsonObject1.getJSONObject("Record");
                                String docname= jsonObject2.getString("doctor");
                                String patname= jsonObject2.getString("patient");
                                String papername= jsonObject2.getString("document_name");
                                String time= jsonObject2.getString("timestamp");

                                //데이터 넣어주기
                                ConsentData consentData =new ConsentData();
                                consentData.setDocName(docname);
                                consentData.setPatName(patname);
                                consentData.setTime(time);
                                consentData.setPapaername(papername);
                                mArrayList.add(consentData);

                                mAdapter.notifyDataSetChanged();
                            }

                        }



                    }else{
                        Log.d(TAG, "unsuccessful: "+result);
                        Toast.makeText(getApplicationContext(), "서버와 통신이 좋지 않아요.", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t);
            }
        });
    }


    private void getConsent(){

        String userid = "vanoman";

        Call<ResponseBody> call = retrofitService.getAgreement(userid);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "Response: "+response);
                //동의서 내용 받아 온 후 파싱
                String result = null;
                try {
                    result = response.body().string();
                    Log.d(TAG, "GETagreement: "+result);

                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("agreement");
                    for(int i=0;i<jsonArray.length();i++){

                        JSONObject item = jsonArray.getJSONObject(i);
                        String num = item.getString("num");
                        String time = item.getString("time");
                        String docname = item.getString("docname");
                        String patname = item.getString("patname");
                        String content = item.getString("content");
                        String papername = item.getString("papername");
                        String paperver = item.getString("paperver");
                        String userid = item.getString("userid");

                        //데이터 넣어주기
                        ConsentData consentData =new ConsentData();
                        consentData.setDocName(docname);
                        consentData.setPatName(patname);
                        consentData.setTime(time);
                        consentData.setContent(content);
                        consentData.setPapaername(papername);
                        consentData.setPaperver(paperver);
                        mArrayList.add(consentData);

                        mAdapter.notifyDataSetChanged();

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
