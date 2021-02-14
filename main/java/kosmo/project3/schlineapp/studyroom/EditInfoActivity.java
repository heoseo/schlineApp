package kosmo.project3.schlineapp.studyroom;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import kosmo.project3.schlineapp.R;
import kosmo.project3.schlineapp.StaticInfo;

public class EditInfoActivity extends AppCompatActivity {

    private String TAG = "EditInfoActivity";

    ImageView imageView;
    ImageView imageView2;
    ImageView img_change;
    EditText change_nick;
    TextView tvHtml1;
    String pro_img;
    String filePath1;//파일의 절대경로
    String info_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        img_change = findViewById(R.id.img_change);
        tvHtml1 = findViewById(R.id.tvHtml1);
        change_nick = findViewById(R.id.change_nick);


        //메니페스트에 설정된 권한에 대해 사용자에게 물어본다.
        //만약 사용자가 허용하지 않는다면 해당기능을 사용할 수 없다.
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        //인텐트 가져오기
        Intent intent = getIntent();
        //번들로 부가데이터 전달
        Bundle bundle = intent.getExtras();
        pro_img = bundle.getString("img");
        info_img = bundle.getString("info_img");

        Log.i(TAG, "넘어온이미지" + pro_img);

        imageView = findViewById(R.id.img_info);
        imageView2 = findViewById(R.id.img_change);

        //프로필이미지 모서리 둥글게
        img_change.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            img_change.setClipToOutline(true);
        }
    }////onCreate


    //이미지선택
    public void onBtnGetPicture(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }
    //파일업로드
    public void onBtnUpload(View v) {

        //텍스트뷰 내용 비우기
        tvHtml1.setText("");

        //파라미터를 맵에 저장
        HashMap<String, String> param1 = new HashMap<>();
        param1.put("userid", "홍길동");
        param1.put("userpwd", "패스워드");

        HashMap<String, String> param2 = new HashMap<>();
        param2.put("filename", filePath1);

        // AsyncTask를 통해 HttpURLConnection 수행.
        UploadAsync networkTask = new UploadAsync(getApplicationContext(), param1, param2);
        //doInBackground() 호출
        networkTask.execute();
    }
/*    //끝내기
    public void onBtnFinish(View v) {
        finish();
    }*/

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
        // 사진의 절대경로를 획득한다!!! 중요~
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

    // 네트웍 처리결과를 화면에 반영하기 위한 안드로이드 핸들러
    public class UploadAsync extends AsyncTask<Object, Integer, JSONObject> {

        //객체생성시 전달한 파라미터로 멤버변수 초기화
        private Context mContext;
        private HashMap<String, String> param;//파라미터
        private HashMap<String, String> files;//사진파일
        public UploadAsync(Context context, HashMap<String, String> param, HashMap<String, String> files){
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
                String sUrl = "http://"+StaticInfo.my_ip+
                        "/schline/android/class/editProfile.do";
                //단말기의 사진을 서버로 업로드하기위한 객체생성 및 메소드호출
                //FileUpload 클래스는 기존내용을 그대로 가져다 쓰면 됨(수정필요없음)
                FileUpload multipartUpload = new FileUpload(sUrl, "UTF-8");
                rtn = multipartUpload.upload(param, files);
                //서버에서 반환받은 결과데이터를 로그로 출력
                Log.d(TAG, "반환 json결과값="+rtn.toString());
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

            if (jsonObject != null) {
                //결과데이터를 텍스트뷰에 출력
                tvHtml1.setText(jsonObject.toString());
                try {
                    if (jsonObject.getInt("success") == 1) {
                        Toast.makeText(mContext, "파일 업로드 성공!",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(mContext, "파일 업로드 실패!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}