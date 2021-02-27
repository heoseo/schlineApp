package kosmo.project3.schlineapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class FragmentSchedule extends Fragment {

    private static final String TAG = "juhee Schedule >>>";
    //일정
    public WebView mWebView;
    private  WebSettings mWebSettings;
    ImageButton btnCalendarMain;
    ImageButton btnSheduleMain;
    String user_id = StaticUserInformation.userID.toString();
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);


        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        ImageButton btnCalendar = (ImageButton)
                view.findViewById(R.id.btn_calendar_main);

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),
                        CalendarActivity.class);
                //달력화면 전환.
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ImageButton btnSheduleMain = (ImageButton)view.findViewById(R.id.btn_schedule_main);
        ImageButton btnCalendarMain = (ImageButton)view.findViewById(R.id.btn_calendar_main);

        btnCalendarMain.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(view.getContext(),CalendarActivity.class);
                Intent intent = new Intent(view.getContext(),CalTest.class);

                startActivity(intent);
            }
        });

        mWebView = (WebView)view.findViewById(R.id.webView_main);

        mWebView.setWebViewClient(new WebViewClient()); // 현재 앱을 나가서 새로운 브라우저를 열지 않도록 함.
        mWebSettings = mWebView.getSettings(); //웹뷰에서 webSettings를 사용할 수 있도록 함.
        mWebSettings.setJavaScriptEnabled(true); //웹뷰에서 javascript를 사용하도록 설정
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); //멀티윈도우 띄우는 것
        mWebSettings.setAllowFileAccess(true); //파일 엑세스
        mWebSettings.setSupportZoom(true); // 화면 줌 사용 여부
        mWebSettings.setBuiltInZoomControls(true); //화면 확대 축소 사용 여부
        mWebSettings.setDisplayZoomControls(true); //화면 확대 축소시, webview에서 확대/축소 컨트롤 표시 여부
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 사용 재정의
        //meta태그의 viewport사용 가능
        // wide viewport를 사용하도록 설정
        mWebSettings.setUseWideViewPort(true);
        // 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정
        // 웹뷰 멀티 터치 가능하게 (줌기능)
        mWebSettings.setLoadWithOverviewMode(true);
        mWebView.setNetworkAvailable(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebSettings.setDefaultFixedFontSize(12); //기본 고정 글꼴 크기, value : 1~72 사이의 숫자

        String str = null;
        try{
            str = "user_id=" + URLEncoder.encode(StaticUserInformation.userID, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "userID>>>>>>"+user_id);

        mWebView.postUrl("http://"+StaticInfo.my_ip+"/schline/android/alertList.do", str.getBytes());

    }

}




