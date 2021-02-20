package kosmo.project3.schlineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class UserinfoActivity extends AppCompatActivity {

    String TAG = "Userinfo";
    String orga_name;
    String user_name;
    String phone_num;
    String email;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);


        user_id = StaticUserInformation.userID;
        Log.i(TAG, user_id);

        new asyncuserinfo().execute(
                "http://"+ StaticInfo.my_ip +"/schline/android/userinfo.do",
                "user_id="+user_id

        );
    }
    class asyncuserinfo extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            StringBuffer receiveData = new StringBuffer();
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection
                        conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                out.write(strings[1].getBytes());
                out.flush();
                out.close();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8")
                    );
                    String data;
                    while ((data = reader.readLine()) != null) {
                        receiveData.append(data + "\n\r");
                    }
                    reader.close();
                } else {
                    Log.i(TAG, "HTTP_OK 안됨, 연결실패");
                }


                Log.i(TAG, receiveData.toString());
                JSONArray jsonArray = new JSONArray(receiveData.toString());
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                receiveData.setLength(0);
                orga_name = jsonObject.getString("orga_name");
                user_name = jsonObject.getString("user_name");
                phone_num = jsonObject.getString("phone_num");
                email = jsonObject.getString("email");


            } catch (Exception e) {
                e.printStackTrace();
            }


            return receiveData.toString();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            
            TextView textView = (TextView) findViewById(R.id.ID);
            TextView textView2 = (TextView) findViewById(R.id.orga);
            TextView textView3 = (TextView) findViewById(R.id.name);
            TextView textView4 = (TextView) findViewById(R.id.phone);
            TextView textView5 = (TextView) findViewById(R.id.email);

            textView.setText(user_id);
            textView2.setText(orga_name);
            textView3.setText(user_name);
            textView4.setText(phone_num);
            textView5.setText(email);
            
        }
    }
}
