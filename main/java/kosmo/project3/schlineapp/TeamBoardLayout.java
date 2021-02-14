package kosmo.project3.schlineapp;

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

import kosmo.project3.schlineapp.vo.TeamVO;

public class TeamBoardLayout extends LinearLayout {


    private List<TeamVO> list;
    private Context context;

    public TeamBoardLayout(Context context, List<TeamVO> list, Context context1) {
        super(context);
        this.list = list;
        this.context = context1;
    }

    public class TeamAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Log.i("SEONGJUN","진입이 되나요?");

            if(view==null){
                view= View.inflate(context, R.layout.teamboard_layout, null);
            }

            TextView teamtitle = findViewById(R.id.teamtitle);
            TextView teamuser = findViewById(R.id.teamuser);
            TextView teampostdate = findViewById(R.id.teampostdate);

            teamtitle.setText(list.get(i).getBoard_title());
            teamuser.setText(list.get(i).getUser_name());
            teampostdate.setText(list.get(i).getBoard_postdate());

            return view;
        }
    }

}
