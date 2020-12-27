package com.app.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.app.R;
import com.app.view.FullScreenVideoView;
import com.punuo.sys.app.activity.BaseActivity;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by acer on 2016/10/12.
 */

public class SmallVideoPlay extends BaseActivity {
    @Bind(R.id.play)
    FullScreenVideoView play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smallvideoplay);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String videopath = intent.getStringExtra("videopath");
        final File video = new File(videopath);
        if (video.exists()) {
            play.setVideoPath(video.getAbsolutePath());
            play.start();
            play.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    play.setVideoPath(video.getAbsolutePath());
                    play.start();
                }
            });
        }
        play.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }
        });
    }
}
