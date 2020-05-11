package com.example.teamproject.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamproject.LoginedUser;
import com.example.teamproject.MainActivity;
import com.example.teamproject.MyToken;
import com.example.teamproject.R;
import com.example.teamproject.RetrofitService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    EditText uId, uPwd;
    Button loginBtn, signupBtn;

    String URL = "http://34.64.150.54/";
    String blockchainURL = "http://34.97.209.205:4000/";

    String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        uId = findViewById(R.id.typeId);
        uPwd = findViewById(R.id.typePwd);
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);

        clickListeners();
    }

    private void clickListeners(){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //sharedPreference에 아이디 저장
                SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String text = uId.getText().toString();
                editor.putString("UID",text);
                editor.commit();


                requestLogin();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void requestLogin(){
        Log.d(TAG, "requestLogin");
        final String id = uId.getText().toString();
        final String pwd = uPwd.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ResponseBody> call = retrofitService.requestLogin(id, pwd);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: "+response);

                try {
                    String result = response.body().string();

                    if(response.isSuccessful()){

                            // 의사 기기에 토큰 생성(로그인)을 해준다.
                            loginRequest();

                            Log.d(TAG, "isSuccessful: "+result);
                            LoginedUser.setId(id);
                            LoginedUser.setName(result);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                    }else{
                        Log.d(TAG, "isn't Successful: "+result);
                        Toast.makeText(getApplicationContext(), "서버와 통신이 좋지 않아요.", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t);
            }
        });
    }


    // 블록체인 네트워크에 접속해서 로그인하는 것. 이후 토큰을 받아서 보관할 예정이다.
    private void loginRequest(){
        Log.d(TAG, "loginRequest");
        HashMap hashMap = new HashMap();
        hashMap.put("username", "NovaHospital");
        hashMap.put("orgName", "Org1");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(blockchainURL)
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ResponseBody> call = retrofitService.loginRequest(hashMap);



        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: "+response);
                try {
                    // 서버에서 보낸 값을 스트링에 담음.
                    String result = response.body().string();
                    if(response.isSuccessful()){
                        Log.d(TAG, "result is successful: "+result);

                        JSONObject jsonObject = new JSONObject(result);
                        MyToken.setmToken(jsonObject.getString("token"));
                        Log.d(TAG, "token: "+ MyToken.getmToken());

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
                Log.d(TAG, "onFailure: "+t);
            }
        });
    }
}
