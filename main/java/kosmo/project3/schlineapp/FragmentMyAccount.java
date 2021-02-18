package kosmo.project3.schlineapp;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

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

public class FragmentMyAccount extends Fragment {
    String TAG = "FragmentMyAccount";

    ArrayList<String> subjectName = new ArrayList<String>();
    ArrayList<String> professorName = new ArrayList<String>();
    ArrayList<String> subject_idx = new ArrayList<String>();
    ViewGroup viewGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_my_account, container, false);
        String user_id = StaticUserInformation.userID;
        Log.i(TAG, user_id);
        new Asyncaccount().execute(
                "http://" + StaticInfo.my_ip + "/schline/android/usersublist.do",
                "user_id=" + user_id
        );


        Button button = (Button) viewGroup.findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                popupMenu.getMenuInflater().inflate(R.menu.account_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    //toast  말고 링크걸기
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_menu1) {
                            Toast.makeText(getActivity(), "메뉴 1 클릭", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), totalgrade.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "메뉴 2 클릭", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), usersetting.class);
                            startActivity(intent);
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        return viewGroup;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    class Asyncaccount extends AsyncTask<String, Void, String> {
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
                            new
                                    InputStreamReader(conn.getInputStream(), "UTF-8")
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
                receiveData.setLength(0);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    professorName.add(jsonObject.getString("professor"));
                    subjectName.add(jsonObject.getString("subject_name"));
                    subject_idx.add(jsonObject.getString("subject_idx"));

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
            ListView listView = (ListView) viewGroup.findViewById(R.id.acconutlist);
            MyAdapter myAdapter = new MyAdapter();
            listView.setAdapter(myAdapter);
            listView.setOnItemClickListener(new
            AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent(view.getContext(), AccountView.class);
                    intent.putExtra("idx", subject_idx.get(i));
                    startActivity(intent);

                    //intent에 데이터 넣는 부분

                    // 데이터를 intent 에 실어 보내기
                    //intent.putExtra("year", 2015);
                    //intent.putExtra("month", 8);
                    //intent.putExtra("day", 15);
                    //intent.putExtra("weekday", "토요일");

                    // 객체를 intent 에 실어 보내기
                    //Person p = new Person();
                    //p.name = "고길동";
                    //p.age = 43;
                    //p.sex = true;
                    //intent.putExtra("person", p); // 객체를 보내야 할 경우
                    // class 선언부에서 implements Serializable 하면 된다
                }

            });
        }
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return subject_idx.size();
        }

        @Override
        public Object getItem(int i) {
            return subject_idx.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            Account_select accountview = new Account_select(getContext());
            accountview.setName(subjectName.get(i));
            accountview.setPerson(professorName.get(i));

            return accountview;
        }
    }

}


