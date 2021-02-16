package kosmo.project3.schlineapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import kosmo.project3.schlineapp.R;

public class lectureLayout extends LinearLayout {

    TextView video_end;//강의명
    TextView video_title;//종료일


    public lectureLayout(Context context) {
        super(context);
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.lecture_layout,this,true);

        video_title = findViewById(R.id.lecture_textView1);
        video_end = findViewById(R.id.lecture_textView2);

    }
    public void setVideo_title (String title){
        video_title.setText(title);
    }
    public void setVideo_end (String end){
        video_end.setText(end);
    }
}
