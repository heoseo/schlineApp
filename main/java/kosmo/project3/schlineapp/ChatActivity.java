package kosmo.project3.schlineapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.firebase.iid.FirebaseInstanceId;

public class ChatActivity extends Activity {

    private static final String TAG = "ChatActivity";

    TextView log;
    String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        log = (TextView)findViewById(R.id.log);

        //앱 실행할때 알림 메세지 있으면 데이터 표시
        Intent intent = getIntent();
        if(intent != null && intent.getExtras() != null){
            for(String key : getIntent().getExtras().keySet()){
                String value = getIntent().getExtras().getString(key);
                Log.d(TAG, "Noti - "+ key + ":" + value);
            }

            Log.i(TAG, "알림 메세지 도착");

            String contents = intent.getStringExtra("message");
            if(contents != null){
                processIntent(contents);
            }
        }
        getResistrationId();
    }

    //전달된 데이터를 텍스트뷰에 출력
    public void println(String data){
        log.append(data + "\n");
    }
    private void processIntent(String contents){
        println("DATA : " + contents);
    }

    //파이어베이스에서 토큰값을 얻어와서 출력
    public void getResistrationId(){
        println("getRegistrationId() 호출됨");

        regId = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "RegID:" + regId);
        println("regId : " + regId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        println("onNewIntent() called.");

        if(intent != null){
            String contents = intent.getStringExtra("message");
            processIntent(contents);
        }
    }
}