package kosmo.project3.schlineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TeamActivity extends AppCompatActivity {

    String TAG = "SEONGJUN";
    ProgressDialog dialog;
    ListView teamview;
    ArrayList<String> board_idx = new  ArrayList<String>();
    ArrayList<String> board_content = new  ArrayList<String>();
    ArrayList<String> board_title = new  ArrayList<String>();
    ArrayList<String> board_postdate = new  ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        String user_id = StaticUserInformation.userID;


        new AsyncTeamRequest().execute(
                "http://"+ StaticInfo.my_ip +"/schline/android/teamList.do",
                "userID="+user_id);

        //서버와 통신시 진행대화창을 띄우기 위한 객체생성
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//스타일설정
        dialog.setIcon(android.R.drawable.ic_dialog_alert);//아이콘설정
        dialog.setTitle("회원정보 리스트 가져오기");//제목
        dialog.setMessage("서버로부터 응답을 기다리고 있습니다.");//출력할 내용
        dialog.setCancelable(false);//back버튼으로 닫히지 않도록 설정

    }

    class AsyncTeamRequest extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //서버와 통신 직전에 진행대화창을 띄워준다.
            if(!dialog.isShowing())
                dialog.show(); //대화창이 없다면 show()를 통해 창을 띄운다.
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
                ////////데이터로 변경///////
                //누른 버튼이 "회원리스트가져오기"라면...
                //if(buttonResId==R.id.btn_json){
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
                        board_idx.add(jsonObject.getString("board_idx"));
                        board_title.add(jsonObject.getString("board_title"));
                        board_content.add(jsonObject.getString("board_content"));
                        board_postdate.add(jsonObject.getString("board_postdate"));
                    }
                //}
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
            //진행대화창을 닫아준다.
            dialog.dismiss();
            //결과값을 텍스트뷰에 출력한다..
            teamview = (ListView)findViewById(R.id.teamView);
            TeamAdapter teamAdapter = new TeamAdapter();
            teamview.setAdapter(teamAdapter);

            teamview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Toast.makeText(getApplicationContext(), "테스트", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    class TeamAdapter extends BaseAdapter{

        @Override
        public int getCount() {
           return board_idx.size();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public Object getItem(int i) {
            return board_idx.get(i);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Log.i("SEONGJUN","진입이 되나요?");
            return null;
        }
    }
}