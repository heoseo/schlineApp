package kosmo.project3.schlineapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GradeExam2 extends LinearLayout {

    TextView sub_grade;//점수


    public GradeExam2(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.account_examgrade2,this,true);

        sub_grade = findViewById(R.id.text11);



    }

    public void setGrade (String name){
        sub_grade.setText(name);
    }


}
