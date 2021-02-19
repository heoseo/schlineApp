package kosmo.project3.schlineapp;



import android.content.Context;

import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Gradeaccount extends LinearLayout {

    TextView exam_name;//과목번호
    TextView setGrade_exam;//과목명

    public Gradeaccount(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.account_grade,this,true);

        exam_name = findViewById(R.id.text11);

        setGrade_exam = findViewById(R.id.text12);



    }

    public void setExam_name (String En){ exam_name.setText(En); }
    public void setGrade_exam (String Gn){
        setGrade_exam.setText(Gn);
    }

}