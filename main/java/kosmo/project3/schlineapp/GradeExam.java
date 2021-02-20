package kosmo.project3.schlineapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GradeExam extends LinearLayout {

    TextView sub_name;//과목명


    public GradeExam(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.account_examgrade,this,true);

        sub_name = findViewById(R.id.text11);



    }

    public void setName (String name){
        sub_name.setText(name);
    }


}
