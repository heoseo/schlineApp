package kosmo.project3.schlineapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class FragmentSchedule extends Fragment {

    private static final String TAG = "juhee";
    public WebView mWebView;
    private  WebSettings mWebSettings;
    String user_id = StaticUserInformation.userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWebView = (WebView)view.findViewById(R.id.webView);

        mWebView.setWebViewClient(new WebViewClient()); // 현재 앱을 나가서 새로운 브라우저를 열지 않도록 함.
        mWebSettings = mWebView.getSettings(); //웹뷰에서 webSettings를 사용할 수 있도록 함.
        mWebSettings.setJavaScriptEnabled(true); //웹뷰에서 javascript를 사용하도록 설정
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); //멀티윈도우 띄우는 것
        mWebSettings.setAllowFileAccess(true); //파일 엑세스
        mWebSettings.setLoadWithOverviewMode(true);// 메타태그
        mWebSettings.setUseWideViewPort(true); //화면 사이즈 맞추기
        mWebSettings.setSupportZoom(true); // 화면 줌 사용 여부
        mWebSettings.setBuiltInZoomControls(true); //화면 확대 축소 사용 여부
        mWebSettings.setDisplayZoomControls(true); //화면 확대 축소시, webview에서 확대/축소 컨트롤 표시 여부
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 사용 재정의
        // value : LOAD_DEFAULT, LOAD_NORMAL,
        // LOAD_CACHE_ELSE_NETWORK, LOAD_NO_CACHE, or LOAD_CACHE_ONLY
        mWebSettings.setDefaultFixedFontSize(14); //기본 고정 글꼴 크기, value : 1~72 사이의 숫자



        StringBuffer receiveData = new StringBuffer();

        String str = null;
        try{
            str = "user_id=" + URLEncoder.encode(StaticUserInformation.userID, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        //mWebView.loadUrl("http://"+StaticInfo.my_ip+"/schline/android/schedule.do", str);
        //mWebView.loadUrl("http://172.30.1.1:9999/schline/android/schedule.do?user_id="+user_id);
        mWebView.loadUrl("http://172.30.1.1:9999/schline/android/schedule.do?user_id="+user_id);
        
        //mWebView.loadUrl("http://"+StaticInfo.my_ip+"schline/android/schedule.do?user_id="+user_id);

    }


}



