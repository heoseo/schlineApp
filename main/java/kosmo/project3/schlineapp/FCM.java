package kosmo.project3.schlineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import kosmo.project3.schlineapp.R;

public class FCM extends AppCompatActivity {

    private  static final String TAG = "FCM";

    TextView log;
    String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_c_m);

        log = (TextView) findViewById(R.id.log);

        //앱실행시 알림이 있으면 데이터 표시
        Intent intent = getIntent();
        if(intent != null && intent.getExtras() != null){
            for(String key : getIntent().getExtras().keySet()){
                String value = getIntent().getExtras().getString(key);
                Log.d(TAG,"Noti - "+ key + ":" + value);
            }
            Log.d(TAG,"알림 메시지가 있어요");
                //알람 클릭해서 앱 실행해야 표시됨 앱만 실행하면 시스템에
                //남아는 있고 데이터가 표시되지 않음
                String contents = intent.getStringExtra("message");
                if(contents != null){
                    processIntent(contents);
                }
        }
        getRegistrationId();
    }
    public void getRegistrationId(){
        println("getRegistrationId() 호출됨");

        regId = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"RegID:"+regId);
        println("regId : "+regId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        println("onNewIntent() called");
        String contents = intent.getStringExtra("message");
        processIntent(contents);
    }

    private void processIntent(String contents) {
        println("DATA : "+ contents);
    }
    public void println(String data){
        log.append(data+"\n");
    }

}