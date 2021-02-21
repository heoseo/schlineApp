package kosmo.project3.schlineapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CalendarActivity extends AppCompatActivity {

    String TAG = "JUHEE>>> Calendar>>  ";

    //달력
    public WebView mWebViewC;
    private WebSettings mWebSettingsC;

    String user_id = StaticUserInformation.userID.toString();

    ImageButton btnSchedule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        ImageButton btnSchedule = (ImageButton) findViewById(R.id.image_schedule_sub);

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



















    }
}