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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TotalgradeActivity extends AppCompatActivity {


    String TAG = "Totalgrade";
    ArrayList<String> sub_name = new ArrayList<String>();
    ArrayList<String> grade_lists = new ArrayList<String>();
    String gradeNum;
    String gradeChar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totalgrade);


        String user_id = StaticUserInformation.userID;
        Log.i(TAG, user_id);

        new asynctotalgrade().execute(
                "http://"+ StaticInfo.my_ip +"/schline/android/SubjectGrade.do",
                "user_id="+user_id

        );
    }
    class asynctotalgrade extends AsyncTask<String,Void,String> {
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
                JSONArray subjectlists = new JSONArray(jSONObject.getString("lists"));
                JSONArray gradelists = new JSONArray(jSONObject.getString("listgrade2"));
                gradeNum = jSONObject.getString("gradeNum");
                gradeChar = jSONObject.getString("gradeChar");
                receiveData.setLength(0);

                for (int i = 0; i < subjectlists.length(); i++) {
                    JSONObject jsonObject2 = subjectlists.getJSONObject(i);
                    sub_name.add(jsonObject2.getString("subject_name"));
                }
                for (int i = 0; i < gradelists.length(); i++) {
                    //JSONObject jsonObject3 = gradelists.getJSONObject(i);
                    grade_lists.add(gradelists.getString(i));
                }



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
            ListView listView = (ListView) findViewById(R.id.subnameview);
            ListView listView2 = (ListView) findViewById(R.id.subgradeview);

            TotalgradeActivity.AccountAdapter accountAdapter = new TotalgradeActivity.AccountAdapter();
            TotalgradeActivity.AccountAdapter2 accountAdapter2 = new TotalgradeActivity.AccountAdapter2();
            listView.setAdapter(accountAdapter);
            listView2.setAdapter(accountAdapter2);

        }


    }
    class AccountAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return grade_lists.size();
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
            GradeExam gradeView = new GradeExam(getApplication());
            gradeView.setName(sub_name.get(i));

            return gradeView;
        }
    }
    class AccountAdapter2 extends BaseAdapter {

        @Override
        public int getCount() {
            return grade_lists.size();
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
            GradeExam2 gradeView2 = new GradeExam2(getApplication());
            gradeView2.setGrade(grade_lists.get(i));

            return gradeView2;
        }

    }

}