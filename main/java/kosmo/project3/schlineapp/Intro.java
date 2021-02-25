package kosmo.project3.schlineapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class Intro extends AppCompatActivity {
    String TAG = "IntroLog";

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent;

            SharedPreferences preferences = getSharedPreferences("account",MODE_PRIVATE);
            Log.d(TAG, "account pref > " + preferences.getString("userID", "defValue"));
            Log.d(TAG, "account static> " + StaticUserInformation.userID);

            if(preferences.getString("userID", "defValue").equals("defValue") )
                intent = new Intent(getApplicationContext(), LoginActivity.class);
            else if(StaticUserInformation.userID == null)
                intent = new Intent(getApplicationContext(), LoginActivity.class);
            else
                intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

    }

    @Override
    protected void onResume() {
        // Intro화면에 진입한 후 2초 후에 runnable객체를 실행한다.
        super.onResume();
        handler.postDelayed(runnable, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}