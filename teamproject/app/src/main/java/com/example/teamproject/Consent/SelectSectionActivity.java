package com.example.teamproject.Consent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamproject.R;

public class SelectSectionActivity extends AppCompatActivity {

    String docInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_section);

        Intent intent2 = getIntent();
        docInfo = intent2.getStringExtra("DocInfo");

        Button plastic_surgery = findViewById(R.id.plastic_surgery);
        plastic_surgery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectSectionActivity.this, SelectSurgeryActivity.class);
                intent.putExtra("DocInfo", docInfo);
                startActivity(intent);
            }
        });


    }
}
