package kosmo.project3.schlineapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;


public class StudyroomChatActivity extends AppCompatActivity {

    private static final String TAG = "StudyroomChatActivity";

    WebView chatWebView;
    WebSettings chatWebSetting;
    TextView time_out, myRec;
    //int cur_Status = Init; //현재의 상태를 저장할변수를 초기화함.
    int myCount=1;
    long myBaseTime;
    String str;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studyroom_chat);

        //타이머
        time_out = (TextView)findViewById(R.id.time_out);
        myRec = (TextView)findViewById(R.id.record);
        myBaseTime = SystemClock.elapsedRealtime();
        System.out.println(myBaseTime);
        //myTimer이라는 핸들러를 빈 메세지를 보내서 호출
        myTimer.sendEmptyMessage(0);

        //런런
        //String str = myRec.getText().toString();
        //str +=  String.format("%d. %s\n",myCount,getTimeOut());
        //myRec.setText(str);
        myCount++; //카운트 증가


        //웹뷰시작
        chatWebView = (WebView) findViewById(R.id.chatWebView);

        // 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정
        chatWebView.setVerticalScrollBarEnabled(false);   //세로 스크롤
        //클릭시 새창 안뜨게.
        //chatWebView.setWebViewClient(new WebViewClient());
        //세부 세팅 등록.
        chatWebSetting = chatWebView.getSettings();
        //웹페이지 자바스크를비트 허용 여부.
        chatWebSetting.setJavaScriptEnabled(true);
        //새창띄우기 허용여부.
        chatWebSetting.setSupportMultipleWindows(true);
        //자바스크립트 새창 띄우기(멀티뷰) 허용 여부.
        chatWebSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        //메타태그 허용 여부.
        chatWebSetting.setLoadWithOverviewMode(true); // 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정
        //화면 사이즈 맞추기 허용 여부.
        chatWebSetting.setUseWideViewPort(true); // wide viewport를 사용하도록 설정
        //화면 줌 허용 여부.
        chatWebSetting.setSupportZoom(true);
        //화면 확대 축소 허용 여부.
        chatWebSetting.setBuiltInZoomControls(true);
        //컨텐츠 사이즈 맞추기
        chatWebSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        //브라우저 캐시 허용 여부.
        chatWebSetting.setCacheMode(WebSettings.LOAD_NORMAL);
        //로컬저장소 허용 여부.
        chatWebSetting.setDomStorageEnabled(true);
        //http://localhost:9999/schline/schedule/calendar.do


        String user_id = StaticUserInformation.userID;


        //웹뷰에 표시할 웹사이트 주소, 웹뷰 시작.
        //chatWebView.loadUrl("http://localhost:9999/schline/android/class/Chat.do");
        //chatWebView.loadUrl("http://"+StaticInfo.my_ip+"/schline/android/class/Chat.do");

        //웹뷰 파라미터 넘기기
        try {
            str = "user_id=" + URLEncoder.encode(StaticUserInformation.userID, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        chatWebView.postUrl("http://"+StaticInfo.my_ip+"/schline/android/class/Chat.do", str.getBytes());
    }/////끝

    class CustomWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }
    }


    Handler myTimer = new Handler(){
        public void handleMessage(Message msg){
            time_out.setText(getTimeOut());

            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송하는겁니다.
            myTimer.sendEmptyMessage(0);
        }
    };

    //현재시간을 계속 구해서 출력하는 메소드
    String getTimeOut(){
        long now = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        long outTime = now - myBaseTime;
        String easy_outTime = String.format("%02d:%02d", outTime/1000 / 60, (outTime/1000)%60);
        return easy_outTime;

    }
}