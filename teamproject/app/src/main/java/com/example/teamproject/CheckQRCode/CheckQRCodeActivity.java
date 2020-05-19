package com.example.teamproject.CheckQRCode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.teamproject.Consent.AuthDocActivity;
import com.example.teamproject.Consent.ConsentFormActivity;
import com.example.teamproject.Consent.SelectSectionActivity;
import com.example.teamproject.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class CheckQRCodeActivity extends AppCompatActivity {

    String TAG = "CheckQRcodeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_qrcode);

        Log.d(TAG, "onCreate");

        // 이 액티비티에 왔을 때 기본적으로 실행시키는 qr코드 스캐너
        IntentIntegrator qrScanner = new IntentIntegrator(this);
        qrScanner.setOrientationLocked(false);          // 막아놓은 회전기능을 풀어줌.
        qrScanner.setPrompt("QR code를 보여주세요.");
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
                Toast.makeText(this, "인식결과: "+data.getDataString(), Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
