package kosmo.project3.schlineapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    String TAG = "LoginActivityLog";

    EditText etId, etPass;
    Button btnLogin;
    CheckBox checkBox;

    MainActivity MA = (MainActivity) MainActivity.activity;

    // 생체인식
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Intent loginIntent = getIntent();
        String back_login = loginIntent.getStringExtra("back_login");

        Log.d(TAG, "back_login : " + back_login);

        if(back_login != null)
            if(back_login.equals("true")){
                MA.finish();
        }

        etId = findViewById(R.id.et_login_id);
        etPass = findViewById(R.id.et_login_pass);


        checkBox = findViewById(R.id.checkbox_auto_login);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new LoginTask().execute(
                        "http://"+ StaticInfo.my_ip +"/schline/android/memberLogin.do",
                        "user_id="+etId.getText().toString(),
                        "user_pass="+etPass.getText().toString()
                );
            }
        });

        // 생체정보로 로그인하기 true => 로그인 띄우기.
        if(StaticUserInformation.biometric != null){
            if(StaticUserInformation.biometric.equals("true"))
                startBiometric();
        }

    }

    private void startBiometric(){

        Log.d(TAG, "startBiometric()");
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        promptInfo = new androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                .setTitle("지문 인증")
                .setSubtitle("기기에 등록된 지문을 이용하여 지문을 인증해주세요.")
                .setNegativeButtonText("취소")
                .setDeviceCredentialAllowed(false)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }


    class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... strings) {


            StringBuffer receiveData = new StringBuffer();

            try{
                Log.i(TAG, "doInbackground");
                URL url = new URL(strings[0]);      //파라미터1 : 요청URL
                Log.i(TAG, "url > " + url);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                OutputStream out = conn.getOutputStream();
                out.write(strings[1].getBytes());   //파라미터2 : 아이디
                out.write("&".getBytes());       //&를 사용하여 쿼리스트링 형태로 만들어준다.
                out.write(strings[2].getBytes());   //파라미터3 : 패스워드
                out.flush();
                out.close();

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){

                    Log.i(TAG, "HTTP_OK 됨. 연결 성공");
                    Log.i(TAG, "conn.getInputStream() > " + conn.getInputStream());


                    //스프링 서버에 연결성공한 경우 JSON데이터를 읽어서 저장한다.
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8")
                    );
                    String data;
                    while((data=reader.readLine())!=null){
                        receiveData.append(data+"\r\n");
                    }
                    reader.close();

                }
                else{
                    Log.i(TAG, "HTTP_OK 안됨. 연결실패");
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            //로그출력
            //저장된 내용을 로그로 출력한 후 onPostExecute()로 반환한다.
            Log.i(TAG, receiveData.toString());

            //서버에서 내려준 JSON정보를 저장 후 반환
            return receiveData.toString();
        }

        //doInBackgoround()에서 반환한 값은 해당 메소드로 전달한다.
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{
                //JSON겍채를 파싱
                JSONObject jsonObject = new JSONObject(s);
                int success = Integer.parseInt(jsonObject.getString("isLogin"));


                Log.i(TAG, "success > " + success);

                // 로그인에 성공하면...
                if(success == 1){
                    Toast.makeText(getApplicationContext(), "로그인하였습니다.", Toast.LENGTH_LONG).show();
                    StaticUserInformation.biometric = "true";


                    //자동로그인 체크돼있으면
                    if(checkBox.isChecked()){

                        //로그인 정보를 저장하고..
                        SharedPreferences preferences = getSharedPreferences("account",MODE_PRIVATE);
                        SharedPreferences.Editor editor=preferences.edit();
                        StaticUserInformation.userID = etId.getText().toString();
                        editor.putString("userID", etId.getText().toString());
                        editor.apply();

                    }
                    else if(checkBox.isChecked() == false){
                        SharedPreferences preferences = getSharedPreferences("account",MODE_PRIVATE);
                        StaticUserInformation.resetDate(preferences);

                        StaticUserInformation.userID = etId.getText().toString();
                    }


                    //메인액티비티를 실행시킨다.
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    finish();
                }
                else if(success == 0)
                    Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_LONG).show();



            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }

}