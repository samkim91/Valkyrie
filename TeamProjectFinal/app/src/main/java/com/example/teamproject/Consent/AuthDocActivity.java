package com.example.teamproject.Consent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.teamproject.MyToken;
import com.example.teamproject.R;
import com.example.teamproject.RetrofitService;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AuthDocActivity extends AppCompatActivity {

    String TAG = "AuthDocActivity";

    Button restartQRCodeScannerBtn;

    Button passBtn; // 테스트용 버튼
    String testDoc = "{\n" +
            "\t\"certificateNum\": \"1234\",\n" +
            "\t\"name\": \"김상진\",\n" +
            "\t\"birthday\": \"1993년 10월 1일\",\n" +
            "\t\"certificateDay\": \"2019년 12월 9일\"\n" +
            "}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_doc);
        Log.d(TAG, "onCreate");

        // TODO.. 테스트를 위해 만들어놓음.. 나중에는 삭제/주석 처리해야함.
        passBtn = findViewById(R.id.passBtn);
        passBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SelectSectionActivity.class);
                intent.putExtra("DocInfo", testDoc);
                startActivity(intent);
            }
        });

        // QR코드 못 읽고, 이 액티비티로 돌아왔을 때 다시 실행시키는 버튼임
        restartQRCodeScannerBtn = findViewById(R.id.restartQRCodeScanner);
        restartQRCodeScannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator qrScanner = new IntentIntegrator(AuthDocActivity.this);
                qrScanner.setOrientationLocked(false);          // 막아놓은 회전기능을 풀어줌.
                qrScanner.setPrompt("의사의 QR code를 보여주세요.");
                qrScanner.initiateScan();           // 스캐너 실행
            }
        });

        // 이 액티비티에 왔을 때 기본적으로 실행시키는 qr코드 스캐너
        IntentIntegrator qrScanner = new IntentIntegrator(this);
        qrScanner.setOrientationLocked(false);          // 막아놓은 회전기능을 풀어줌.
        qrScanner.setPrompt("의사의 QR code를 보여주세요.");
        qrScanner.initiateScan();           // 스캐너 실행
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult");
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);      // intent로 넘긴 결과를 qr코드 스캐너 결과에 넘겨줌.

        // 결과값이 널이 아니고, 안에 내용이 널이 아닌지 확인하는 조건문
        if(result != null){
            Log.d(TAG, "result not null");
            if(result.getContents() == null){
                Log.d(TAG, "result have contents");
                Toast.makeText(this, "인식이 되지 않습니다.", Toast.LENGTH_SHORT).show();
                //
            }else{
               // qr코드에서 가져온 정보를 인텐트에 담아서 동의서 액티비티로 보냄
                Log.d(TAG, "result is not null");



                Intent intent = new Intent(getApplicationContext(), SelectSectionActivity.class);
                intent.putExtra("DocInfo", result.getContents());
                startActivity(intent);
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }



}
