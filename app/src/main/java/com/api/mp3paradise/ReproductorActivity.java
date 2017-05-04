package com.api.mp3paradise;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TabHost;

public class ReproductorActivity extends AppCompatActivity {

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);
        TabHost th = (TabHost)findViewById(R.id.th);
        th.setup();
        String user = getIntent().getExtras().getString("user");
        setTitle(getTitle().toString()+user);
        ListView lv = (ListView)findViewById(R.id.lv_canciones);
        ImageButton btPreb = (ImageButton)findViewById(R.id.previous);
        ImageButton btPlay = (ImageButton)findViewById(R.id.play);
        ImageButton btNext = (ImageButton)findViewById(R.id.next);
        SeekBar sb = (SeekBar)findViewById(R.id.seekBar);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNext(v);
            }
        });
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPlay(v);
            }
        });
        btPreb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPreb(v);
            }
        });
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }
        mp = new MediaPlayer();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onCancionTerminada(mp);
            }
        });
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressBarChanged(seekBar, progress, fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void onClickNext(View v){

    }

    private void onClickPlay(View v){

    }

    private void onClickPreb(View v){

    }

    private void onCancionTerminada(MediaPlayer mp){

    }

    private void progressBarChanged(SeekBar seekBar, int progress, boolean fromUser){

    }
}
