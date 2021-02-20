package kosmo.project3.schlineapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import kosmo.project3.schlineapp.R;

public class TaskBoardLayout extends LinearLayout {

    String TAG = "SEONGJUN";
    TextView tasktitle;
    TextView taskuser;
    TextView taskdate;

    public TaskBoardLayout(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.taskboard_layout, this, true);

        tasktitle = findViewById(R.id.tasktitle);
        taskuser = findViewById(R.id.taskuser);
        taskdate = findViewById(R.id.taskdate);
    }

    public void settitle(String title){
        tasktitle.setText(title);
    }
    public void setuser(String content){ taskuser.setText(content); }
    public void setpostdate(String postdate){ taskdate.setText(postdate); }
}
