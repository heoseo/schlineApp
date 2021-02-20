package kosmo.project3.schlineapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;


public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";

    private BottomNavigationView mBottomNV;
    private Button btnLogout;

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "id 정보 : "+StaticUserInformation.userID);

        activity = MainActivity.this;

        btnLogout = (Button)findViewById(R.id.logoutBtn);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("account",MODE_PRIVATE);
                StaticUserInformation.resetDate(preferences);

                // 로그인 페이지로 이동하면 메인액티비티(현재) 종료
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("back_login", "true");
                startActivity(intent);
            }
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



        //안드로이드 아이디
        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("FirebaseSettingEx", "getInstanceId failed", task.getException());
                return;
            }

            // 토큰을 읽고, 콘솔에 찍기
            String token = task.getResult().getToken();
            Log.d("토큰: ", token);

        });
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

}