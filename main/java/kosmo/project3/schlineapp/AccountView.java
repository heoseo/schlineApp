package kosmo.project3.schlineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AccountView extends AppCompatActivity {

    String TAG = "AccountView";
    String subject_idx;
    ArrayList<String> rnum = new ArrayList<String>();
    ArrayList<String> attendance_flag = new ArrayList<String>();
    ArrayList<String> exam_name = new ArrayList<String>();
    ArrayList<String> grade_exam = new ArrayList<String>();
    String grade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        subject_idx = bundle.getString("idx");
        String user_id = StaticUserInformation.userID;
        Log.i(TAG, user_id);
        Log.i(TAG, subject_idx);
        new asyncgrade().execute(
                "http://"+ StaticInfo.my_ip +"/schline/android/grade.do",
                "user_id="+user_id+"&subject_idx="+subject_idx

        );
    }
    class asyncgrade extends AsyncTask<String,Void,String> {
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

                //map으로 가져오 arrayobject
                Log.i(TAG, receiveData.toString());
                JSONObject jSONObject = new JSONObject(receiveData.toString());
                JSONArray attenlists = new JSONArray(jSONObject.getString("attenlists"));
                JSONArray gradelists = new JSONArray(jSONObject.getString("gradelists"));
                //JSONArray gradeChar = new JSONArray(jSONObject.getString("gradeChar"));
                receiveData.setLength(0);

                for (int i = 0; i < attenlists.length(); i++) {
                    JSONObject jsonObject2 = attenlists.getJSONObject(i);
                    rnum.add(jsonObject2.getString("rnum"));
                    if(jsonObject2.getString("attendance_flag").equals("2")){
                        attendance_flag.add("출석");
                    }
                    if(jsonObject2.getString("attendance_flag").equals("1")){
                        attendance_flag.add("강의 시청중");
                    }
                    else {
                        attendance_flag.add("결석");
                    }
                    //attendance_flag.add(jsonObject2.getString("attendance_flag"));
                }
                for (int i = 0; i < gradelists.length(); i++) {
                    JSONObject jsonObject3 = gradelists.getJSONObject(i);
                    exam_name.add(jsonObject3.getString("exam_name"));
                    grade_exam.add(jsonObject3.getString("grade_exam"));
                }
                JSONObject jsonObject4 = attenlists.getJSONObject(0);
                grade = jsonObject4.getString("gradeChar");

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
            GridView gridView = (GridView) findViewById(R.id.accountlistview);
            GridView gridView2 = (GridView) findViewById(R.id.accountlistview2);

            AccountAdapter accountAdapter = new AccountAdapter();
            AccountAdapter2 accountAdapter2 = new AccountAdapter2();
            gridView.setAdapter(accountAdapter);
            gridView2.setAdapter(accountAdapter2);

        }


    }
    class AccountAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return rnum.size();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public Object getItem(int i) {
            return rnum.get(i);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Log.i(TAG,"account :"+rnum.get(i)+attendance_flag.get(i));
            Subjectaccount accountView = new Subjectaccount(getApplication());
            accountView.setIdx(rnum.get(i));
            accountView.setName(attendance_flag.get(i));

            return accountView;
        }
    }
    class AccountAdapter2 extends BaseAdapter {

        @Override
        public int getCount() {
            return exam_name.size();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Gradeaccount accountView2 = new Gradeaccount(getApplication());
            accountView2.setExam_name(exam_name.get(i));
            accountView2.setGrade_exam(grade_exam.get(i));

            return accountView2;
        }

    }

}
