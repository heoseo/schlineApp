package kosmo.project3.schlineapp.task;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;

import kosmo.project3.schlineapp.R;

public class TaskView extends AppCompatActivity {

    private static final String TAG = "SEONGJUN";

    EditText title = findViewById(R.id.taskviewtitle);
    EditText content = findViewById(R.id.taskviewcontent);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        //메니페스트에 설정된 권한에 대해 사용자에게 물어본다.
        //만약 사용자가 허용하지 않으면 해당기능은 사용할 수 없게된다.
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        Intent intent = new Intent(Intent.ACTION_PICK);


    }
}