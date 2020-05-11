package com.example.teamproject.CheckDoc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamproject.MyToken;
import com.example.teamproject.R;
import com.example.teamproject.RetrofitService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CheckDocHistoryActivity extends AppCompatActivity {

    CheckDocRCAdapter adapter;

    EditText typeDoctorName;
    Button searchBtn;

    String TAG = "CheckDocHistoryActivity";

    String URL = "http://34.64.150.54/";
    String blockchainURL = "http://34.97.209.205:4000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_doc_history);

        typeDoctorName = findViewById(R.id.editTextSearch);
        searchBtn = findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 블록체인에서 의사의 정보를 불러오는 부분.
                getDocInfo();
            }
        });

        adapter = new CheckDocRCAdapter(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewDocHistory);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(adapter);

        // 리사이클러뷰 아이템 클릭 시 넘어가기.
        adapter.setOnItemClickListener(new OnCheckDocRCClickListener() {
            @Override
            public void onItemClick(CheckDocRCAdapter.ViewHolder viewHolder, View view, int position) {
                Log.d(TAG, "onItemClick: "+position);
                CheckDocRCData item = adapter.getItem(position);

                Intent intent = new Intent(getApplicationContext(), CheckDocDetail.class);
                intent.putExtra("pass", item.getAllData());

                startActivity(intent);
            }
        });

    }

    private void getDocInfo(){
        Log.d(TAG, "getDocInfo");
        // 의사 정보를 조회해오는 부분.. 서버에서 입력된 의사 이름으로 된 키값을 다 받아오고 이것을 블록체인 네트워크에 요청할 것이다.

        String doctorName = typeDoctorName.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ResponseBody> call = retrofitService.getDoctorKey(doctorName);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: "+response);

                try {
                    String result = response.body().string();
                    if(response.isSuccessful()){
                        Log.d(TAG, "isSuccessful: "+result);
                        // 제이슨어레이가 날아왔을 것이다. 이것을 쪼개서 필요한 내용을 블록체인 네트워크에 올린다.
                        getDocInfoFromBlock(result);
                    }else{
                        Log.d(TAG, "unSuccessful: "+result);
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

    private void getDocInfoFromBlock(String rawKeys){
        Log.d(TAG, "getDocInfoFromBlock");

        try {

            String firstKey = null;
            String lastKey = null;
            String newLastKey = null;
            String mFunction = null;

            final JSONArray jsonArray = new JSONArray(rawKeys);
            JSONArray jsonArray1 = new JSONArray();

            if(jsonArray.length() > 1){
                mFunction = "queryRange";

                firstKey = jsonArray.get(0).toString();
                int lastIdx = jsonArray.length() - 1 ;
                lastKey = jsonArray.get(lastIdx).toString();

                String [] splitLastkey = lastKey.split("_");
                long addOne = Long.parseLong(splitLastkey[1]) + 1;

                newLastKey = splitLastkey[0]+"_"+addOne;

                jsonArray1.put(firstKey);
                jsonArray1.put(newLastKey);

            }else{
                mFunction = "query";
                firstKey = jsonArray.get(0).toString();

                jsonArray1.put(firstKey);
            }

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

            // 블록체인에 보내는 레트로핏
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(blockchainURL)
                    .client(client)
                    .build();

            Log.d(TAG, "mFcn: "+mFunction+"/args: "+jsonArray1.toString());
            RetrofitService retrofitService = retrofit.create(RetrofitService.class);
            Call<ResponseBody> call = retrofitService.pushToBlock(mFunction, "peer0.org1.example.com", jsonArray1.toString());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    // 서버에서 보낸 값을 스트링에 담음.
                    try {
                        String result = response.body().string();
                        if(response.isSuccessful()){
                            Log.d(TAG, "result is successful: "+result);

                            if(jsonArray.length() > 1){

                                JSONArray jsonArray2 = new JSONArray(result);

                                for(int i = 0 ; i<jsonArray2.length() ; i++){
                                    JSONObject jsonObject = jsonArray2.getJSONObject(i);

                                    JSONObject jsonObject1 = jsonObject.getJSONObject("Record");
                                    String mTitle = jsonObject1.getString("title");
                                    String mHospital = jsonObject1.getString("hospital");
                                    String mDocName = jsonObject1.getString("name");
                                    String docInfo = mHospital+" / "+mDocName;

                                    String time = jsonObject1.getString("timestamp");
                                    String timeEdited = time.substring(0,4)+"."+time.substring(4,6)+"."+time.substring(6,8);

                                    String writer = jsonObject1.getString("publisher");

                                    CheckDocRCData item = new CheckDocRCData(mTitle, docInfo, timeEdited, writer, result);

                                    adapter.addItem(item);
                                }
                            }else{

                                JSONObject jsonObject = new JSONObject(result);

                                String mTitle = jsonObject.getString("title");
                                String mHospital = jsonObject.getString("hospital");
                                String mDocName = jsonObject.getString("name");
                                String docInfo = mHospital+" / "+mDocName;

                                String time = jsonObject.getString("timestamp");
                                String timeEdited = time.substring(0,4)+"."+time.substring(4,6)+"."+time.substring(6,8);

                                String writer = jsonObject.getString("publisher");

                                CheckDocRCData item = new CheckDocRCData(mTitle, docInfo, timeEdited, writer, result);

                                adapter.addItem(item);
                            }

                            adapter.notifyDataSetChanged();

                        }else{
                            Log.d(TAG, "unsuccessful: "+result);
                            Toast.makeText(getApplicationContext(), "서버와 통신이 좋지 않아요.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: "+t);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
