package kosmo.project3.schlineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class StudyRoomActivity extends AppCompatActivity {

    String TAG = "StudyRoomActivity";
    //사용자 정보를 알아와서 채팅방 열어주기
    String user_id;
    String info_nick;
    String info_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room);

        /*
        Intent를 통해 부가데이터를 전달하는 Bundle객체를 통해
        액티비티간에 전송된다. 액티비티가 실행될때 제일 처음
        실행되는 생명주기 함수가 onCreate()인데 이때 파라미터
        형태로 전송받게된다.
         */
        Intent intent = getIntent();//메소드를 통해 Intent객체 얻어오기
        Bundle bundle = intent.getExtras();//번들객체 얻어옴
        //user_id = bundle.getIntArray("user_id");
        //info_nick = bundle.getIntArray("info_nick");
        //info_img = bundle.getIntArray("info_img");

        //얻어온값을 세팅한다.

    }
}