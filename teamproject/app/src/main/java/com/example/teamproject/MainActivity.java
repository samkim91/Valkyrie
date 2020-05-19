package com.example.teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.teamproject.CheckDoc.AddDocHistory;
import com.example.teamproject.CheckDoc.CheckDocHistoryActivity;
import com.example.teamproject.CheckQRCode.CheckQRCodeActivity;
import com.example.teamproject.Consent.AuthDocActivity;
import com.example.teamproject.ReadConsent.ReadConsentActivity;

public class MainActivity extends AppCompatActivity {

    Button writeConsentBtn, readAgreementBtn, checkDocHistoryBtn, checkQRCodeBtn, reportDoctorBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 버튼 선언
        writeConsentBtn = findViewById(R.id.writeConsent);
        readAgreementBtn = findViewById(R.id.readAgreement);
        checkDocHistoryBtn = findViewById(R.id.checkDoc);
        checkQRCodeBtn = findViewById(R.id.checkQRCode);
        reportDoctorBtn = findViewById(R.id.reportDoctor);

        // 버튼 클릭 이벤트 모음 메소드
        btnListeners();
    }

    private void btnListeners(){
        // 동의서 작성 버튼 클릭리스너
        writeConsentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 동의서를 작성하기 위해 과->수술->인증->동의서 순으로 이동
                Intent intent = new Intent(getApplicationContext(), AuthDocActivity.class);
                startActivity(intent);
            }
        });

        // 의사 조회 버튼 클릭리스너
        checkDocHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 의사조회 폼으로 이동
                Intent intent = new Intent(getApplicationContext(), CheckDocHistoryActivity.class);
                startActivity(intent);
            }
        });

        readAgreementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReadConsentActivity.class);
                startActivity(intent);
            }
        });

        // QR코드 확인 버튼 클릭리스너
        checkQRCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CheckQRCodeActivity.class);
                startActivity(intent);
            }
        });

        // 의사 신고하는 기능
        reportDoctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddDocHistory.class);
                startActivity(intent);
            }
        });

    }
}
