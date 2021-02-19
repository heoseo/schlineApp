package kosmo.project3.schlineapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Account_select extends LinearLayout {

    TextView subjectName;
    TextView professorName;

    public Account_select(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.account_view, this, true);

        subjectName = findViewById(R.id.AccountView1);
        professorName = findViewById(R.id.AccountView2);
    }

    public void setName(String name) { subjectName.setText(name); }

    public void setPerson(String pCount) {
        professorName.setText(pCount);
    }
}