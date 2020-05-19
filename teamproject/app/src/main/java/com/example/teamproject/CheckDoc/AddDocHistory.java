package com.example.teamproject.CheckDoc;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamproject.LoginedUser;
import com.example.teamproject.MyToken;
import com.example.teamproject.R;
import com.example.teamproject.RealPathFromURI;
import com.example.teamproject.RetrofitService;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class AddDocHistory extends AppCompatActivity {

    ImageView addImage;
    EditText title, hospital, doctorName, doctorMajor, content;
    Button saveBtn;

    AddImageRCAdapter adapter = new AddImageRCAdapter(this);

    String TAG = "AddDocHistory";
    int REQUEST_CODE_FOR_IMAGES = 100;

    String URL = "http://34.64.150.54/";
    private String blockchainURL = "http://34.97.209.205:4000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doc_history);

        addImage = findViewById(R.id.fake);
        title = findViewById(R.id.title_et);
        hospital = findViewById(R.id.hospital);
        doctorName = findViewById(R.id.doctorName);
        doctorMajor = findViewById(R.id.doctorMajor);
        content = findViewById(R.id.content_tv);
        saveBtn = findViewById(R.id.saveBtn);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_image);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        // 이미지 추가하기 위한 버튼 기능
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMultiImages();
            }
        });

        // 어뎁터에서 해당 사진 삭제하는 기능
        adapter.setOnItemClickListener(new OnAddImageRCClickListener() {
            @Override
            public void onItemClick(AddImageRCAdapter.ViewHolder viewHolder, View view, int position) {

                adapter.items.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageToServer();
            }
        });

    }

    private void uploadImageToServer(){
        Log.d(TAG, "uploadImageToServer");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        ArrayList<MultipartBody.Part> imageList = new ArrayList<>();

        for(int i = 0 ; i<adapter.items.size() ; i++){
            File file = new File(adapter.getItem(i).getImageURL());

            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("files[]", adapter.getItem(i).getImageURL(), requestBody);
            imageList.add(uploadFile);
        }

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ResponseBody> call = retrofitService.uploadImage(imageList);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: "+response);

                try {
                    String result = response.body().string();

                    if(response.isSuccessful()){
                        Log.d(TAG, "isSuceessful: "+result);
                        // 이미지 주소를 가진 제이슨어레이가 날라옴. 블록체인과 통신하면 됨.
                        uploadReportToBlock(result);

                    }else{
                        Log.d(TAG, "isn't Suceessful: "+result);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t);
            }
        });
    }

    // 서버에 의사와 의사관련 게시물의 키를 저장하는 부분
    private void uploadReportToServer(String doctorName, String uniqueKey){
        Log.d(TAG, "uploadReportToServer");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ResponseBody> call = retrofitService.uploadDoctorKey(doctorName, uniqueKey);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: "+response);

                try {
                    // 서버에서 보낸 값을 스트링에 담음.
                    String result = response.body().string();
                    if(response.isSuccessful()){
                        Log.d(TAG, "result is successful: "+result);
                    }else{
                        Log.d(TAG, "unsuccessful: "+result);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t);
            }
        });


    }

    private void uploadReportToBlock(String imageURLArray){
        Log.d(TAG, "uploadReportToBlock");

        // 에딭텍스트에서 변수값 받아옴.
        String writer = LoginedUser.getName();
        String mDocterName = doctorName.getText().toString();
        String mHospital = hospital.getText().toString();
        String mDoctorMajor = doctorMajor.getText().toString();
        String mTitle = title.getText().toString();
        String mContent = content.getText().toString();
        // 이미지주소 어레이는 메소드의 파라미터로 받아왔기에 바로 사용하면 됨.

        //timeStamp 생성
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStamp = simpleDate.format(mDate);

        // 디비, 블록체인과 연결하기 위한 유일키
        String uniqueKey = mDocterName+"_"+timeStamp;

        uploadReportToServer(mDocterName, uniqueKey);

        // 피어들
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("peer0.org1.example.com");
        jsonArray.put("peer0.org2.example.com");

        // 아규먼트 만들기
        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.put(uniqueKey);
        jsonArray1.put(writer);
        jsonArray1.put(mDocterName);
        jsonArray1.put(mHospital);
        jsonArray1.put(mDoctorMajor);
        jsonArray1.put(mTitle);
        jsonArray1.put(mContent);
        jsonArray1.put(imageURLArray);
        jsonArray1.put(timeStamp);

        // 헤더에 토큰을 넣으려고 레트로핏을 네트워크 층에서 가로챈 다음에 수정해줘야함.
        // okhttp 클라이언트 빌더를 선언해주고 인터셉트 함수 안에서 필요한 헤더를 추가한다. 메소드나 바디는 기존 요청의 것을 사용한다.
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("authorization", "Bearer "+ MyToken.getmToken())
                        .header("content-type", "application/json")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
        OkHttpClient client = httpClient.build();

        // 레트로핏 객체와 서비스 객체 선언은 기존과 같다. 한가지 추가된 것은 헤더 변경을 위해 위에서 만들어준 통신 클라이언트를 레트로핏에 넣어주는 것
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(blockchainURL)
                .client(client)
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ResponseBody> call = retrofitService.createDocument("createDoctor", jsonArray.toString(), jsonArray1.toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: "+response);
                try {
                    // 서버에서 보낸 값을 스트링에 담음.
                    String result = response.body().string();
                    if(response.isSuccessful()){
                        Log.d(TAG, "result is successful: "+result);
                        Toast.makeText(getApplicationContext(), "블록체인에 저장되었어요.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }else{
                        Log.d(TAG, "unsuccessful: "+result);
                        Toast.makeText(getApplicationContext(), "서버와 통신이 좋지 않아요.", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t);
            }
        });
    }

    private void selectMultiImages(){
        Log.d(TAG, "selectMultiImages");
        // 선택을 할 수 있는 인텐트를 만듦
        Intent intent = new Intent(Intent.ACTION_PICK);
        // 이미지 선택을 하겠다는 의미
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

        // 아래 줄로 인해서 한번에 여러장을 받아올 수 있다.
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        // 선택된 이미지의 uri를 받아오겠다는 의미
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_FOR_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: "+data);
        Log.d(TAG, "requestCode: "+requestCode);
        Log.d(TAG, "resultCode: "+resultCode);

        if(requestCode == REQUEST_CODE_FOR_IMAGES && resultCode == RESULT_OK){

            if(data.getClipData() == null){
                Toast.makeText(getApplicationContext(), "다중선택이 불가능한 기기에요.", Toast.LENGTH_SHORT).show();
            }else{
                ClipData clipData = data.getClipData();
                Log.d(TAG, "clipData: "+clipData.getItemCount());

                // 총 사진의 갯수가 10개가 초과되지 못하게 한다.
                if(clipData.getItemCount() > 10 || (clipData.getItemCount()+adapter.getItemCount()) > 10){
                    Toast.makeText(getApplicationContext(), "사진은 10장 이하만 선택해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d(TAG, "사진 선택완료!");
                    for(int i = 0 ; i < clipData.getItemCount() ; i++){

                        // 파일의 path를 불러옴
                        String path = RealPathFromURI.getRealPathFromURI(this, clipData.getItemAt(i).getUri());
                        Log.d(TAG, "Path: "+path);

                        // 리사이클러뷰에 넣을 객체를 생성하고 path를 부여해줌
                        AddImageRCData item = new AddImageRCData(path);

                        // 리사이클러뷰에 추가
                        adapter.addItem(item);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

}
