package kosmo.project3.schlineapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CourseView extends LinearLayout {

    TextView sub_idx;//과목번호
    TextView sub_name;//과목명


    public CourseView(Context context) {
        super(context);
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.course_view,this,true);

    sub_idx = findViewById(R.id.course_textView1);
    sub_name = findViewById(R.id.course_textView2);

    }
    public void setIdx (String idx){
        sub_idx.setText(idx);
    }
    public void setName (String name){
        sub_name.setText(name);
    }
}
