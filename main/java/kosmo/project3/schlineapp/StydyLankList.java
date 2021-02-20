package kosmo.project3.schlineapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import kosmo.project3.schlineapp.R;

//LinearLayout을 상속한 커스텀뷰
public class StydyLankList extends LinearLayout {

    TextView lank_num;//랭킹순위
    TextView lank_nick;//랭킹닉네임
    TextView lank_time;//누적시간

    public StydyLankList(Context context) {
        super(context);

        //레이아웃 전개를 위해 Inflater객체 생성
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
//        LayoutInflater inflater = (LayoutInflater)context.getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        //텍스트뷰 inflate()를 통해 레이아웃 전개
        inflater.inflate(R.layout.study_lank_view,this,true);
        //데이터를 출력할 위젯
        lank_num = findViewById(R.id.text_lank_num);
        lank_nick = findViewById(R.id.text_lank_nick);
        lank_time = findViewById(R.id.text_lank_time);
    }

    //매개변수타입 TextView를 String으로 바꿔줘야함
    //따로 생성한 setter
    public void setNum(String a) {
        lank_num.setText(a);
    }
    public void setNick(String a) {
        lank_nick.setText(a);
    }
    public void setTime(String a) {
        lank_time.setText(a);
    }
}