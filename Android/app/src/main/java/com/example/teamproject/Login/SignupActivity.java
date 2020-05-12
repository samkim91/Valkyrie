package com.example.teamproject.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teamproject.R;
import com.example.teamproject.RetrofitService;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignupActivity extends AppCompatActivity {

    EditText editTextId, editTextPwd, editTextPwdAgain, editTextName;
    Button checkDuplicateBtn, signupBtn;

    String URL = "http://34.64.150.54/";

    String TAG = "SingupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextId = findViewById(R.id.typeId);
        editTextPwd = findViewById(R.id.typePwd);
        editTextPwdAgain = findViewById(R.id.typePwdAgain);
        editTextName = findViewById(R.id.typeName);

        checkDuplicateBtn = findViewById(R.id.checkDuplicate);
        signupBtn = findViewById(R.id.signupBtn);

        clickListeners();
    }

    private void clickListeners(){
        checkDuplicateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 서버랑 통신해서 아이디가 중복되는지 확인
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 서버에 해당값을 저장
                requestSignup();
            }
        });
    }

    private void requestSignup(){
        String id = editTextId.getText().toString();
        String pwd = editTextPwd.getText().toString();
        String pwdAgain = editTextPwdAgain.getText().toString();
        String name = editTextName.getText().toString();

        // 비밀번호와 비밀번호 확인이 같으면 실행
        if(pwd.equals(pwdAgain)){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .build();

            RetrofitService retrofitService = retrofit.create(RetrofitService.class);
            Call<ResponseBody> call = retrofitService.requestSignup(id, pwd, name);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d(TAG, "onResponse: "+response);

                    try {
                        String result = response.body().string();
                        if(response.isSuccessful()){
                            if(result.equals("ok")){
                                Log.d(TAG, "isSuccessful: "+result);
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }else{
                                Log.d(TAG, "isSuccessful but not ok: "+result);
                                Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
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

        }else{
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않아요.", Toast.LENGTH_SHORT).show();
        }
    }
}
