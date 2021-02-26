package kosmo.project3.schlineapp;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import kosmo.project3.schlineapp.MainActivity;
import kosmo.project3.schlineapp.R;
import kosmo.project3.schlineapp.StaticInfo;
import retrofit2.Call;

import static com.google.android.exoplayer2.Player.*;


public class VideoPlay extends AppCompatActivity {
    private PlayerView exoPlayerView;
    String user_id = StaticUserInformation.userID;
    private SimpleExoPlayer player;
    RetrofitAPI retrofitAPI;
    ProgressBar progressBar;
    ImageView btFullScreen;
    private TextView TimeTextView, AttendTextView;
    private Thread timeThread = null;
    Timer timer = new Timer();

    private Boolean isRunning = true;


    boolean flag = false;


    private Boolean playWhenReady = true;
    private int currentWindow = 0;
    private Long playbackPosition = 0L;
    /**
     * The player does not have any media to play.
     */
    int STATE_IDLE = 1;
    /**
     * The player is not able to immediately play from its current position. This state typically
     * occurs when more data needs to be loaded.
     */
    int STATE_BUFFERING = 2;
    /**
     * The player is able to immediately play from its current position. The player will be playing if
     * {엣 링크 #getPlayWhenReady()} is true, and paused otherwise.
     */
    int STATE_READY = 3;
    /**
     * The player has finished playing the media.
     */
    int STATE_ENDED = 4;
    String saved;
    String title;
    String vid_idx;
    String currenttime;
    String play_time;
    String attendance_flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        exoPlayerView = findViewById(R.id.exoPlayerView);
        progressBar = findViewById(R.id.progress_bar);
        btFullScreen = exoPlayerView.findViewById(R.id.bt_fullscreen);
        Intent intent = getIntent();
        TimeTextView = (TextView) findViewById(R.id.timeView);
        AttendTextView = (TextView) findViewById(R.id.attendView);
        retrofitAPI = RetrofitAPI.getClient().create(RetrofitAPI.class);


        saved = intent.getStringExtra("saved");
        vid_idx = intent.getStringExtra("vid_idx");
        title = intent.getStringExtra("title");
        attendance_flag = intent.getStringExtra("attendance_flag");
        play_time = intent.getStringExtra("play_time");
        currenttime = intent.getStringExtra("currenttime");
        if (!attendance_flag.equals("0")) {
            AttendTextView.setText(attendance_flag);
        }


        //풀스크린 만들기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //컨트롤 초기화
        LoadControl loadControl = new DefaultLoadControl();
        //band width meter 초기화
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        //trac selector 초기화
        TrackSelector trackSelector = new DefaultTrackSelector(
                new AdaptiveTrackSelection.Factory(bandwidthMeter)
        );

        player = ExoPlayerFactory.newSimpleInstance(this.getApplicationContext(), trackSelector,
                loadControl);

        DefaultHttpDataSourceFactory factory = new DefaultHttpDataSourceFactory(
                "exoplayer_video"
        );
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        String sample = "http://" + StaticInfo.my_ip + "/schline/resources/video/" + saved;

        Uri videoUrl = Uri.parse(sample);
        MediaSource mediaSource = new ExtractorMediaSource(videoUrl, factory, extractorsFactory, null, null);


        //플레이어 연결
        exoPlayerView.setPlayer(player);

        exoPlayerView.setKeepScreenOn(true);
        // 미디어 준비
        player.prepare(mediaSource);

        //준비되면 플레이
        player.setPlayWhenReady(true);


        player.addListener(new Player.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {


                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);


                } else if (playbackState == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);


                }
                if (playWhenReady == true) {
                    isRunning = true;
                } else {
                    isRunning = false;

                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
        btFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    btFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    flag = false;
                } else {
                    btFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_exit));
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    flag = true;
                }
            }
        });


    }

    @Override
    protected void onPause() {

        super.onPause();
        player.setPlayWhenReady(false);
        player.getPlaybackState();


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        player.setPlayWhenReady(true);
        player.getPlaybackState();

    }

    @Override
    protected void onStart() {
        super.onStart();
        timeThread = new Thread(new timeThread());
        timeThread.start();
        timer.schedule(TT, 0, 10000); //Timer 실행
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
        timer.cancel();
    }


    private MediaSource buildMediaSource(Uri uri) {

        String userAgent = Util.getUserAgent(this, "blackJin");

        if (uri.getLastPathSegment().contains("mp3") || uri.getLastPathSegment().contains("mp4")) {

            return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);

        } else if (uri.getLastPathSegment().contains("m3u8")) {

            //com.google.android.exoplayer:exoplayer-hls 확장 라이브러리를 빌드 해야 합니다.
            return new HlsMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);

        } else {

            return new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(this, userAgent))
                    .createMediaSource(uri);
        }

    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();

            exoPlayerView.setPlayer(null);
            player.release();
            player = null;

        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int sec = (msg.arg1) % 60;
            int min = (msg.arg1) / 60;
            int hour = (msg.arg1) / 360;
            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간

            @SuppressLint("DefaultLocale") String result = String.format("%2d:%02d:%02d", hour, min, sec);

            TimeTextView.setText(result);
        }
    };

    public class timeThread implements Runnable {
        @Override
        public void run() {
            int i = Integer.parseInt(play_time);

            while (true) {
                while (isRunning) { //일시정지를 누르면 멈춤
                    Message msg = new Message();
                    msg.arg1 = i++;
                    play_time = msg.toString();
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(1100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TimeTextView.setText("");
                                TimeTextView.setText("0:00:00");
                            }
                        });
                        return; // 인터럽트 받을 경우 return
                    }
                }
            }
        }
    }
    TimerTask TT = new TimerTask() {
        @Override
        public void run() {
            int timeMs = (int) player.getDuration();
            int totalSeconds = timeMs / 1000;
            Log.d("디비","듀레이션:"+totalSeconds+" 현재:"+player.getCurrentPosition());
            /*if ((totalSeconds* 0.4) <= player.getCurrentPosition()) {
                attendance_flag = "2";
                AttendTextView.setText(attendance_flag);

            } currentposition duration 둘다 0뜸 이거 고쳐야해*/

            Call<Void> call = retrofitAPI.dbupdate(vid_idx, user_id, play_time, player.getCurrentPosition(),attendance_flag);


        }

    };
}

