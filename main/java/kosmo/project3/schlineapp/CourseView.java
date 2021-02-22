package kosmo.project3.schlineapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kosmo.project3.schlineapp.R;

public class CourseView extends LinearLayout {

    TextView sub_idx;//과목번호
    TextView sub_name;//과목명
    ImageView imageView;


    public CourseView(Context context) {
        super(context);
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.course_view,this,true);

    sub_idx = findViewById(R.id.course_textView1);
    sub_name = findViewById(R.id.course_textView2);
    imageView = findViewById(R.id.minilogo);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            GradientDrawable drawable=
                    (GradientDrawable) context.getDrawable(R.drawable.subject_img_round_corner);
            imageView.setBackground(drawable);
            imageView.setClipToOutline(true);

        }


    }
    public void setIdx (String idx){
        sub_idx.setText(idx);
    }
    public void setName (String name){
        sub_name.setText(name);
    }
}
