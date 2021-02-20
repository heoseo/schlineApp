package kosmo.project3.schlineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LectureView extends AppCompatActivity {

    String TAG = "LectureView";
    String subject_idx;
    ArrayList<String> video_idx = new ArrayList<String>();
    ArrayList<String> video_end = new ArrayList<String>();
    ArrayList<String> video_title = new ArrayList<String>();
    ArrayList<String> server_saved = new ArrayList<String>();



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
        /*
        버튼테스트용 추후삭제해야함...
         */
        Button lectureBtn = (Button)findViewById(R.id.lectureBtn);
        lectureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), LectureView.class);
                intent.putExtra("idx", subject_idx);
                startActivity(intent);
            }
        });
        Button teamBtn = (Button)findViewById(R.id.teamBtn);
        teamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TeamActivity.class);
                intent.putExtra("subject_idx", subject_idx);
                startActivity(intent);
            }
        });
        Button taskBtn = (Button)findViewById(R.id.taskBtn);
        taskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TaskActivity.class);
                intent.putExtra("subject_idx", subject_idx);
                startActivity(intent);
            }
        });
        /*
        여기까지
         */
        ///////////////////////////////////////////////////////////////


        Call<Post> call = retrofitAPI.doGetUserList(subject_idx);
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
            lectureLayout.setVideo_end(video_end.get(i));
            lectureLayout.setVideo_title(i+1+"."+video_title.get(i));

            return lectureLayout;


        }
    }

}