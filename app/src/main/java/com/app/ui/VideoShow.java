package com.app.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.R;
import com.app.view.FullScreenVideoView;
import com.punuo.sys.app.activity.BaseActivity;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class VideoShow extends BaseActivity {
    @Bind(R.id.play)
    FullScreenVideoView play;
    @Bind(R.id.returnback)
    Button returnback;
    @Bind(R.id.send)
    Button send;
    String smallVideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_show);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        smallVideoPath = intent.getStringExtra("smallVideoPath");
        play.setVideoPath(smallVideoPath);
        play.start();
        play.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                play.setVideoPath(smallVideoPath);
                play.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.play, R.id.returnback, R.id.send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                break;
            case R.id.returnback:
                File file = new File(smallVideoPath);
                if (file.exists()) {
                    file.delete();
                }
                String thumbnailsmallmovie = smallVideoPath.replace(getString(R.string.Video),
                        getString(R.string.Thumbnail));
                thumbnailsmallmovie = thumbnailsmallmovie.replace(".mp4", ".jpg");
                File thumbnailfile = new File(thumbnailsmallmovie);
                if (thumbnailfile.exists()) {
                    thumbnailfile.delete();
                }
                finish();
                break;
            case R.id.send:
                Intent intent = new Intent();
                intent.putExtra("smallVideoPath", smallVideoPath);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
