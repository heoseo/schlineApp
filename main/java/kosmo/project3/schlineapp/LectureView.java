package kosmo.project3.schlineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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

public class LectureView extends AppCompatActivity {

    String TAG = "LectureView";
    String subject_idx;
    ArrayList<String> video_idx = new ArrayList<String>();
    ArrayList<String> video_end = new ArrayList<String>();
    ArrayList<String> video_title = new ArrayList<String>();
    ArrayList<String> server_saved = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_view);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        subject_idx = bundle.getString("idx");
        new asynclecServer().execute(
                "http://"+ StaticInfo.my_ip +"/schline/android/time.do",
                "subject_idx="+subject_idx
        );
    }
    class asynclecServer extends AsyncTask<String,Void,String>{
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
                        conn=(HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                out.write(strings[1].getBytes());
                out.flush();
                out.close();
                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(
                            new
                                    InputStreamReader(conn.getInputStream(),"UTF-8")
                    );
                    String responseData;
                    while ((responseData=reader.readLine())!=null){
                        receiveData.append(responseData+"\n\r");
                    }
                    reader.close();
                }else{
                    Log.i(TAG,"HTTP_OK 안됨, 연결실패");
                }
                Log.i(TAG,receiveData.toString());
                JSONArray jsonArray = new JSONArray(receiveData.toString());
                receiveData.setLength(0);
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    video_idx.add(jsonObject.getString("video_idx"));
                    video_title.add(jsonObject.getString("video_title"));
                    video_end.add(jsonObject.getString("video_end"));
                    server_saved.add(jsonObject.getString("server_saved"));

                }



            }catch (Exception e){
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
            ListView listView = (ListView) findViewById(R.id.LectureListview);

            final lectureAdapter adapter = new lectureAdapter();
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new
                AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Intent intent = new Intent(view.getContext(),LectureView.class);
                        /*intent.putExtra("idx",subject_idx.get(i));
                        startActivity(intent);*/

                    }
                });
        }
    }
    class lectureAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return video_idx.size();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public Object getItem(int i) {
            return video_idx.get(i);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            lectureLayout lectureLayout = new lectureLayout(getApplicationContext());
            lectureLayout.setVideo_end(video_end.get(i));
            lectureLayout.setVideo_title(i+1+"."+video_title.get(i));

            return lectureLayout;


        }
    }

}