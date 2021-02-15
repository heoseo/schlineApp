package kosmo.project3.schlineapp.team;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

import kosmo.project3.schlineapp.R;
import kosmo.project3.schlineapp.team.TeamVO;

public class TeamBoardLayout extends LinearLayout {

    String TAG = "SEONGJUN";
    TextView teamtitle;
    TextView teamuser;
    TextView teampostdate;

    public TeamBoardLayout(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.teamboard_layout, this, true);

        teamtitle = findViewById(R.id.teamtitle);
        teamuser = findViewById(R.id.teamuser);
        teampostdate = findViewById(R.id.teampostdate);
    }

    public void settitle(String title){
        teamtitle.setText(title);
    }
    public void setuser(String user){
        teamuser.setText(user);
    }
    public void setpostdate(String postdate){
        teampostdate.setText(postdate);
    }

}
