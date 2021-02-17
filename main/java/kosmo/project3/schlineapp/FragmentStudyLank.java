package kosmo.project3.schlineapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;

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

public class FragmentStudyLank extends Fragment {

    private static final String tag = "FragmentStudyRank";

    int[] lank_count = {1,2,3,4,5,6,7,8,9,10};
    ArrayList<String> lank_nick = new ArrayList<String>();
    ArrayList<String> lank_time = new ArrayList<String>();
    ViewGroup studyRankView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //리스트뷰
        studyRankView = (ViewGroup)
                inflater.inflate(R.layout.study_lank_list, container, false);
                //inflater.inflate(R.layout.study_lank_list, container, false);

        new syncLankServer().execute(
                "http://"+ StaticInfo.my_ip +"/schline/android/class/studyLank.do"
        );
        //여기서 쓰면 null뜬다!
        /*
        //리스트뷰 위젯 객체화
        ListView lank_listView = (ListView)studyRankView.findViewById(R.id.listview_lank);
        //사용자정의 어댑터 연결
        final LankAdapter adapter= new LankAdapter();
        lank_listView.setAdapter(adapter);
        */
        return studyRankView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //이클립스 연결
    class syncLankServer extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            StringBuffer receiveDate = new StringBuffer();
            try{
                Log.i(tag, "서버연결 랭크리스트 가져오기");
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                out.flush();
                out.close();

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                    Log.i(tag, "랭크리스트 연결성공");
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8")
                    );
                    String responseData;
                    while ((responseData=reader.readLine())!=null){
                        receiveDate.append(responseData+"\n\r");
                    }
                    reader.close();
                }
                else{
                    Log.i(tag, "HTTP_OK 안됨. 연결실패");
                }

                Log.i(tag, receiveDate.toString());
                //제이슨배열 가져오기
                JSONArray jsonArray = new JSONArray(receiveDate.toString());
                receiveDate.setLength(0);
                for(int i=0 ; i<jsonArray.length() ; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    lank_nick.add(jsonObject.getString("info_nick"));
                    lank_time.add(jsonObject.getString("info_time"));

                    Log.i(tag, "lank_nick테스트 : " + lank_nick);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return receiveDate.toString(); //onPostExecute전달
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
        //백그라운드 다음 호출
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //여기서 어댑터 연결해서 null에러 방지
            //스피너와 연결
            Spinner listView = (Spinner)studyRankView.findViewById(R.id.listview_lank);
            //사용자정의 어댑터 연결
            LankAdapter adapter= new LankAdapter();
            listView.setAdapter(adapter);
        }
    }


    class LankAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            Log.i(tag,"랭크리스트1");
            return lank_nick.size();
        }
        //인덱스
        @Override
        public long getItemId(int i) {
            Log.i(tag,"랭크리스트2"+i);
            return i;
        }
        @Override
        public Object getItem(int i) {
            Log.i(tag,"랭크리스트3"+lank_time.get(i));
            return lank_nick.get(i);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Log.i(tag, lank_nick.get(i)+lank_time.get(i));//여기까지 들어옴

            StydyLankList lankList = new StydyLankList(getContext());
            //기본자료(정수) -> 클래스 : Boxing
            lankList.setNum(new Integer(lank_count[i]).toString());
            //lankList.setLank_num(i);
            lankList.setNick(lank_nick.get(i));
            lankList.setTime(lank_time.get(i));
            return lankList;
        }
    }
}
