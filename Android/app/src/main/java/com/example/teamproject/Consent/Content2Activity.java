package com.example.teamproject.Consent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamproject.R;

public class Content2Activity extends AppCompatActivity {

    public static final int CONTENT2_OK = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content2);

        Button btn_content2 = findViewById(R.id.btn_content2);
        btn_content2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.putExtra("check2", true);
                setResult(CONTENT2_OK, intent) ;
                finish() ;
            }
        });

    }
}
