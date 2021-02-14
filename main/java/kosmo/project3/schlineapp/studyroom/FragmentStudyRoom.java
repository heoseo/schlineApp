package kosmo.project3.schlineapp.studyroom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import kosmo.project3.schlineapp.R;
import kosmo.project3.schlineapp.StaticInfo;
import kosmo.project3.schlineapp.StaticUserInformation;
import kosmo.project3.schlineapp.studyroom.StudyroomChatActivity;

//프레그먼트 상속
@SuppressLint("HandlerLeak")
public class FragmentStudyRoom extends Fragment implements Runnable{

    //전역변수 선언
    private static final String TAG = "FragmentStudyRoom";
    Bitmap bitmap;// 비트맵 객체
    //위젯용변수
    TextView textNICK, textTime, textATTN, textBLOCK;
    ImageView imgINFO;
    Button btnStudyGO;
    //제이슨 파싱용 변수
    String info_nick, reported_count, info_img, info_attend, info_time;
    URL url;
    //Integer info_attend, info_time;
    String user_id = StaticUserInformation.userID;
    //info_img도 있어야함
    ViewGroup studyRoomView;

    // 메인 스레드와 백그라운드 스레드 간의 통신
    Handler handler;
    {
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.i(TAG, "핸들러 들어옴");
                Log.i(TAG, "최종bitmap=" + bitmap);
                // 서버에서 받아온 이미지를 핸들러를 경유해 이미지뷰에 비트맵 리소스 연결
                imgINFO.setImageBitmap(bitmap);
            }
        };
    }


    //레이아웃 전개 출발점 onCreateView
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //레이아웃 전개
        //자바코드로 프레그먼트를 추가할때는 inflate()의 세번째 인자는 false로 처리
        studyRoomView = (ViewGroup)inflater.inflate(R.layout.fragment_studyroom, container, false);
        //위젯 가져오기
        textNICK = (TextView)studyRoomView.findViewById(R.id.text_nick);//얘의값을 셋팅해줘야한다.
        //imgINFO = (ImageView)studyRoomView.findViewById(R.id.img_info);
        textTime = (TextView)studyRoomView.findViewById(R.id.text_time);
        textBLOCK = (TextView)studyRoomView.findViewById(R.id.text_reported_count);
        textATTN = (TextView)studyRoomView.findViewById(R.id.text_attend);
        btnStudyGO = (Button)studyRoomView.findViewById(R.id.btn_studyRoomGo);
        imgINFO = (ImageView)studyRoomView.findViewById(R.id.img_info);

        //이미지뷰 모서리 둥글게
        imgINFO.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imgINFO.setClipToOutline(true);
        }

        Button editBtn;
        //이클립스, DB연동 사용자정보 요청
        new syncInfoServer().execute(
                //getString(R.string.server_addr)  +"/schline/android/class/study.do",
                "http://"+StaticInfo.my_ip  +"/schline/android/class/study.do",
                "user_id="+StaticUserInformation.userID
        );

        //채팅 이동
        btnStudyGO.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(),
                                StudyroomChatActivity.class);
                        //채팅을 띄워준다.
                        startActivity(intent);
                    }
                }
        );

        //채팅이동 2222 (임시)
        Button bt2 = (Button)studyRoomView.findViewById(R.id.btn_studyRoomGo2);
        bt2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(),
                                ChatActivity.class);
                        //채팅2이동
                        startActivity(intent);
                    }
                }
        );

        editBtn = (Button)studyRoomView.findViewById(R.id.btn_editInfo);
        editBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(),
                                EditInfoActivity.class);
                        Log.i(TAG,"넘길 info 이미지="+ info_img);
                        intent.putExtra("img", info_img);
                        intent.putExtra("url", url);
                        //프로필수정 이동
                        startActivity(intent);
                    }
                }
        );


        return studyRoomView;
    }/////onCreateView 끝

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }




    // 백그라운드 스레드
    @Override
    public void run() {
        URL url =null;
        try{
            // 스트링 주소를 url 형식으로 변환
            url = new URL("http://"+StaticInfo.my_ip+"/schline/resources/profile_image"+File.separator+info_img);
            //url = new URL("http://localhost:9999//resources/profile_image"+File.separator+info_img);
            Log.i(TAG, "url최종="+url);
            // url에 접속 시도
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.connect();
            // 스트림 생성
            InputStream is = conn.getInputStream();
            // 스트림에서 받은 데이터를 비트맵 변환
            // 인터넷에서 이미지 가져올 때는 Bitmap을 사용해야함
            bitmap = BitmapFactory.decodeStream(is);

            // 핸들러에게 화면 갱신을 요청한다.
            handler.sendEmptyMessage(0);
            // 연결 종료
            is.close();
            conn.disconnect();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //요청 회원정보 받아오기
    class syncInfoServer extends AsyncTask<String, Void, String>{
        //doInBackground() 실행되기 전에 호출됨
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /*
        execute() 호출시 전달된 파라미터를 가변인자가 받게된다.
        가변인자는 여러개의 파라미터를 받을수 있는 매개변수로 배열로
        사용하게된다.
         */
        @Override
        protected String doInBackground(String... strings) {

            //스프링 서버에서 반환하는 JSON데이터를 저장할 변수
            StringBuffer sBuffer = new StringBuffer();

            try {
                Log.i(TAG, "공부방 doInbackground");

                URL url = new URL(strings[0]);//파라미터1 : 요청URL
                //URL연결할 객체 생성
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                //서버와 통신방식 설정
                conn.setRequestMethod("POST");
                //쓰기모드 지정
                conn.setDoOutput(true);
                //요청할 파라미터 설정
                OutputStream out = conn.getOutputStream();
                out.write(strings[1].getBytes());//파라미터2 : 아이디
                out.flush();
                out.close();

                //서버에 요청 전달후 성공여부 확인
                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                    Log.i(TAG, "공부방메인 HTTP_OK.연결성공");
                    Log.i(TAG, "conn.getInputStream()="+conn.getInputStream());

                    //읽어오기
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(),"UTF-8")
                    );

                    String responseData;
                    while ((responseData = reader.readLine()) != null) {
                        Log.i(TAG, "결과값 저장중"+reader.readLine());//null
                        //내용을 한줄씩 읽어 StringBuffer객체에 저장
                        sBuffer.append(responseData+"\r\n");
                        Log.i(TAG, "결과값 저장중"+sBuffer);//결과나옴
                    }
                    reader.close();
                }
                else {//접속실패
                    Log.i(TAG, "공부방 HTTP_OK 실패");
                }

                //JSON객체 파싱
                JSONObject jsonObject = new JSONObject(sBuffer.toString());

                Log.i(TAG, "프로필불러오기 성공");
                //프래그먼트 안 intent등 생성시 getContext(), getApplicationContext() 안먹힐때
                //Toast.makeText(getActivity().getApplicationContext() ,"프로필 불러오기 성공",Toast.LENGTH_LONG).show();
                //객체안에 또다른 객체가 있으므로 getJSONObject를 통해 파싱
                user_id = jsonObject.getJSONObject("user").getString("user_id").toString();
                info_nick = jsonObject.getJSONObject("user").getString("info_nick").toString();
                info_img = jsonObject.getJSONObject("user").getString("info_img").toString();
                info_time = jsonObject.getJSONObject("user").getString("info_time").toString();
                info_attend = jsonObject.getJSONObject("user").getString("info_atten").toString();
                reported_count = jsonObject.getJSONObject("user").getString("reported_count").toString();

                Log.i(TAG, "공부방메인 파싱 user_id="+user_id);
                Log.i(TAG, "공부방메인 파싱 info_nick="+info_nick);
                Log.i(TAG, "공부방메인 파싱 info_img="+info_img);


            }catch (Exception e){
                e.printStackTrace();
            }

            //String path = Environment.getExternalStorageDirectory().getAbsolutePath()+
              //      File.separator+"profile_img"+File.separator;
            ///path에는 "sdcard/ImageList/" 와 같은 값이 들어갑니다.
            //경로를 이용해 File객체 생성
            //File list = new File(path);
            //list객체에서 이미지목록만 추려냄

            /*
            파싱이 완료된 StringBuffer객체 String으로 변환하여 반환
            반환된 값은 onPostExecute()로 전달됨
             */
            textNICK.setText(info_nick);
            textTime.setText(info_time);
            textBLOCK.setText(reported_count);
            textATTN.setText(info_attend);

            //imgINFO.setImageBitmap(url);
            // 백그라운드 스레드 생성
            Thread th =new Thread(FragmentStudyRoom.this);
            // 동작 수행
            th.start();

            return sBuffer.toString();
        }


        //doInBackground()에서 반환한 값은 해당 메소드로 전달된다.
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        //doInBackground()가 정상종료되면 해당 함수가 호출된다.
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }
}
