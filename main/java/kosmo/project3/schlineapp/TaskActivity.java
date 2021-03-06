package kosmo.project3.schlineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;


import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener{

    String TAG = "SEONGJUN";
    ListView tasklist;
    private ArrayList<TaskVO> list = new ArrayList<>();
    ArrayList<String> boardidxs = new ArrayList<>();

    //플로팅버튼 테스트
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private ExtendedFloatingActionButton fab, floatteam, floattask, floatlecture;
    String subject_idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);


        String user_id = StaticUserInformation.userID;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        subject_idx = intent.getStringExtra("subject_idx");
        Log.i(TAG, subject_idx);

        ////////////////플로팅버튼테스트//////////////////
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab = (ExtendedFloatingActionButton)findViewById(R.id.fab);
        floatteam = (ExtendedFloatingActionButton)findViewById(R.id.floatteam);
        floattask = (ExtendedFloatingActionButton)findViewById(R.id.floattask);
        floatlecture = (ExtendedFloatingActionButton)findViewById(R.id.floatlecture);

        fab.setOnClickListener(this);
        floatteam.setOnClickListener(this);
        floattask.setOnClickListener(this);
        floatlecture.setOnClickListener(this);

        new AsyncExamRequest().execute(
                "http://"+ StaticInfo.my_ip +"/schline/android/taskList.do",
                "user_id="+user_id, "subject_idx="+subject_idx);
    }

    //플로팅버튼용
    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id) {
            case R.id.fab:
                anim();
                break;
            case R.id.floattask:
                anim();
                intent = new Intent(view.getContext(), TaskActivity.class);
                intent.putExtra("subject_idx", subject_idx);
                finish();
                startActivity(intent);
                break;
            case R.id.floatteam:
                anim();
                intent = new Intent(view.getContext(), TeamActivity.class);
                intent.putExtra("subject_idx", subject_idx);
                finish();
                startActivity(intent);
                break;
            case R.id.floatlecture:
                anim();
                intent = new Intent(view.getContext(), LectureView.class);
                intent.putExtra("idx", subject_idx);
                finish();
                startActivity(intent);
                break;
        }
    }

    public void anim() {

        if (isFabOpen) {
            floatlecture.startAnimation(fab_close);
            floatteam.startAnimation(fab_close);
            floattask.startAnimation(fab_close);
            floatlecture.setClickable(false);
            floatteam.setClickable(false);
            floattask.setClickable(false);
            isFabOpen = false;
        } else {
            floatlecture.startAnimation(fab_open);
            floatteam.startAnimation(fab_open);
            floattask.startAnimation(fab_open);
            floatlecture.setClickable(true);
            floatteam.setClickable(true);
            floattask.setClickable(true);
            isFabOpen = true;
        }
    }

    class AsyncExamRequest extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer sBuffer = new StringBuffer();
            try{
                //0번째 인자를 통해 접속할URL로 객체를 생성한다.
                URL url = new URL(strings[0]);
                //URL을 연결한 객체 생성
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                //서버와 통신할때의 방식(GET or POST)
                connection.setRequestMethod("POST");
                //쓰기모드 지정
                connection.setDoOutput(true);

                OutputStream out = connection.getOutputStream();
                out.write(strings[1].getBytes());//아이디 전달
                out.write("&".getBytes());//&를 사용하여 쿼리스트링으로 만들어줌
                out.write(strings[2].getBytes());//과목전달

                out.flush();
                out.close();

                if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    Log.i(TAG, "HTTP OK 성공");
                    //서버로부터 받은 응답데이터(JSON)를 스트림을 통해 읽어 저장한다.
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "UTF-8")
                    );
                    String responseData;

                    while ((responseData=reader.readLine())!=null){
                        //내용을 한줄씩 읽어서 StringBuffer객체에 저장한다.
                        sBuffer.append(responseData+"\n\r");
                    }
                    reader.close();
                }
                else{
                    //서버 접속에 실패한경우..
                    Log.i(TAG, "HTTP OK 안됨");
                }
                ////////데이터로 변경//////
                //읽어온 JSON데이터를 로그로 출력
                Log.i(TAG, sBuffer.toString());
                //먼저 JSON배열로 파싱
                JSONArray jsonArray = new JSONArray(sBuffer.toString());
                //StringBuffer 객체를 비움
                sBuffer.setLength(0);
                //배열 크기만큼 반복
                for(int i=0; i<jsonArray.length(); i++){
                    //배열의 요소는 객체이므로 JSON객체로 파싱
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    //각 Key에 해당하는 값을 가져와서 StringBuffer객체에 저장
                    String exam_idx = jsonObject.getString("exam_idx");
                    String exam_name = jsonObject.getString("exam_name");
                    String exam_date = jsonObject.getString("exam_date");
                    String exam_content = jsonObject.getString("exam_content");
                    String exam_scoring = jsonObject.getString("exam_scoring");
                    String subject_idx = jsonObject.getString("subject_idx");
                    //과제일련번호 넣어보기..
                    boardidxs.add(exam_idx);
                    //VO객체에 넣기
                    TaskVO vo = new TaskVO();
                    vo.setExam_idx(exam_idx);
                    vo.setExam_name(exam_name);
                    vo.setExam_date(exam_date);
                    vo.setExam_content(exam_content);
                    vo.setExam_scoring(exam_scoring);
                    vo.setSubject_idx(subject_idx);
                    list.add(vo);
                    Log.i(TAG, "리스트담기까지..");
                }

            }
            catch(Exception e){
                e.printStackTrace();
            }

            return sBuffer.toString();
        }

        //doInBackground()가 정상종료되면 해당 함수가 호출된다

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i(TAG, "팀뷰어댑터?");
            //결과값을 텍스트뷰에 출력한다..?
            tasklist = (ListView)findViewById(R.id.tasklist);
            TaskAdaper taskAdaper = new TaskAdaper();
            tasklist.setAdapter(taskAdaper);
            tasklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.i(TAG, "어떤값이 넘어오나요? : "+boardidxs.size());
                    Intent intent = new Intent(adapterView.getContext(), TaskViewActivity.class);
                    intent.putExtra("board_idx", boardidxs.get(i));
                    intent.putExtra("subject_idx", list.get(i).getSubject_idx());
                    intent.putExtra("exam_name", list.get(i).getExam_name());
                    intent.putExtra("exam_idx", list.get(i).getExam_idx());

                    startActivity(intent);
                }
            });
        }
    }

    public class TaskAdaper extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public Object getItem(int i) {
            return list.get(i).getExam_name();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Log.i(TAG,"진입이 되나요?");

            TaskBoardLayout taskBoardLayout = new TaskBoardLayout(getApplicationContext());

            taskBoardLayout.settitle(list.get(i).getExam_name());
            taskBoardLayout.setuser(list.get(i).getExam_content());
            taskBoardLayout.setpostdate(list.get(i).getExam_date());

            return taskBoardLayout;
        }
    }
}