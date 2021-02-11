package kosmo.project3.schlineapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

//프레그먼트 상속
public class FragmentStudyRoom extends Fragment {

    private static final String TAG = "FragmentStudyRoom";

    ProgressDialog dialog;
    //ArrayList<InfoVO> infoLANK;//랭킹
    TextView textLANK, textNICK, textTime, textATTN, textBLOCK;
    ImageView imgINFO;
    Button btnStudyGO;

    //레이아웃 전개 출발점 onCreateView
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //레이아웃 전개
        //자바코드로 프레그먼트를 추가할때는 inflate()의 세번째 인자는 false로 처리
        View studyRoomView = inflater.inflate(R.layout.fragment_studyroom, container, false);
        //위젯 가져오기
        textLANK = (TextView)studyRoomView.findViewById(R.id.text_lank);
        textNICK = (TextView)studyRoomView.findViewById(R.id.text_nick);//얘의값을 셋팅해줘야한다.
        imgINFO = (ImageView)studyRoomView.findViewById(R.id.img_info);

        /*
        위젯에서 가져온값 설정하기
            : 지역변수는 익명클래스 내부에서 사용하기 위해 반드시 final로 선언
            아래버튼의 리스너 내부에서 사용된다.
         */
        //textNICK.setText();

        //이클립스 연동해서 유저정보 가져오기
        //이클립스, DB연동
        new InfoAsyncHttpRequest().execute(
                "http://"+ StaticInfo.my_ip +"/schline/android/class/study.do",
                "user_id="+ StaticUserInformation.userID
        );

        //서버와 통신시 진행대화창 띄우기 위한 객체
        //dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//스타일설정
        dialog.setIcon(android.R.drawable.ic_dialog_alert);//아이콘설정
        dialog.setTitle("공부방 입장하기");//제목
        dialog.setMessage("서버로부터 응답을 기다리고있습니다");//출력내용
        dialog.setCancelable(false);//back버튼으로 닫히지 않도록 설정

        //공부방 채팅 이동하기
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

        return studyRoomView;
        //setHasOptionsMenu(true);
        //return inflater.inflate(R.layout.fragment_studyroom, container, false);
    }/////onCreateView 끝


    //공부방 메인 접속시 회원정보 가져오기
    class InfoAsyncHttpRequest extends AsyncTask<String, Void, String>{
        //doInBackground() 실행되기 전에 호출됨
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //서버와 통신직전에 진행대화창 띄워줌
            if(!dialog.isShowing())
                dialog.show();//대화창이 없다면 show()를 통해 창을 띄운다.
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
                Log.i(TAG, "url="+url);
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

                    //스프링 서버 연결성공한경우 응답된 JSON데이터를 스트림으로 읽어 저장
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(),"UTF-8")
                    );
                    String responseData;
                    while ((responseData = reader.readLine()) != null) {
                        //내용을 한줄씩 읽어 StringBuffer객체에 저장
                        sBuffer.append(reader+"\r\n");
                    }
                    reader.close();
                }
                else {//접속실패
                    Log.i(TAG, "공부방 HTTP_OK 실패");
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            /*
            파싱이 완료된 StringBuffer객체 String으로 변환하여 반환
            반환된 값은 onPostExecute()로 전달됨
             */
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

            //진행대화창을 닫아준다.
            dialog.dismiss();

            //결과값을 출력한다.
            StringBuffer sb = new StringBuffer();

            try {
                //JSON객체 파싱
                JSONObject jsonObject = new JSONObject(s);
                int success = Integer.parseInt(jsonObject.getString("user"));

                Log.i(TAG, "success ="+success);

                if(success==1){
                    sb.append("공부방메인 입장성공");
                    //객체안에 또다른 객체가 있으므로 getJSONObject를 통해 파싱
                    String user_id = jsonObject.getJSONObject("user").getString("user_id").toString();
                    String info_nick = jsonObject.getJSONObject("user").getString("info_nick").toString();
                    String info_img = jsonObject.getJSONObject("user").getString("info_img").toString();
                    String info_time = jsonObject.getJSONObject("user").getString("info_time").toString();
                    String info_atten = jsonObject.getJSONObject("user").getString("info_atten").toString();
                    String reported_count = jsonObject.getJSONObject("user").getString("reported_count").toString();

                    //이거 어떠케 ?
                    //ArrayList lists = jsonObject.getJSONObject("LankList").toString();


                    sb.append("회원정보");
                    sb.append("아이디"+user_id+"\n");
                    sb.append("닉네임"+info_nick+"\n");
                    sb.append("이미지"+info_img+"\n");
                    sb.append("출석횟수"+info_atten+"\n");

                }
                else{
                    sb.append("공부방메인 입장실패");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            //통신이 끝나면 진행창을 닫고 뷰에 결과를 출력한다.
            //textLANK.setText();
            //(TextView)textLANK.setText();
            //(TextView)textATTN.setText(info_atten);
            //(TextView)textBLOCK.setText(reported_count);
            //(TextView)textNICK.setText(info_nick);
            //(TextView)textTime.setText(info_time);
            //(ImageView)imgINFO.setImageResource(info_img);
        }
    }
}
