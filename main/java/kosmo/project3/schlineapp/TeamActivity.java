package kosmo.project3.schlineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TeamActivity extends AppCompatActivity {

    String TAG = "SEONGJUN";
    ListView teamlist;
    private ArrayList<TeamVO> list = new ArrayList<>();
    ArrayList<String> boardidxs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        String user_id = StaticUserInformation.userID;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String subject_idx = intent.getStringExtra("subject_idx");
        Log.i(TAG, subject_idx);
        new AsyncTeamRequest().execute(
                "http://"+ StaticInfo.my_ip +"/schline/android/teamList.do",
                "user_id="+user_id, "subject_idx="+subject_idx);
    }

    class AsyncTeamRequest extends AsyncTask<String, Void, String>{

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
                    String board_idx = jsonObject.getString("board_idx");
                    String user_name = jsonObject.getString("user_name");
                    String board_title = jsonObject.getString("board_title");
                    String board_content = jsonObject.getString("board_content");
                    String board_postdate = jsonObject.getString("board_postdate");
                    //보드일련번호 넣어보기..
                    boardidxs.add(board_idx);
                    //VO객체에 넣기
                    TeamVO vo = new TeamVO();
                    vo.setBoard_idx(board_idx);
                    vo.setBoard_title(board_title);
                    vo.setUser_name(user_name);
                    vo.setBoard_content(board_content);
                    vo.setBoard_postdate(board_postdate);
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
            teamlist = (ListView)findViewById(R.id.teamlist);
            TeamAdapter teamadapter = new TeamAdapter();
            teamlist.setAdapter(teamadapter);
            teamlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.i(TAG, "어떤값이 넘어오나요? : "+boardidxs.size());
                    Intent intent = new Intent(adapterView.getContext(), TeamView.class);
                    intent.putExtra("board_idx", boardidxs.get(i));

                    startActivity(intent);
                }
            });
        }
    }

    public class TeamAdapter extends BaseAdapter {

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
            return list.get(i).getBoard_title();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Log.i(TAG,"진입이 되나요?");

            TeamBoardLayout teamBoardLayout = new TeamBoardLayout(getApplicationContext());

            teamBoardLayout.settitle(list.get(i).getBoard_title());
            teamBoardLayout.setuser(list.get(i).getUser_name());
            teamBoardLayout.setpostdate(list.get(i).getBoard_postdate());

            return teamBoardLayout;
        }
    }
}