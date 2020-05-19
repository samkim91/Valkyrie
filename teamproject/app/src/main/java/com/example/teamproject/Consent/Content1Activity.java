package com.example.teamproject.Consent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamproject.R;

public class Content1Activity extends AppCompatActivity {

    public static final int CONTENT1_OK = 10 ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content1);

        Button btn_content1 = findViewById(R.id.btn_content1);
        btn_content1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.putExtra("check1", true) ;

                setResult(CONTENT1_OK, intent) ;
                finish() ;
            }
        });

    }
}
