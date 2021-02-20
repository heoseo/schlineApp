package kosmo.project3.schlineapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

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

public class TeamView extends AppCompatActivity {

    String TAG = "SEONGJUN";

    private long enqueue;
    private DownloadManager dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_view);

        String user_id = StaticUserInformation.userID;
        Intent intent = getIntent();
        String board_idx = intent.getStringExtra("board_idx");

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
                String board_idx = jsonObject.getString("board_idx");
                String subject_idx = jsonObject.getString("subject_idx");
                String user_name = jsonObject.getString("user_name");
                String board_title = jsonObject.getString("board_title");
                String board_content = jsonObject.getString("board_content");
                String board_postdate = jsonObject.getString("board_postdate");
                String team_num = jsonObject.getString("team_num");


                //파일이 없는경우 optString으로 실패콜백값을 지정해줌
                String board_file = jsonObject.optString("board_file", "");
                Log.i(TAG, "파일은?" + board_file);

                String user_id = jsonObject.getString("user_id");

                TextView teamviewtitle = findViewById(R.id.teamviewtitle);
                TextView teamviewuser = findViewById(R.id.teamviewuser);
                TextView teamviewteamnum = findViewById(R.id.teamviewteamnum);
                TextView teamviewpostdate = findViewById(R.id.teamviewpostdate);
                TextView teamviewcontent = findViewById(R.id.teamviewcontent);
                //TextView teamviewfile = findViewById(R.id.teamviewfile);

                teamviewtitle.setText(board_title);
                teamviewcontent.setText(board_content);
                teamviewuser.setText(user_name);
                teamviewteamnum.setText(team_num+"팀");
                teamviewpostdate.setText(board_postdate);
                //teamviewfile.setText(board_file);

                Button btn = (Button)findViewById(R.id.teamviewfile);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String fileURL = "http://" + StaticInfo.my_ip + "/schline/resources/uploadsFile" + File.separator + board_file; // URL

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

    //AsyncTask로 다운로드...
    /*class Teamdownload extends AsyncTask<String, String, Boolean> {

        private String TAG = "DownloaderTask";
        public String fileUrl = null;
        public String filePath = null;
        public long fileSize = -1; public long downloadSize = 0;
        public Boolean downloadEnd = false;
        String folder;

        public Teamdownload(String url, String file) {
            this.fileUrl = url;
            this.filePath = file;

        }

        protected Boolean doInBackground(String... params) {
            Log.i( TAG, "doInBackground" );
            Boolean result = false;
            InputStream input = null;
            OutputStream output = null;
            URLConnection connection = null;

            try {

                folder = fileFolderDirectory();

                URL url = new URL(fileUrl);
                Log.i(TAG, "유알엘:"+url);
                connection = url.openConnection();
                connection.connect();
                fileSize = connection.getContentLength();
                Log.i(TAG, "fileLength : " + fileSize + "");
                input = new BufferedInputStream(url.openStream(), 8192);
                File outputFile = new File(getExternalFilesDir(folder), filePath);
                Log.i(TAG, "outputFile : " + outputFile + "");
                output = new FileOutputStream(outputFile);
                byte[] buffer = new byte[8192];

                for (int bytesRead; (bytesRead = input.read(buffer)) >= 0; ) {
                    if (isCancelled()) {
                        Log.i(TAG, "isCancelled : " + isCancelled() + "");
                        break;
                    }
                    downloadSize += bytesRead;
                    publishProgress(downloadSize + "");
                    output.write(buffer, 0, bytesRead);
                }

                output.flush();
                output.close();
                input.close();

                if (isCancelled()) {
                    if (outputFile.exists()) { // 다운로드 취소된 파일은 삭제
                        outputFile.delete();
                        Log.i(TAG, "outputFile.exists() : " + outputFile.exists() + "");
                    }
                    downloadEnd = true;
                    result = false;

                } else {
                    result = true;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                result = false;
            }
            return result;
        }

        @Override protected void onPreExecute() {
            super.onPreExecute();

            Log.i( TAG, "onPreExecute" );
        }

        @Override protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(fileSize == downloadSize) {
                downloadEnd = true;
                Toast.makeText(getApplicationContext(), "다운로드가 완료되었습니다.", Toast.LENGTH_LONG).show();
            }
            Log.i( TAG, "onPostExecute" );
        }

        @Override protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            long v = Long.parseLong(values[0]); //
            Log.i( TAG, "onProgressUpdate : " + v );

        }
    }*/

    //폴더 없을때 만들기
/*    public static String fileFolderDirectory() {
        String folder= Environment.DIRECTORY_DOWNLOADS + File.separator + "schline" + File.separator;
        //String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "schline" + File.separator;
        File directory = new File(folder);
        if(!directory.exists()){
            directory.mkdirs();
        }
        return folder;
    }*/
}