package kosmo.project3.schlineapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import kosmo.project3.schlineapp.R;

public class lectureLayout extends LinearLayout {

    TextView video_title;//강의명
    TextView video_end;//종료일
    TextView video_num; // 강의 번호


    public lectureLayout(Context context) {
        super(context);
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.lecture_layout,this,true);

        video_title = findViewById(R.id.lecture_tv_video_title);
        video_num = findViewById(R.id.lecture_tv_video_num);
        video_end = findViewById(R.id.lecture_tv_video_end);

    }
    public void setVideo_title (String title){
        video_title.setText(title);
    }
    public void setVideo_end (String end){
        video_end.setText(end);
    }
    public void setVideo_num (String num){
        video_num.setText(num);
    }
}
