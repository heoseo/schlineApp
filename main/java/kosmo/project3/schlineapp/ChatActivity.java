package kosmo.project3.schlineapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//파이어베이스 채팅
public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    TextView time_out, myRec;
    //int cur_Status = Init; //현재의 상태를 저장할변수를 초기화함.
    int myCount=1;
    long myBaseTime;
    ListView list_chat;
    EditText edit_chat;
    Button btn_chat;

    private RecyclerView mRecyclerView;
    public  RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ChatData> chatList;
    private String nick;
    private String id;
    private String img;
    private String today;

    private EditText EditText_chat;
    private Button Button_send;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    //파이어베이스 사용을 위한 코드추가
    //private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    //private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ///데이타베이스 접속하고 파베쓰기
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("채팅!");

        ////파베 읽어오기



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

        //배경화면 설정
        LinearLayout cb = findViewById(R.id.chat_back);
        //현재시간
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        //int d = date.getHours();
        //날짜 가져오기
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMdd");//폰날짜설정해야함
        today = simpleDateFormat.format(date);
        Log.i(TAG, "today="+today);


        SimpleDateFormat CurHourFormat = new SimpleDateFormat("hh");
        int d = Integer.parseInt(CurHourFormat.format(date));

        Log.i(TAG, "현재시간"+d);
        //(4~7):b1, (7~11)b2, (11~17)b3, (17~22)b4, (22~4):b5
        if(d>=4 && d<7) {
            cb.setBackground(getDrawable(R.drawable.b1));
        }
        else if(d>=7 && d<11) {
            cb.setBackground(getDrawable(R.drawable.b2));
        }
        else if(d>=11 && d<17) {
            cb.setBackground(getDrawable(R.drawable.b3));
        }
        else if(d>=17 && d<22) {
            cb.setBackground(getDrawable(R.drawable.b4));
        }
        else {
            cb.setBackground(getDrawable(R.drawable.b5));
        }

        //10초에 한번씩 db연결 공부시간저장
        Timer timer = new Timer();
        TimerTask TT = new TimerTask() {
            @Override public void run() {
                new syncTimeServer().execute(
                        "http://"+StaticInfo.my_ip + "/schline/android/studyTimeSet.do",
                        "user_id="+StaticUserInformation.userID
                );
            }
        };
        timer.schedule(TT, 0, 10000);//Timer 실행
        // timer.cancel();//타이머 종료


        //출석증가
        new syncAttenServer().execute(
                "http://"+StaticInfo.my_ip + "/schline/android/attenPlus.doo",
                "user_id="+StaticUserInformation.userID,
                "today="+today
        );


        /////////////FireBase//////////////
        /*Intent intent = getIntent();//인텐트로 정보 가져오기
        nick = intent.getStringExtra("info_nick");//유저닉네임
        img = intent.getStringExtra("info_img");
        id = StaticUserInformation.userID;

        Button_send = findViewById(R.id.Button_send);
        EditText_chat = findViewById(R.id.EditText_chat);

        //전송버튼
       Button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = EditText_chat.getText().toString(); //msg

                if(msg != null) {
                    ChatData chat = new ChatData();
                    chat.setNickname(nick);
                    chat.setMsg(msg);
                    //chat.setImg(img);//추가
                    myRef.push().setValue(chat);
                }
            }
        });

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        chatList = new ArrayList<>();
        mAdapter = new ChatAdapter(chatList, ChatActivity.this, nick);

        mRecyclerView.setAdapter(mAdapter);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        //caution!!!
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("CHATCHAT", dataSnapshot.getValue().toString());
                ChatData chat = dataSnapshot.getValue(ChatData.class);
                ((ChatAdapter) mAdapter).addChat(chat);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
*/


    }////onCreate 끝


    Handler myTimer = new Handler(){
        public void handleMessage(Message msg){
            time_out.setText(getTimeOut());

            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송하는겁니다.
            myTimer.sendEmptyMessage(0);
        }
    };

    //시간저장
    class syncTimeServer extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
        @Override
        protected String doInBackground(String... strings) {
            StringBuffer sBuffer = new StringBuffer();

            try{
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                out.write(strings[1].getBytes());
                out.flush();
                out.close();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                    Log.i(TAG, "공부시간저장");
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8")
                    );
                    String responseDate;
                    while ((responseDate = reader.readLine())!=null){
                        sBuffer.append(responseDate+"\r\n");
                        Log.i(TAG, "공부시간 저장 중"+sBuffer);
                    }
                    reader.close();
                }
                else {
                    Log.i(TAG, "공부시간저장 연결 실패");
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    class syncAttenServer extends AsyncTask<String, Void, String>{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
        @Override
        protected String doInBackground(String... strings) {
            StringBuffer sBuffer = new StringBuffer();

            try{
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                out.write(strings[1].getBytes());
                out.write("&".getBytes());
                out.write(strings[2].getBytes());
                out.flush();
                out.close();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                    Log.i(TAG, "출석체크");
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8")
                    );
                    String responseDate;
                    if ((responseDate = reader.readLine())!=null){
                        sBuffer.append(responseDate+"\r\n");
                        Log.i(TAG, "출석체크결과"+sBuffer);
                    }
                    reader.close();
                }
                else {
                    Log.i(TAG, "출석체크 연결 실패");
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    //현재시간을 계속 구해서 출력하는 메소드
    String getTimeOut(){
        long now = SystemClock.elapsedRealtime();
        long outTime = now - myBaseTime;
        String easy_outTime = String.format("%02d:%02d", outTime/1000 / 60, (outTime/1000)%60);
        return easy_outTime;
    }
}