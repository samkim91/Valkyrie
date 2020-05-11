package com.example.teamproject.CheckDoc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.teamproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckDocDetail extends AppCompatActivity {

    TextView tvTitle, tvDocInfo, tvDay, tvWriter, tvContent;
    ImageView imageView;

    String TAG = "CheckDocDetail";

    CheckDetailRCAdapter adapter = new CheckDetailRCAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_doc_detail);

        tvTitle = findViewById(R.id.titleSector);
        tvDocInfo = findViewById(R.id.docInfo);
        tvDay = findViewById(R.id.day);
        tvWriter = findViewById(R.id.writer);
        tvContent = findViewById(R.id.contentSector);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_image);
        // 레이아웃을 관리하는 매니저를 만들어주고, 이를 리사이클러뷰에 적용해준다.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        Intent intent = getIntent();
        String rawData = intent.getStringExtra("pass");

        rawDataToView(rawData);

        // 위에서 선언한 어뎁터를 리사이클러뷰에 적용해준다.
        recyclerView.setAdapter(adapter);

    }

    private void rawDataToView(String raw){
        try {
            JSONObject jsonObject = new JSONObject(raw);

            String mTitle = jsonObject.getString("title");
            String mHospital = jsonObject.getString("hospital");
            String mDocName = jsonObject.getString("name");
            String mMajor = jsonObject.getString("major");
            String docInfo = mHospital+" / "+mMajor+" / "+mDocName;

            String time = jsonObject.getString("timestamp");
            String timeEdited = time.substring(0,4)+"."+time.substring(4,6)+"."+time.substring(6,8);

            String writer = jsonObject.getString("publisher");
            String mContent = jsonObject.getString("text");

            String photo = jsonObject.getString("photo");

            tvTitle.setText(mTitle);
            tvDocInfo.setText(docInfo);
            tvDay.setText(timeEdited);
            tvWriter.setText(writer);
            tvContent.setText(mContent);

            JSONArray jsonArray = new JSONArray(photo);
            for(int i = 0 ; i<jsonArray.length() ; i++){
                String url = jsonArray.getString(i);

                CheckDetailRCData item = new CheckDetailRCData();
                item.setImageURL(url);

                adapter.addItem(item);
            }
            adapter.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
