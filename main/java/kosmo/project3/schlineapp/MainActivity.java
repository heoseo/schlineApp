package kosmo.project3.schlineapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    String token;
    private BottomNavigationView mBottomNV;

    public static Activity activity;
    RetrofitAPI retrofitAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "id 정보 : "+StaticUserInformation.userID);

        activity = MainActivity.this;




        //안드로이드 아이디
        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("FirebaseSettingEx", "getInstanceId failed", task.getException());
                return;
            }

            // 토큰을 읽고, 콘솔에 찍기
            token = task.getResult().getToken();
            Log.d("토큰: ", token);
            String user_id = StaticUserInformation.userID;
            new syncCourseServer().execute(
                    "http://"+ StaticInfo.my_ip +"/schline/android/token.do",
                    "token="+token,
                    "user_id="+user_id
            );


        });


        mBottomNV = findViewById(R.id.nav_view);
        mBottomNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() { //NavigationItemSelecte
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                BottomNavigate(menuItem.getItemId());


                return true;
            }
        });
        mBottomNV.setSelectedItemId(R.id.navigation_1);




    }

    private void BottomNavigate(int id) {  //BottomNavigation 페이지 변경
        String tag = String.valueOf(id);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment currentFragment = fragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            if (id == R.id.navigation_1) {
                fragment = new FragmentClassRoom();
            } else if (id == R.id.navigation_2){
                fragment = new FragmentSchedule();
            }else if (id == R.id.navigation_3){
                fragment = new FragmentStudyRoom();
            }else{
                fragment = new FragmentMyAccount();
            }

            fragmentTransaction.add(R.id.content_layout, fragment, tag);
        } else {
            fragmentTransaction.show(fragment);
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNow();



    }

    class syncCourseServer extends AsyncTask<String,Void,String> {
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
                out.write("&".getBytes());
                out.write(strings[2].getBytes());
                out.flush();
                out.close();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new
                                    InputStreamReader(conn.getInputStream(), "UTF-8")
                    );
                    String responseData;
                    while ((responseData = reader.readLine()) != null) {
                        receiveData.append(responseData + "\n\r");
                    }
                    reader.close();
                } else {
                    Log.i(TAG, "HTTP_OK 안됨, 연결실패");
                }
                Log.i(TAG, receiveData.toString());


            } catch (Exception e) {
                e.printStackTrace();
            }


            return receiveData.toString();
        }

    }



}