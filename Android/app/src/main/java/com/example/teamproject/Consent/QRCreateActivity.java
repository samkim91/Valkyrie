package com.example.teamproject.Consent;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamproject.MainActivity;
import com.example.teamproject.MyToken;
import com.example.teamproject.R;
import com.example.teamproject.RetrofitService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.hendrix.pdfmyxml.PdfDocument;
import com.hendrix.pdfmyxml.viewRenderer.AbstractViewRenderer;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class QRCreateActivity extends AppCompatActivity {

    private String TAG = "QRCreateActivity";

    private String URL = "http://34.64.150.54/callmyapp.php?value=";
    private String myURL = "http://34.64.150.54/";
    private String blockchainURL = "http://34.97.209.205:4000/";

    private ImageView iv;

    private TextView tv_doctorName, tv_surgery_name;
    private String docInfo, paperId, surgery_name, patName, doctor;
    private String text;

    private Button check_return_btn, convert_pdf_btn;

    RetrofitService retrofitService;
    Retrofit retrofit;

    String agreementContent, agreementVer, timeStamp, userid;
    String totalDataHashed;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_create);

        iv = (ImageView)findViewById(R.id.qrcode);
        tv_doctorName = findViewById(R.id.tv_doctorName);
        tv_surgery_name = findViewById(R.id.tv_surgery_name);
        check_return_btn = findViewById(R.id.check_return);
        convert_pdf_btn = findViewById(R.id.convert_pdf);

        Intent intent = getIntent();
        docInfo = intent.getStringExtra("docInfo");
        paperId = intent.getStringExtra("paperId");
        surgery_name = intent.getStringExtra("surgery_name");
        patName = intent.getStringExtra("patName");

        // QR코드에 넣을 텍스트 json화
        text = "["+docInfo+",{\"paperId\":\""+paperId+"\",\"surgery_name\":\""+surgery_name+"\"}]";
        Log.d(TAG, "text is : "+text);

        // 레트로핏 객체 선언
        retrofit = new Retrofit.Builder()
                .baseUrl(myURL)
                .build();
        // 레트로핏 서비스 객체 선언
        retrofitService = retrofit.create(RetrofitService.class);

        // QR코드 생성하는 작업
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{

            String encoded_text = URLEncoder.encode(text, "utf-8");

            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

            BitMatrix bitMatrix = multiFormatWriter.encode(URL+encoded_text, BarcodeFormat.QR_CODE,200,200, hints);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            iv.setImageBitmap(bitmap);
        }catch (Exception e){}


        //DB에서 수술동의서 가져오기
        getConsent();

        // 의사가 확인 버튼을 누르면
        check_return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 환자가 본인 인증했는지 서버에 물어보는 기능
                getPatientOpinion();
            }
        });

        convert_pdf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "convert to PDF btn clicked");
