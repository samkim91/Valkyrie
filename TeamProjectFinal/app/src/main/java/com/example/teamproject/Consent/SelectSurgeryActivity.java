package com.example.teamproject.Consent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamproject.R;

public class SelectSurgeryActivity extends AppCompatActivity {

    private String TAG = "SelectSurgeryActivity";

    String docInfo;

    String surgery_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_surgery);

        Intent intent2 = getIntent();
        docInfo = intent2.getStringExtra("DocInfo");

        final Button surgery1 = findViewById(R.id.surgery1);
        surgery1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surgery_name = surgery1.getText().toString();

                Log.d(TAG, surgery_name);

                Intent intent = new Intent(SelectSurgeryActivity.this, ConsentFormActivity.class);
                intent.putExtra("DocInfo", docInfo);
                intent.putExtra("surgery_name", surgery_name);
                startActivity(intent);

            }
        });

    }
}
