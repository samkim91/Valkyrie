package com.example.teamproject.Consent;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamproject.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ConsentFormActivity extends AppCompatActivity {

    private String TAG = "ConsentFormActivity";

    static final int CONTENT = 1;
    public static final int CONTENT1_OK = 10;
    public static final int CONTENT2_OK = 20;
    public static final int CONTENT3_OK = 30;
    CheckBox check1, check2, check3, check4;
    Button btn_toQR;

    boolean checked1, checked2, checked3, checked4 = false;

    String docInfo;
    String surgery_name;

    TextView tv_doctorName, tv_surgeryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consent_form);

        // 이전 액티비티에서 qr코드 읽은 결과를 받음
        Intent intent = getIntent();
        docInfo = intent.getStringExtra("DocInfo");
        surgery_name = intent.getStringExtra("surgery_name");
        Log.d(TAG, docInfo+"/"+surgery_name);

        check1 = (CheckBox) findViewById(R.id.check1);
        check2 = (CheckBox) findViewById(R.id.check2);
        check3 = (CheckBox) findViewById(R.id.check3);
        check4 = (CheckBox) findViewById(R.id.check4);

        tv_doctorName = findViewById(R.id.tv_doctorName);
        tv_surgeryName = findViewById(R.id.tv_surgery_name);

        tv_surgeryName.setText(surgery_name);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(docInfo);
            tv_doctorName.setText(jsonObject.getString("name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //onClick 리스트
        onClickList();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == CONTENT) {
            if (resultCode == CONTENT1_OK) {
                //각 content를 읽고 확인 버튼을 누르면 true가 반환됨.
                checked1 = intent.getBooleanExtra("check1", false);
                if (checked1) {
                    check1.setChecked(true);
                }
            }
            if (resultCode == CONTENT2_OK) {

                checked2 = intent.getBooleanExtra("check2", false);
                if (checked2) {
                    check2.setChecked(true);
                }
            }
            if (resultCode == CONTENT3_OK) {

                checked3 = intent.getBooleanExtra("check3", false);
                if (checked3) {
                    check3.setChecked(true);
                }
            }

        }
    }

    private void onClickList(){

        //클릭시 해당하는 수술동의서 콘텐츠로 이동
        LinearLayout content1 = findViewById(R.id.content1);
        content1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConsentFormActivity.this, Content1Activity.class) ;
                startActivityForResult(intent, CONTENT);
            }
        });

        LinearLayout content2 = findViewById(R.id.content2);
        content2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConsentFormActivity.this, Content2Activity.class) ;
                startActivityForResult(intent, CONTENT);
            }
        });

        LinearLayout content3 = findViewById(R.id.content3);
        content3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConsentFormActivity.this, Content3Activity.class) ;
                startActivityForResult(intent, CONTENT);
            }
        });

        //다음 화면(QR코드 인식)으로 진행
        btn_toQR = findViewById(R.id.btn_toQR);
        btn_toQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checked4 = check4.isChecked();

                //테스트용. 안 읽고 체크해도 넘어가도록
                checked1 = check1.isChecked();
                checked2 = check2.isChecked();
                checked3 = check3.isChecked();

                Log.d(TAG, "Boolean 1 2 3 4 : "+checked1+"/"+checked2+"/"+checked3+"/"+checked4);
                //모든 항목에 체크가 되었을때만 다음으로 넘어가도록 설정
                if(checked1 && checked2 && checked3 && checked4){
                    Intent intent = new Intent(ConsentFormActivity.this, QRCreateActivity.class);
                    // 환자가 본인인증하는 QR를 생성하기 위해 필요한 값들을 넘긴다.
                    intent.putExtra("docInfo", docInfo);

                    //환자이름 가져오기.
                    EditText tv_patName = findViewById(R.id.et_patName);
                    String patName = tv_patName.getText().toString();

                    //TODO.. 동의서 고유값 반영 필요.
                    intent.putExtra("paperId", "2323");
                    intent.putExtra("surgery_name", surgery_name);
                    intent.putExtra("patName", patName);

                    startActivity(intent);
                }else{
                    Toast.makeText(ConsentFormActivity.this, "모든 항목에 동의해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
