package com.api.mp3paradise;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class ReproductorActivity extends AppCompatActivity
        implements CancionesFragment.OnCancionesFragmentInteractionListener,
        ListasFragment.OnListasFragmentInteractionListener,
        DownloadFragment.OnDownloadFragmentInteractionListener{

    private static final int UPDATE_FREQUENCY = 500;
    private static final int STEP_VALUE = 4000;

    private boolean isMovingSeekBar = false;

    private boolean isStarted = true;

    private FragmentTabHost tabHost;
    private final String TAG = "ReproductorActivity";
    private MediaPlayer player = null;
    private int repeatState;
    private boolean esAleatorio;

    private final Runnable updatePositinRunnable = new Runnable() {
        @Override
        public void run() {
            updatePosition();
        }
    };

    private TextView selectedFile;
    private String currentFile = "";

    private SeekBar seekBar;
    private ImageButton prev;
    private ImageButton play;
    private ImageButton next;
    private ImageButton repeat;
    private ImageButton rand;
    private final Handler handler = new Handler();

    private static final int REPETIR_TODAS = 100;
    private static final int REPETIR_CANCION = 101;
    private static final int NO_REPETIR = 102;

    private String user;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updatePositinRunnable);
        player.stop();
        player.reset();
        player.release();
        player = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);
        tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this,
                getSupportFragmentManager(),android.R.id.tabcontent);
        tabHost.addTab(
                tabHost.newTabSpec("tab1")
                        .setIndicator(getResources().getString(R.string.tab_canciones)),
                CancionesFragment.class, null);
        tabHost.addTab(
                tabHost.newTabSpec("tab2")
                        .setIndicator(getResources().getString(R.string.tab_listas)),
                ListasFragment.class, null);
        tabHost.addTab(
                tabHost.newTabSpec("tab3")
                        .setIndicator(getResources().getString(R.string.tab_downloads)),
                DownloadFragment.class, null);
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            return;
        }

        Bundle ext = getIntent().getExtras();
        if(ext!=null){
            user = ext.getString("user");
            setTitle(getTitle().toString()+ " - "+ user);
        }

        if(player==null){
            player = new MediaPlayer();
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                gestorAlCompletar(mp);
            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener(){
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        seekBar =(SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(isMovingSeekBar){
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isMovingSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isMovingSeekBar = false;
            }
        });

        repeatState = NO_REPETIR;
        esAleatorio = false;
        selectedFile = (TextView) findViewById(R.id.selecteditem);

        prev = (ImageButton)findViewById(R.id.previous);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPrev(v);
            }
        });

        play = (ImageButton)findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPlay(v);
            }
        });

        next = (ImageButton)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNext(v);
            }
        });

        repeat = (ImageButton)findViewById(R.id.bt_repeat);
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRepeat(v);
            }
        });

        rand = (ImageButton)findViewById(R.id.bt_random);
        rand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRand(v);
            }
        });
    }

    private void onClickPrev(View v) {
        if(!currentFile.equals("")) {
            int seekto = player.getCurrentPosition() - STEP_VALUE;
            if (seekto < 0)
                seekto = 0;
            player.pause();
            player.seekTo(seekto);
            player.start();
        }
    }

    private void onClickPlay(View v) {
        if(!currentFile.equals("")){
            if(player.isPlaying()){
                handler.removeCallbacks(updatePositinRunnable);
                player.pause();
                play.getBackground().setTint(getResources().getColor(R.color.superBlue,null));
                play.setImageResource(android.R.drawable.ic_media_play);
            }else{
                if(isStarted){
                    player.start();
                    play.getBackground().setTint(getResources().getColor(R.color.miniBlue,null));
                    play.setImageResource(android.R.drawable.ic_media_pause);
                    updatePosition();
                }else{
                    startPlay();
                }
            }
        }
    }

    private void onClickNext(View v) {
        if(!currentFile.equals("")) {
            int seekto = player.getCurrentPosition() + STEP_VALUE;
            if (seekto > player.getDuration())
                seekto = player.getDuration();
            player.pause();
            player.seekTo(seekto);
            player.start();
        }
    }

    private void onClickRepeat(View v) {
        if(this.repeatState == this.NO_REPETIR){
            this.repeatState = this.REPETIR_TODAS;
            repeat.getBackground().setTint(getResources().getColor(R.color.nanoBlue,null));
            Toast.makeText(this,getResources().getString(R.string.rep_todas),Toast.LENGTH_SHORT).show();

        } else if(this.repeatState == this.REPETIR_TODAS){
            this.repeatState = this.REPETIR_CANCION;
            repeat.getBackground().setTint(getResources().getColor(R.color.miniBlue,null));
            Toast.makeText(this,getResources().getString(R.string.rep_cancion),Toast.LENGTH_SHORT).show();

        } else if(this.repeatState == this.REPETIR_CANCION){
            this.repeatState = this.NO_REPETIR;
            repeat.getBackground().setTint(getResources().getColor(R.color.superBlue,null));
            Toast.makeText(this,getResources().getString(R.string.rep_no),Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickRand(View v) {
        this.esAleatorio = !this.esAleatorio;
        if(this.esAleatorio){
            rand.getBackground().setTint(getResources().getColor(R.color.miniBlue,null));
            Toast.makeText(this,getResources().getString(R.string.random_yes),Toast.LENGTH_SHORT).show();
        } else{
            rand.getBackground().setTint(getResources().getColor(R.color.superBlue,null));
            Toast.makeText(this,getResources().getString(R.string.random_no),Toast.LENGTH_SHORT).show();
        }
    }

    private void gestorAlCompletar(MediaPlayer mp){
        if(esAleatorio){
            if(repeatState==REPETIR_CANCION){
                stopPlay();
                startPlay();
            } else {
                //TODO get aleatoria
                stopPlay();
                currentFile = cancionesFragment.getCancionAleatoria();
                startPlay();
            }
        } else {
            if(repeatState==NO_REPETIR){
                stopPlay();
            } else if(repeatState==REPETIR_TODAS){
                //TODO repetir todas
            } else if(repeatState==REPETIR_CANCION){
                stopPlay();
                startPlay();
            }
        }
    }

    private void startPlay() {
        String file = currentFile;
        selectedFile.setText(file);
        seekBar.setProgress(0);
        player.stop();
        player.reset();

        try{
            player.setDataSource(file);
            player.prepare();
            player.start();
        }catch (Exception e){
            e.printStackTrace();
        }
        seekBar.setMax(player.getDuration());
        play.getBackground().setTint(getResources().getColor(R.color.miniBlue,null));
        play.setImageResource(android.R.drawable.ic_media_pause);
        updatePosition();
        isStarted = true;
    }

    private void stopPlay(){
        player.stop();
        player.reset();
        play.getBackground().setTint(getResources().getColor(R.color.superBlue,null));
        play.setImageResource(android.R.drawable.ic_media_play);
        handler.removeCallbacks(updatePositinRunnable);
        seekBar.setProgress(0);
        isStarted = false;
    }

    private void updatePosition() {
        handler.removeCallbacks(updatePositinRunnable);
        seekBar.setProgress(player.getCurrentPosition());
        handler.postDelayed(updatePositinRunnable,UPDATE_FREQUENCY);
    }

    @Override
    public void onListasFragmentInteraction() {
        Log.d(TAG,"onListasFragmentInteraction");
    }

    @Override
    public void onDownloadFragmentInteraction() {
        Log.d(TAG,"onDownloadFragmentInteraction");
    }


    CancionesFragment cancionesFragment;
    @Override
    public void onCancionesFragmentInteraction(CancionesFragment frag, int mreqid, String[] args) {
        Log.d(TAG,"onCancionesFragmentInteraction");
        if(mreqid == CancionesFragment.REQUEST_INIT){
            cancionesFragment = frag;
        }
        else if(mreqid == CancionesFragment.REQUEST_SONG_PATH){
            currentFile = args[0];
            startPlay();
        }
    }
}
