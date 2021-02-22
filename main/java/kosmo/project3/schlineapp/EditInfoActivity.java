package kosmo.project3.schlineapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class EditInfoActivity extends AppCompatActivity implements Runnable{

    private String TAG = "EditInfoActivity";

    ImageView img_change;
    EditText change_nick;
    TextView tvHtml1;
    String filePath1;//파일의 절대경로
    String info_img, info_nick;
    URL url;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        //editText 키보드가 가릴때 맞춰주기
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //메니페스트에 설정된 권한에 대해 사용자에게 물어본다.
        //만약 사용자가 허용하지 않는다면 해당기능을 사용할 수 없다.
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        //위젯
        img_change = findViewById(R.id.img_change);
        tvHtml1 = findViewById(R.id.tvHtml1);
        change_nick = findViewById(R.id.change_nick);

        //인텐트 가져오기
        Intent intent = getIntent();
        //번들로 부가데이터 전달
        Bundle bundle = intent.getExtras();
        info_img = bundle.getString("info_img");
        info_nick = bundle.getString("info_nick");
        url = (URL)bundle.get("url");
        //bitmap = (Bitmap)intent.getParcelableExtra("bitmap");

        //Log.i(TAG, "넘어온 비트맵"+bt);
        Log.i(TAG, "넘어온이미지" + info_img);
        Log.i(TAG, "넘어온 닉네임"+ info_nick);
        Log.i(TAG, "넘어온 url"+ url);

        //위젯 셋팅
        change_nick.setText(info_nick);
        Log.i(TAG,"닉네임 셋팅값 확인"+change_nick.getText().toString());

        //프로필이미지 모서리 둥글게
        img_change.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            img_change.setClipToOutline(true);
        }

        // 백그라운드 스레드 생성
        Thread th =new Thread(this);
        th.start();

    }////onCreate


    // 키보드 내리기 버튼(사용안함)
    public void onKeydownClicked(View v) {
        // IME Hide
        InputMethodManager mgr =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    //////////////////////이미지 변경하기
    //이미지선택
    public void onBtnGetPicture(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    //파일업로드(1)
    public void onBtnUpload(View v) {

        //텍스트뷰 내용 비우기
        tvHtml1.setText("");

        //파라미터를 맵에 저장
        HashMap<String, String> param1 = new HashMap<>();
        param1.put("change_nick", change_nick.getText().toString());//editText 값가져오기
        param1.put("info_img", info_img);
        param1.put("user_id", StaticUserInformation.userID);

        //param1.put("change_img", img_change.toString());

        HashMap<String, String> param2 = new HashMap<>();
        UploadAsync networkTask;//프로필수정 진행 핸들러

        //절대경로가 null이 아닐때 파일업로드 진행
        if(filePath1!=null) {//파일이 있을때
            param2.put("filename", filePath1);
            Log.i(TAG, "신규 업로드 파일 절대경로=" + filePath1);
            networkTask = new UploadAsync(getApplicationContext(), param1, param2);
        }
        else{//파일이 없을때
            networkTask = new UploadAsync(getApplicationContext(), param1);
        }
        // AsyncTask를 통해 HttpURLConnection 수행.
        //doInBackground() 호출
        networkTask.execute();
    }
    //끝내기
    public void onBtnFinish(View v) {
        Intent intent = new Intent(v.getContext(),MainActivity.class);
        startActivity(intent);
        //finish();
    }

    // 갤러리 리스트뷰에서 사진 데이터를 가져오는 방법
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri selPhotoUri = data.getData();
                showCapturedImage(selPhotoUri);
            }
        }
    }
    // 사용자정의함수 - 사진의 절대경로 구하기
    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        Log.i(TAG, "사용자정의함수 절대경로="+cursor.getString(column_index));
        return cursor.getString(column_index);
    }

    // 사용자정의함수 - 사진의 회전값을 처리하지 않으면 사진을 찍은 방향대로 이미지뷰에 처리되지 않는다.
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
    // 사용자정의함수 - 사진을 정방향대로 회전하기
    private Bitmap rotate(Bitmap src, float degree) {
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);

        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0,
                src.getWidth(), src.getHeight(), matrix, true);
    }

    private void showCapturedImage(Uri imageUri) {
        // 사진의 절대경로 획득
        filePath1 = getRealPathFromURI(imageUri);//사용자정의함수
        Log.d(TAG, "path1:"+filePath1);

        ExifInterface exifInterface = null;

        try{
            exifInterface = new ExifInterface(filePath1);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        int exifOrientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);//사용자정의함수

        //경로를 통해 비트맵으로 전환
        Bitmap bitmap = BitmapFactory.decodeFile(filePath1);
        Bitmap rotatedBitmap = rotate(bitmap, exifDegree);//사용자정의함수
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, 800, 800, false);
        bitmap.recycle();

        //이미지뷰에 비트맵 삽입
        img_change.setImageBitmap(scaledBitmap);
    }

    //파일업로드(2)
    // 네트웍 처리결과를 화면에 반영하기 위한 핸들러<param, progress, result>
    public class UploadAsync extends AsyncTask<Object, Integer, JSONObject> {

        //객체생성시 전달한 파라미터로 멤버변수 초기화
        private Context mContext;
        private HashMap<String, String> param;//파라미터
        private HashMap<String, String> files;//사진파일

        //생성자1(사진파일 없음)
        public UploadAsync(Context context, HashMap<String, String> param){
            mContext = context;
            this.param = param;//파라미터
        }
        //생성자2(사진파일 있음)
        public UploadAsync(Context context, HashMap<String, String> param, HashMap<String, String> files){
            mContext = context;
            this.param = param;//파라미터
            this.files = files;//사진파일
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            JSONObject rtn = null;
            String sUrl = "http://" + StaticInfo.my_ip + "/schline/android/class/editProfile.do";
            try {
                //단말기의 사진을 서버로 업로드하기위한 객체생성 및 메소드호출
                //FileUpload 클래스는 기존내용을 그대로 가져다 쓰면 됨(수정필요없음)
                FileUpload multipartUpload = new FileUpload(sUrl, "UTF-8");
                if(filePath1!=null) {
                    rtn = multipartUpload.upload(param, files);
                    Log.d(TAG, "반환 json결과값="+rtn.toString());
                }
                else{//파일 없을때(기존방식)
                    rtn = multipartUpload.upload(param);
                    Log.d(TAG, "반환 json결과값="+rtn.toString());
                    //JSON객체 파싱
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return rtn;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            //여기까지 오기전에 이미 널에러 발생
            if (jsonObject != null) {
                //결과데이터를 텍스트뷰에 출력
                tvHtml1.setText("");
                try {
                    if (jsonObject.getInt("result") == 1) {//map에 저장된 값 받아오기
                        Toast.makeText(mContext, "프로필 수정 성공:)",
                                Toast.LENGTH_SHORT).show();
                    }
                    else if(jsonObject.getInt("result") == 0){
                        Toast.makeText(mContext, "중복닉네임이 있습니다.",
                                Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(mContext, "프로필 수정 실패",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    //////////기존 프로필 이미지 가져오기
    // 메인 스레드와 백그라운드 스레드 간의 통신
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, "핸들러 들어옴");
            // 서버에서 받아온 이미지를 핸들러를 경유해 이미지뷰에 비트맵 리소스 연결
            img_change.setImageBitmap(bitmap);
        }
    };

    // 백그라운드 스레드
    @Override
    public void run() {
        try{
            Log.i(TAG, "이미지 url최종="+url);
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
            handler.sendEmptyMessage(0);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}