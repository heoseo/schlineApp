package kosmo.project3.schlineapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Subjectaccount extends LinearLayout {

    TextView sub_idx;//과목번호
    TextView sub_flag;//과목명


    public Subjectaccount(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.account_subject,this,true);

        sub_idx = findViewById(R.id.text11);

        sub_flag = findViewById(R.id.text12);



    }
    public void setIdx (String idx){ sub_idx.setText(idx); }
    public void setName (String name){
        sub_flag.setText(name);
    }


}
