package kosmo.project3.schlineapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FragmentClassRoom extends Fragment
{
    String TAG = "FragmentClassRoom";

    ArrayList<String> subject_name = new  ArrayList<String>();
   ArrayList<String> subject_idx = new ArrayList<String>();
    ViewGroup viewGroup;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

       viewGroup = (ViewGroup)
                inflater.inflate(R.layout.fragment_classroom, container, false);
            String user_id = StaticUserInformation.userID;
        new syncCourseServer().execute(
                "http://"+ StaticInfo.my_ip +"/schline/android/CourseList.do",
                "userID="+user_id
        );


        return viewGroup;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    class syncCourseServer extends AsyncTask<String,Void,String>{
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
                      subject_idx.add(jsonObject.getString("subject_idx"));
                    subject_name.add(jsonObject.getString("subject_name"));


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
            ListView listView = (ListView)viewGroup.findViewById(R.id.courseListView);
            CourseAdapter courseAdapter = new CourseAdapter();
            listView.setAdapter(courseAdapter);
            listView.setOnItemClickListener(new
            AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent(view.getContext(), LectureView.class);
                    intent.putExtra("idx",subject_idx.get(i));
                    startActivity(intent);

                }
            });
        }
    }
    class CourseAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return subject_idx.size();
        }

        @Override
        public long getItemId(int i) {
            return i;
         }

        @Override
        public Object getItem(int i) {
            return subject_idx.get(i);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Log.i(TAG,"코스뷰 :"+subject_idx.get(i)+subject_name.get(i));
            CourseView courseView = new CourseView(getContext());
            courseView.setIdx(subject_idx.get(i));
            courseView.setName(subject_name.get(i));

            return courseView;
        }
    }

}
