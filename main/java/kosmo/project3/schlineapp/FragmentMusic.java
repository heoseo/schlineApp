package kosmo.project3.schlineapp;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import kosmo.project3.schlineapp.R;


public class FragmentMusic extends Fragment {
    private static String TAG = "FragmentMusic";
    //public final static String url = "https://sites.google.com/site/ubiaccessmobile/sample_audio.amr";
    public final static String url = "https://t1.daumcdn.net/cfile/tistory/213E9D465854DA2301?original";//애뮬에서 소리조정

    private MediaPlayer player;
    private int position = 0;
    View musicView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.i(TAG, "뮤직 들어옴");
        musicView = inflater.inflate(R.layout.fragment_music, container, false);

        //오디오 자동재생
        playAudio();

        ImageButton btnPlay = (ImageButton) musicView.findViewById(R.id.btn_play);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });

        ImageButton btnPause = (ImageButton) musicView.findViewById(R.id.btn_pause);
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseAudio();
            }
        });

        ImageButton btnReplay = (ImageButton) musicView.findViewById(R.id.btn_replay);
        btnReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeAudio();
            }
        });

        ImageButton btnStop = (ImageButton) musicView.findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAudio();
            }
        });
        return musicView;
    }


    public void playAudio() {
        try {
            closePlayer();

            player = new MediaPlayer();
            player.setDataSource(url); // 음악 파일의 위치를 지정
            //player.setDataSource(R.raw.music); // 이눔 왜 안돼
            player.prepare();
            player.start();

            Toast.makeText(getContext(), "음악시작", Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseAudio() {
        if (player != null) {
            position = player.getCurrentPosition();
            player.pause();

            Toast.makeText(getContext(), "일시정지", Toast.LENGTH_LONG).show();
        }
    }

    public void resumeAudio() {
        if (player != null && !player.isPlaying()) { // position 값도 확인 해야함
            player.seekTo(position);
            player.start();

            Toast.makeText(getContext(), "재생", Toast.LENGTH_LONG).show();
        }
    }

    public void stopAudio() {
        if (player != null && player.isPlaying()) { // position 값도 확인 해야함
            player.stop();

            Toast.makeText(getContext(), "중지", Toast.LENGTH_LONG).show();
        }
    }

    public void closePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}