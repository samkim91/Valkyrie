package com.example.teamproject.CheckQRCode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamproject.R;
import com.example.teamproject.RetrofitService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.concurrent.Executor;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AuthPatientActivity extends AppCompatActivity {

    private String TAG = "AuthPatientActivity";
    private String URL = "http://34.64.150.54/";

    TextView tv_surgery_name, tv_doctor_name;
    Button auth_btn;

    Uri uriData;
    String value, paperId;

    // 생체 인식을 사용하기 위한 핸들러, 실행
    private Handler handler = new Handler();
    private Executor executor = new Executor() {
        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_patient);

        tv_doctor_name = findViewById(R.id.tv_doctorName);
        tv_surgery_name = findViewById(R.id.tv_surgery_name);
        auth_btn = findViewById(R.id.auth_btn);

        // URI로 보내준 값을 받는다.
        uriData = getIntent().getData();
        Log.d(TAG, "받은 값: "+uriData);
        // value라는 쿼리파라미터를 받아온다.
        value = uriData.getQueryParameter("value");
        Log.d(TAG, "value: "+value);

        // 파라미터로 받아온 값이 제이슨 형태이기 때문에, 제이슨객체를 선언해서 풀어준다.
        try {
            JSONArray jsonArray = new JSONArray(value);
            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
            JSONObject jsonObject2 = jsonArray.getJSONObject(1);

            tv_doctor_name.setText(jsonObject1.getString("name"));
            tv_surgery_name.setText(jsonObject2.getString("surgery_name"));

            // 동의서 번호 조회
            paperId = jsonObject2.getString("paperId");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        auth_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBiometricPrompt();

            }
        });

    }

    private void showBiometricPrompt(){
        // 본인인 다이얼로그에 띄울 정보들을 선언.. 지문인식이 안 될때 2차 수단을 이용하게 함.
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("본인인증")
                .setDeviceCredentialAllowed(true)
                .build();

        // 본인인증 다이얼로그 실행! 각 결과에 따라 값을 리턴
        BiometricPrompt biometricPrompt = new BiometricPrompt(AuthPatientActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.d(TAG, "onAuthenticationError:" +errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.d(TAG, "onAuthenticationSucceeded: "+result);
                afterAuth("agree");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.d(TAG, "onAuthenticationFailed");
            }
        });

        biometricPrompt.authenticate(promptInfo);
    }

    // 지문인증 한 뒤! 서버에 저장하는 부분.
    private void afterAuth(String opinion){

        // 서버로 넘길 값들 모으기
        Log.d(TAG, "msg: "+paperId+opinion);
        HashMap hashMap = new HashMap();
        hashMap.put("paperId", paperId);
        hashMap.put("opinion", opinion);

        // 레트로핏 객체 선언
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        // 레트로핏 서비스 인터페이스 선언
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        Call<ResponseBody> call = retrofitService.authPatient(hashMap);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.d(TAG, "onResponse: "+response);
                    // 서버에서 보낸 값을 스트링에 담음.
                    String result = response.body().string();
                    if(response.isSuccessful()){
                        if(result.equals("ok")){
                            Toast.makeText(getApplicationContext(), "수술에 동의하셨습니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "result is not ok: "+result);
                        }
                    }else{
                        Log.d(TAG, "unsuccessful: "+result);
                        Toast.makeText(getApplicationContext(), "서버와 통신이 좋지 않아요.", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //  통신 실패
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t);
            }
        });

    }
}