//                convert_to_pdf();
                updateConsent();

                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent1);
            }
        });

    }


    private void getPatientOpinion(){

        // 콜 객체 선언 및 큐에 넣기.. 데이터베이스 서버에 문서 아이디가 저장되어 있으므로, 해당 문서 아이디를 겟으로 보내서 저장된 값이 있는지 확인한다.
        Call<ResponseBody> call = retrofitService.getOpinion(paperId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: "+response);

                try {
                    // 서버에서 보낸 값을 스트링에 담음.
                    String result = response.body().string();
                    if(response.isSuccessful()){
                        if(result.equals("ok")){
                            Toast.makeText(getApplicationContext(), "환자가 동의하셨습니다.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "result is ok: "+result);

                        }else{
                            Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "result is not ok: "+result);
                        }
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
                Log.d(TAG, "onFailure: "+t);
            }
        });
    }


    //RetroFit으로 수술동의서 내용과 버전 받아오기.
    private void getConsent(){
        Call<ResponseBody> call = retrofitService.getConsent(surgery_name);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                //JSON으로 content와 ver를 가져온 후 parsing.
                String result = null;
                try {
                    result = response.body().string();
                    Log.d(TAG, "GETconsent: "+result);

                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("consentform");
                    for(int i=0;i<jsonArray.length();i++){

                        JSONObject item = jsonArray.getJSONObject(i);

                        String content = item.getString("content");
                        String ver = item.getString("ver");

                        agreementContent = content;         //수술동의서 콘텐츠
                        agreementVer = ver;                 //수술동의서 버전
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    //
    private void updateConsent(){

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(docInfo);
            doctor = jsonObject.getString("name");      //의사 이름 받아오기

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //timeStamp 생성 후 저장
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss");
        timeStamp = simpleDate.format(mDate);

        //userid
        userid = "test";

        HashMap hashMap = new HashMap();
        //hashMap.put("patname", patName);
        //hashMap.put("docname", doctor);
        //hashMap.put("content", agreementContent);
        //hashMap.put("papername", surgery_name);
        //hashMap.put("paperver", agreementVer);
        hashMap.put("time", timeStamp);
        hashMap.put("userid", userid);
        hashMap.put("documentkey", userid+timeStamp);

        Call<ResponseBody> call = retrofitService.updateConsentForm(hashMap);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.d(TAG, "onResponse: "+response);
                    // 서버에서 보낸 값을 스트링에 담음.
                    String result = response.body().string();
                    if(response.isSuccessful()){
                        if(result.equals("ok")){
                            Toast.makeText(getApplicationContext(), "저장에 성공했습니다.", Toast.LENGTH_SHORT).show();
                            //데이터 합쳐서 해시 후 데이터 저장
                            String totalData = timeStamp+doctor+patName+agreementContent+surgery_name+agreementVer+userid;
                            totalDataHashed = testSHA256(totalData);
                            Log.d("해쉬된 값 : ", totalDataHashed);

                            createDocumentForBlock();

                        }else{
                            Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "result is not ok: "+result);
                        }
                    }else{
                        Log.d(TAG, "unsuccessful: "+result);
                        Toast.makeText(getApplicationContext(), "db 서버 unsuccess.", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(QRCreateActivity.this, "db 서버 failed.", Toast.LENGTH_SHORT).show();

                    //지금 여기서 에러.
                    //아예 통신이 안 됨. url. 입력값.
            }

        });
    }


    private void createDocumentForBlock(){

        //userid를 인텐트로 받아온다.
        SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);
        String userid = sharedPreferences.getString("UID","");
        Log.d("쉐어드 저장", userid);

        JSONArray jsonArray = new JSONArray();
        jsonArray.put("peer0.org1.example.com");
        jsonArray.put("peer0.org2.example.com");

        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.put(userid+timeStamp);
        jsonArray1.put(doctor);
        jsonArray1.put(patName);
        jsonArray1.put(surgery_name+0.1);
        jsonArray1.put(timeStamp);

//        HashMap hashMap = new HashMap();
//        hashMap.put("peers", jsonArray);
//        hashMap.put("args", jsonArray1);

//        String encodedPeers = null;
//        String encodedArgs = null;
//        try {
//            encodedPeers = URLEncoder.encode(jsonArray.toString(), "utf-8");
//            encodedArgs = URLEncoder.encode(jsonArray1.toString(), "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }


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
        Call<ResponseBody> call = retrofitService.createDocument("createDocument", jsonArray.toString(), jsonArray1.toString());
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
                        Toast.makeText(getApplicationContext(), "블록체인 서버와 통신이 좋지 않아요.", Toast.LENGTH_SHORT).show();
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


    //String 해쉬화
    public String testSHA256(String str){

        String SHA = "";

        try{
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for(int i = 0 ; i < byteData.length ; i++){
                sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
            }
            SHA = sb.toString();

        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            SHA = null;
        }
        return SHA;
    }


    // 파일 생성은 되는 것 같은데 안드로이드 기기에서 읽지 못하는 문제와 파일에 아무 내용이 담기지 않는 문제가 있음.
    private void convert_to_pdf(){
        Log.d(TAG, "in convert to PDF method");
        AbstractViewRenderer page = new AbstractViewRenderer(getApplicationContext(), R.layout.qr_create) {
            @Override
            protected void initView(View view) {

            }
        };

        // 비트맵을 재사용 할 수 있게끔 함.
        page.setReuseBitmap(true);

        PdfDocument doc = new PdfDocument(getApplicationContext());

        doc.addPage(page);
        doc.setRenderWidth(100);
        doc.setRenderHeight(200);
        doc.setOrientation(PdfDocument.A4_MODE.PORTRAIT);
        doc.setProgressTitle(R.string.converttitle);
        doc.setProgressMessage(R.string.convertmessage);
        doc.setFileName("agreement");
        doc.setSaveDirectory(getApplicationContext().getExternalFilesDir(null));
        doc.setInflateOnMainThread(false);
        doc.setListener(new PdfDocument.Callback() {
            @Override
            public void onComplete(File file) {
                Log.d(TAG, "onComplete: "+file);
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, "onError: "+e);
            }
        });

        doc.createPdf(QRCreateActivity.this);

    }
}
