package kosmo.project3.schlineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;


import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LectureView extends AppCompatActivity implements View.OnClickListener{

    String TAG = "LectureView";
    String subject_idx;
    ArrayList<String> video_idx = new ArrayList<String>();
    ArrayList<String> video_end = new ArrayList<String>();
    ArrayList<String> video_title = new ArrayList<String>();
    ArrayList<String> server_saved = new ArrayList<String>();
    ArrayList<String> play_time = new ArrayList<String>();
    ArrayList<String> currenttime = new ArrayList<String>();
    ArrayList<String> attendance_flag = new ArrayList<String>();
    String user_id = StaticUserInformation.userID;

    //플로팅버튼 테스트
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private ExtendedFloatingActionButton fab, floatteam, floattask, floatlecture;

    RetrofitAPI retrofitAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_view);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ViewGroup viewGroup;
        subject_idx = bundle.getString("idx");
        retrofitAPI = RetrofitAPI.getClient().create(RetrofitAPI.class);

        ///////////////////////////////////////////////////////////////////////
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab = (ExtendedFloatingActionButton)findViewById(R.id.fab);
        floatteam = (ExtendedFloatingActionButton)findViewById(R.id.floatteam);
        floattask = (ExtendedFloatingActionButton)findViewById(R.id.floattask);
        floatlecture = (ExtendedFloatingActionButton)findViewById(R.id.floatlecture);

        fab.setOnClickListener(this);
        floatteam.setOnClickListener(this);
        floattask.setOnClickListener(this);
        floatlecture.setOnClickListener(this);
        ///////////////////////////////////////////////////////////////


        Call<Post> call = retrofitAPI.doGetUserList(subject_idx,user_id);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                Post post = response.body();
                List<Post.Datum> datumList = post.lists;
                for(Post.Datum datum : datumList){
                    video_idx.add(datum.video_idx);
                    video_title.add(datum.video_title);
                    video_end.add(datum.video_end);
                    server_saved.add(datum.server_saved);
                    play_time.add(datum.play_time);
                    currenttime.add(datum.currenttime);
                    attendance_flag.add(datum.attendance_flag);

                }
                ListView listView = (ListView) findViewById(R.id.LectureListview);

                lectureAdapter adapter = new lectureAdapter();
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new
                AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                       Log.i("디버그:","saved"+server_saved.get(i));
                        Intent intent = new Intent(view.getContext(),VideoPlay.class);
                        intent.putExtra("saved",server_saved.get(i));
                        intent.putExtra("vid_idx",video_idx.get(i));
                        intent.putExtra("title",video_title.get(i));
                        intent.putExtra("play_time",play_time.get(i));
                        intent.putExtra("currenttime",currenttime.get(i));
                        intent.putExtra("attendance_flag",attendance_flag.get(i));

                        startActivity(intent);


                    }
                });
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                call.cancel();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    class lectureAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return video_idx.size();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public Object getItem(int i) {
            return video_idx.get(i);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            lectureLayout lectureLayout = new lectureLayout(getApplicationContext());
            lectureLayout.setVideo_num(""+(i+1)+"");
            lectureLayout.setVideo_title(video_title.get(i));
            lectureLayout.setVideo_end(video_end.get(i));

            return lectureLayout;


        }
    }

    //플로팅버튼용
    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id) {
            case R.id.fab:
                anim();
                break;
            case R.id.floattask:
                anim();
                intent = new Intent(view.getContext(), TaskActivity.class);
                intent.putExtra("subject_idx", subject_idx);
                finish();
                startActivity(intent);
                break;
            case R.id.floatteam:
                anim();
                intent = new Intent(view.getContext(), TeamActivity.class);
                intent.putExtra("subject_idx", subject_idx);
                finish();
                startActivity(intent);
                break;
            case R.id.floatlecture:
                anim();
                intent = new Intent(view.getContext(), LectureView.class);
                intent.putExtra("idx", subject_idx);
                finish();
                startActivity(intent);
                break;
        }
    }

    public void anim() {

        if (isFabOpen) {
            floatlecture.startAnimation(fab_close);
            floatteam.startAnimation(fab_close);
            floattask.startAnimation(fab_close);
            floatlecture.setClickable(false);
            floatteam.setClickable(false);
            floattask.setClickable(false);
            isFabOpen = false;
        } else {
            floatlecture.startAnimation(fab_open);
            floatteam.startAnimation(fab_open);
            floattask.startAnimation(fab_open);
            floatlecture.setClickable(true);
            floatteam.setClickable(true);
            floattask.setClickable(true);
            isFabOpen = true;
        }
    }


}