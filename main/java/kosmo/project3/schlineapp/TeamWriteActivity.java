package kosmo.project3.schlineapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class TeamWriteActivity extends AppCompatActivity {

    private static final String TAG = "SEONGJUN";

    EditText title;
    EditText content;
    String filePath1;//파일의 절대경로
    TextView settingfile;
    String subject_idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_write);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    public void btnTeamWrite(View view){
        Intent intent = getIntent();
        title = findViewById(R.id.teamwritetitle);
        content = findViewById(R.id.teamwritecontent);
        subject_idx = intent.getStringExtra("subject_idx");
        HashMap<String, String> param1 = new HashMap<>();
        param1.put("user_id", StaticUserInformation.userID);
        param1.put("board_title", title.getText().toString());
        param1.put("board_content", content.getText().toString());
        param1.put("subject_idx", subject_idx);

        Log.i(TAG, param1.get("user_id")+" "+param1.get("board_title")+" "+param1.get("board_content")+" "+param1.get("subject_idx")+" "+param1.get("exam_name"));

        HashMap<String, String> param2 = new HashMap<>();
        param2.put("filename", filePath1);

        AsyncTeamWrite taskWrite = new AsyncTeamWrite(getApplicationContext(), param1, param2);

        taskWrite.execute();

    }

    public void btnTeamUpload(View view){

        Intent it = new Intent();
        it.setType("application/*");
        it.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(it, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                Log.i(TAG, "URI는:"+uri.toString());
                showfile(uri);
            }
        }
    }

    private void showfile(Uri imageUri) {

        filePath1 = getRealPathFromURI(imageUri);//사용자정의함수
        Log.i(TAG, "path1:" + filePath1);
        String[] filenames = filePath1.split("/");
        String filename = filenames[filenames.length-1];
        Log.i(TAG, filename);
        settingfile = findViewById(R.id.teamfile);
        settingfile.setText(filename);
    }

    private String getRealPathFromURI(Uri uri) {

        Cursor returnCursor =
                getContentResolver().query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String path = returnCursor.getString(returnCursor.getColumnIndex("_data"));

        Log.i(TAG, "경로는:" + path);
        return path;


/*        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null );
        cursor.moveToNext();
        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );

        cursor.close();
        return path;*/

        /*int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);*/
    }



    class AsyncTeamWrite extends AsyncTask<Object, Integer, JSONObject>{

        private Context mContext;
        private HashMap<String, String> param;
        private HashMap<String, String> files;

        public AsyncTeamWrite(Context context, HashMap<String, String> param, HashMap<String, String> files) {
            mContext = context;
            this.param = param;
            this.files = files;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            JSONObject rtn = null;
            try {
                //프로젝트명이나 요청명이 변경될 수 있음
                //따라서 서비스URL은 리소스의 상수로 저장하는것이 좋다.
                String sUrl =  "http://"+ StaticInfo.my_ip +
                        "/schline/android/teamUpload.do";
                //단말기의 사진을 서버로 업로드하기위한 객체생성 및 메소드호출
                //FileUpload 클래스는 기존내용을 그대로 가져다 쓰면 됨(수정필요없음)
                Log.i(TAG, "파일명확인:"+files.get("filename"));
                if(files.get("filename")!=null) {

                    FileUpload multipartUpload = new FileUpload(sUrl, "UTF-8");
                    rtn = multipartUpload.upload(param, files);
                    //서버에서 반환받은 결과데이터를 로그로 출력
                    Log.i(TAG, rtn.toString());
                }
                else{

                    FileUpload multipartUpload = new FileUpload(sUrl, "UTF-8");
                    rtn = multipartUpload.upload(param);
                    //서버에서 반환받은 결과데이터를 로그로 출력
                    Log.i(TAG, rtn.toString());

                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return rtn;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);


            if (jsonObject != null) {
                //결과데이터를 텍스트뷰에 출력
                try {
                    if (jsonObject.getInt("success") == 1) {
                        Toast.makeText(mContext, "작성완료",
                                Toast.LENGTH_LONG).show();
                        TeamActivity ta = (TeamActivity)TeamActivity.teamcontext;
                        ta.finish();
                        Intent intent = new Intent(getApplicationContext(), TeamActivity.class);
                        intent.putExtra("subject_idx", subject_idx);
                        finish();
                        startActivity(intent);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(mContext, "작성실패",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}