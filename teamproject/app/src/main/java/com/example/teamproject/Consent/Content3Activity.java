package com.example.teamproject.Consent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamproject.R;

public class Content3Activity extends AppCompatActivity {

    public static final int CONTENT3_OK = 30;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content3);

        Button btn_content3 = findViewById(R.id.btn_content3);
        btn_content3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.putExtra("check3", true);
                setResult(CONTENT3_OK, intent) ;
                finish() ;
            }
        });

    }
}
