package com.example.teamproject.ReadConsent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamproject.MyToken;
import com.example.teamproject.R;
import com.example.teamproject.RetrofitService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ViewConsentActivity extends AppCompatActivity {

    private String TAG = "Agreement";
    private String blockchainURL = "http://34.97.209.205:4000/";
    String patname, docname, time, papername, papaerver, documentkey;
    TextView view_patname, view_docname, view_time, view_agreement, view_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_consent);

        //동의 당시 문서 정보 받아오기.
        Intent intent = getIntent();
        patname = intent.getExtras().getString("patname");
        docname = intent.getExtras().getString("docname");
        time = intent.getExtras().getString("time");
        papername = intent.getExtras().getString("papername");

        String timeEdited = time.substring(0,4)+"."+time.substring(4,6)+"."+time.substring(6,8)+" "+time.substring(8,10)+":"+time.substring(10,12)+":"+time.substring(12,14);

        documentkey = patname+time;

        //받은 데이터 수술동의서 폼에 넣어주기
        view_patname = findViewById(R.id.view_patname);
        view_docname = findViewById(R.id.view_docname);
        view_time = findViewById(R.id.view_time);
        view_agreement = findViewById(R.id.view_agreementAndVer);
        view_content = findViewById(R.id.view_content);

        view_agreement = findViewById(R.id.view_agreementAndVer);
        view_patname = findViewById(R.id.view_patname);
        view_docname = findViewById(R.id.view_docname);
        view_time = findViewById(R.id.view_time);

        view_agreement.setText(papername);
        view_patname.setText(patname);
        view_docname.setText(docname);
        view_time.setText(timeEdited);

        //intent로 document_name을 받아오면 이를 통해서 문서 양식 가져오기
        getConsentFromBlockchain();

    }

    private void getConsentFromBlockchain(){

//        JSONArray jsonArray = new JSONArray();
//        jsonArray.put("peer0.org1.example.com");

        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.put("T1");


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

                return chain.proceed(request);
            }
        });
        OkHttpClient client = httpClient.build();

        // 레트로핏 객체와 서비스 객체 선언은 기존과 같다. 한가지 추가된 것은 헤더 변경을 위해 위에서 만들어준 통신 클라이언트를 레트로핏에 넣어주는 것
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(blockchainURL)
                .client(client)
                .build();

        String keyEncoded = null;
        try {
            keyEncoded = URLEncoder.encode(String.valueOf(jsonArray1), "UTF-8");
            Log.d("들어가는값: ",String.valueOf(jsonArray1));
            Log.d("인코딩!!: ",keyEncoded);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d("레트로핏 전입니다. ", "레트로핏 전입니다. ");

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ResponseBody> call = retrofitService.getOriginal("query", "peer0.org1.example.com", String.valueOf(jsonArray1));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: "+response);
                try {
                    // 서버에서 보낸 값을 스트링에 담음.
                    String result = response.body().string();
                    if(response.isSuccessful()){
                        Log.d(TAG, "result is successful: "+result);

                        //받은 자료를 parsing 해서 레이아웃에 띄워주기.
                        JSONObject jsonObject = new JSONObject(result);
                        String content = jsonObject.getString("text");
                        view_content.setText(content);


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
}
