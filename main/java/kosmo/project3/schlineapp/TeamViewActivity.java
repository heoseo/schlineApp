package kosmo.project3.schlineapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import kosmo.project3.schlineapp.R;
import kosmo.project3.schlineapp.StaticInfo;
import kosmo.project3.schlineapp.StaticUserInformation;

public class TeamViewActivity extends AppCompatActivity {

    String TAG = "SEONGJUN";
    public static Context teamviewcontext;

    private long enqueue;
    private DownloadManager dm;
    Button teamdelBtn;
    Button teamEditBtn;
    String user_id;
    String board_file;
    String board_idx;
    String subject_idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_view);

        teamviewcontext = this;

        user_id = StaticUserInformation.userID;
        Intent intent = getIntent();
        board_idx = intent.getStringExtra("board_idx");

        teamEditBtn = (Button)findViewById(R.id.teamEditBtn);
        teamdelBtn = (Button)findViewById(R.id.teamdelBtn);


        teamdelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

                alert.setCancelable(true);
                alert.setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("삭제주의")
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new AsyncTeamDel().execute(
                                    "http://"+ StaticInfo.my_ip +"/schline/android/teamDelete.do",
                                    "user_id="+user_id, "board_idx="+board_idx, "board_file="+board_file);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(), "삭제를 취소하였습니다",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

        teamEditBtn.setVisibility(View.INVISIBLE);
        teamdelBtn.setVisibility(View.INVISIBLE);

        new AsyncTeamViewRequest().execute(
                "http://"+ StaticInfo.my_ip +"/schline/android/teamView.do",
                "user_id="+user_id, "board_idx="+board_idx);
    }

    class AsyncTeamViewRequest extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer sBuffer = new StringBuffer();
            try {
                //0번째 인자를 통해 접속할URL로 객체를 생성한다.
                URL url = new URL(strings[0]);
                //URL을 연결한 객체 생성
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //서버와 통신할때의 방식(GET or POST)
                connection.setRequestMethod("POST");
                //쓰기모드 지정
                connection.setDoOutput(true);

                OutputStream out = connection.getOutputStream();
                out.write(strings[1].getBytes());//아이디 전달
                out.write("&".getBytes());//&를 사용하여 쿼리스트링으로 만들어줌
                out.write(strings[2].getBytes());//게시판일련번호전달

                out.flush();
                out.close();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.i(TAG, "HTTP OK 성공");
                    //서버로부터 받은 응답데이터(JSON)를 스트림을 통해 읽어 저장한다.
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "UTF-8")
                    );
                    String responseData;

                    while ((responseData = reader.readLine()) != null) {
                        //내용을 한줄씩 읽어서 StringBuffer객체에 저장한다.
                        sBuffer.append(responseData + "\n\r");
                    }
                    reader.close();
                } else {
                    //서버 접속에 실패한경우..
                    Log.i(TAG, "HTTP OK 안됨");
                }

                //읽어온 JSON데이터를 로그로 출력
                Log.i(TAG, sBuffer.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return sBuffer.toString();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //JSONObject 파싱
            try{
                JSONObject jsonObject = new JSONObject(s);
                board_idx = jsonObject.getString("board_idx");
                subject_idx = jsonObject.getString("subject_idx");
                String user_name = jsonObject.getString("user_name");
                String board_title = jsonObject.getString("board_title");
                String board_content = jsonObject.getString("board_content");
                String board_postdate = jsonObject.getString("board_postdate");
                String team_num = jsonObject.getString("team_num");


                //파일이 없는경우 optString으로 실패콜백값을 지정해줌
                board_file = jsonObject.optString("board_file", "");
                Log.i(TAG, "파일은?" + board_file);


                String getid = jsonObject.getString("user_id");
                if(getid.equals(user_id)){
                    teamEditBtn.setVisibility(View.VISIBLE);
                    teamdelBtn.setVisibility(View.VISIBLE);
                }

                TextView teamviewtitle = findViewById(R.id.teamviewtitle);
                TextView teamviewuser = findViewById(R.id.teamviewuser);
                TextView teamviewteamnum = findViewById(R.id.teamviewteamnum);
                TextView teamviewpostdate = findViewById(R.id.teamviewpostdate);
                TextView teamviewcontent = findViewById(R.id.teamviewcontent);
                TextView teamviewfilename = findViewById(R.id.teamviewfilename);

                teamviewtitle.setText(board_title);
                teamviewcontent.setText(board_content);
                teamviewuser.setText(user_name);
                teamviewteamnum.setText(team_num+"팀");
                teamviewpostdate.setText(board_postdate);
                teamviewfilename.setText(board_file);

                Button btn = (Button)findViewById(R.id.teamviewfiledown);
                if(board_file.equals("")){
                    btn.setVisibility(View.INVISIBLE);
                }

                teamEditBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent editintent = new Intent(getApplicationContext(), TeamEditActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("board_content", board_content);
                        bundle.putString("board_title", board_title);
                        bundle.putString("board_file", board_file);
                        bundle.putString("board_idx", board_idx);
                        editintent.putExtra("teambundle", bundle);
                        startActivity(editintent);
                    }
                });

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String fileURL = "http://" + StaticInfo.my_ip + "/schline/resources/uploadsFile/team" + File.separator + board_file; // URL

                        Log.i(TAG, "URL:" + fileURL + " 파일명:" + board_file);

                        String ready_filename = board_file;

                        //다운로드매니저 실행

                        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileURL));
                        request.setTitle(ready_filename);              // 다운로드 제목
                        request.setNotificationVisibility(1);  // 상단바에 완료 결과 놔둠. 0 은 안뜸
                        enqueue = dm.enqueue(request);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, ready_filename);
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();

                        Toast.makeText(getApplicationContext(), "다운로드 완료", Toast.LENGTH_LONG).show();

                    }
                });
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    class AsyncTeamDel extends AsyncTask <String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String responseData = "";
            String resultstr = "";
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
                out.write("&".getBytes());//&를 사용하여 쿼리스트링으로 만들어줌
                out.write((strings[3].getBytes()));//파일명전달

                out.flush();
                out.close();

                if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    Log.i(TAG, "HTTP OK 성공");
                    //서버로부터 받은 응답데이터(JSON)를 스트림을 통해 읽어 저장한다.
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "UTF-8")
                    );

                   responseData=reader.readLine();

                    reader.close();
                }
                else{
                    //서버 접속에 실패한경우..
                    Log.i(TAG, "HTTP OK 안됨");
                }
                ////////데이터로 변경//////
                //읽어온 JSON데이터를 로그로 출력
                Log.i(TAG, "읽은값:"+responseData);
                JSONObject jsonObject = new JSONObject(responseData);

                //Key 가져와서 파싱
                int result = jsonObject.getInt("success");
                resultstr = Integer.toString(result);

            }
            catch(Exception e){
                e.printStackTrace();
            }

            return resultstr;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i(TAG, "결과:"+s);
            if (s != null) {
                //결과데이터를 텍스트뷰에 출력
                if (s.equals("1")) {
                    Toast.makeText(getApplicationContext(), "삭제완료",
                            Toast.LENGTH_LONG).show();
                    TeamActivity ta = (TeamActivity)TeamActivity.teamcontext;
                    ta.finish();
                    Intent intent = new Intent(getApplicationContext(), TeamActivity.class);
                    intent.putExtra("subject_idx", subject_idx);
                    finish();
                    startActivity(intent);
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "삭제실패",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}