package com.api.mp3paradise;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import frags.CancionesFragment;
import frags.DownloadFragment;
import frags.ListasFragment;
import model.MyReproductor;

public class ReproductorActivity extends AppCompatActivity
        implements CancionesFragment.OnCancionesFragmentInteractionListener,
        ListasFragment.OnListasFragmentInteractionListener,
        DownloadFragment.OnDownloadFragmentInteractionListener{

    private FragmentTabHost tabHost;
    private final String TAG = "ReproductorActivity";

    private TextView selectedFile;

    private SeekBar seekBar;
    private ImageButton prev;
    private ImageButton play;
    private ImageButton next;
    private ImageButton repeat;
    private ImageButton rand;

    private String user;

    private CancionesFragment cancionesFragment;
    private ListasFragment listasFragment;
    private DownloadFragment downloadFragment;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reproductor);
        Bundle ext = getIntent().getExtras();
        if(ext!=null){
            user = ext.getString("user");
            //guardar usuario registrado en preferencias
            SharedPreferences prefs = getSharedPreferences("mp3paradise_preferences", Context.MODE_APPEND);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_actual",user);
            editor.apply();

            setTitle(getTitle().toString()+ " - "+ user);
        }

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

        MediaPlayer player = MyReproductor.getMr().player;

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
                if(MyReproductor.getMr().isMovingSeekBar){
                    MyReproductor.getMr().player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MyReproductor.getMr().isMovingSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MyReproductor.getMr().isMovingSeekBar = false;
            }
        });

        selectedFile = (TextView) findViewById(R.id.selecteditem);
        selectedFile.setTextColor(Color.RED);

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

        MyReproductor.getMr().seekBar = seekBar;

        if(MyReproductor.getMr().isStateSaved){
            selectedFile.setText(MyReproductor.getMr().currentFile);
            if(MyReproductor.getMr().isFileOk){
                selectedFile.setTextColor(Color.BLACK);
            } else {
                selectedFile.setTextColor(Color.RED);
            }

            if(MyReproductor.getMr().player.isPlaying()){
                play.getBackground().setTint(getResources().getColor(R.color.miniBlue,null));
                play.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                play.getBackground().setTint(getResources().getColor(R.color.superBlue,null));
                play.setImageResource(android.R.drawable.ic_media_play);
            }

            if(MyReproductor.getMr().esAleatorio){
                rand.getBackground().setTint(getResources().getColor(R.color.miniBlue,null));
            } else{
                rand.getBackground().setTint(getResources().getColor(R.color.superBlue,null));
            }

            if(MyReproductor.getMr().repeatState == MyReproductor.NO_REPETIR){
                repeat.getBackground().setTint(getResources().getColor(R.color.superBlue,null));
            } else if(MyReproductor.getMr().repeatState == MyReproductor.REPETIR_TODAS){
                repeat.getBackground().setTint(getResources().getColor(R.color.nanoBlue,null));
            } else if(MyReproductor.getMr().repeatState == MyReproductor.REPETIR_CANCION){
                repeat.getBackground().setTint(getResources().getColor(R.color.miniBlue,null));
            }

            if(MyReproductor.getMr().isStarted){
                seekBar.setMax(MyReproductor.getMr().player.getDuration());
                seekBar.setProgress(MyReproductor.getMr().player.getCurrentPosition());
            }
        } else {
            MyReproductor.getMr().isStateSaved = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_user,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //detectar que se ha pulsado cerrar sesion en el menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.it_cerrar_sesion:
                cerrarSesion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void cerrarSesion(){
        //borrar preferencia
        SharedPreferences prefs = getSharedPreferences("mp3paradise_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("user_actual");
        editor.apply();
        //mostrar actividad principal (login y registro)
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        MyReproductor.getMr().handler.removeCallbacks(MyReproductor.getMr().updatePositinRunnable);
        MyReproductor.getMr().player.stop();
        MyReproductor.getMr().player.reset();
        MyReproductor.getMr().player.release();
        MyReproductor.getMr().player = null;
        MyReproductor.destroy();
        finish();
    }

    private void onClickPrev(View v) {
        if(!MyReproductor.getMr().currentFile.equals("")) {
            int seekto = MyReproductor.getMr().player.getCurrentPosition() - MyReproductor.STEP_VALUE;
            if (seekto < 0)
                seekto = 0;
            MyReproductor.getMr().player.pause();
            MyReproductor.getMr().player.seekTo(seekto);
            MyReproductor.getMr().player.start();
        }
    }

    private void onClickPlay(View v) {
        if(!MyReproductor.getMr().currentFile.equals("")){
            if(MyReproductor.getMr().player.isPlaying()){
                MyReproductor.getMr().handler.removeCallbacks(MyReproductor.getMr().updatePositinRunnable);
                MyReproductor.getMr().player.pause();
                play.getBackground().setTint(getResources().getColor(R.color.superBlue,null));
                play.setImageResource(android.R.drawable.ic_media_play);
            }else{
                if(MyReproductor.getMr().isStarted){
                    MyReproductor.getMr().player.start();
                    play.getBackground().setTint(getResources().getColor(R.color.miniBlue,null));
                    play.setImageResource(android.R.drawable.ic_media_pause);
                    MyReproductor.getMr().updatePosition();
                }else{
                    startPlay();
                }
            }
        }
    }

    private void onClickNext(View v) {
        if(!MyReproductor.getMr().currentFile.equals("")) {
            int seekto = MyReproductor.getMr().player.getCurrentPosition() + MyReproductor.STEP_VALUE;
            if (seekto > MyReproductor.getMr().player.getDuration())
                seekto = MyReproductor.getMr().player.getDuration();
            MyReproductor.getMr().player.pause();
            MyReproductor.getMr().player.seekTo(seekto);
            MyReproductor.getMr().player.start();
        }
    }

    private void onClickRepeat(View v) {
        if(MyReproductor.getMr().repeatState == MyReproductor.NO_REPETIR){
            MyReproductor.getMr().repeatState = MyReproductor.REPETIR_TODAS;
            repeat.getBackground().setTint(getResources().getColor(R.color.nanoBlue,null));
            Toast.makeText(this,getResources().getString(R.string.rep_todas),Toast.LENGTH_SHORT).show();

        } else if(MyReproductor.getMr().repeatState == MyReproductor.REPETIR_TODAS){
            MyReproductor.getMr().repeatState = MyReproductor.REPETIR_CANCION;
            repeat.getBackground().setTint(getResources().getColor(R.color.miniBlue,null));
            Toast.makeText(this,getResources().getString(R.string.rep_cancion),Toast.LENGTH_SHORT).show();

        } else if(MyReproductor.getMr().repeatState == MyReproductor.REPETIR_CANCION){
            MyReproductor.getMr().repeatState = MyReproductor.NO_REPETIR;
            repeat.getBackground().setTint(getResources().getColor(R.color.superBlue,null));
            Toast.makeText(this,getResources().getString(R.string.rep_no),Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickRand(View v) {
        MyReproductor.getMr().esAleatorio = !MyReproductor.getMr().esAleatorio;
        if(MyReproductor.getMr().esAleatorio){
            rand.getBackground().setTint(getResources().getColor(R.color.miniBlue,null));
            Toast.makeText(this,getResources().getString(R.string.random_yes),Toast.LENGTH_SHORT).show();
        } else{
            rand.getBackground().setTint(getResources().getColor(R.color.superBlue,null));
            Toast.makeText(this,getResources().getString(R.string.random_no),Toast.LENGTH_SHORT).show();
        }
    }

    private void gestorAlCompletar(MediaPlayer mp){
        if(MyReproductor.getMr().esAleatorio){
            if(MyReproductor.getMr().repeatState==MyReproductor.REPETIR_CANCION){
                stopPlay();
                startPlay();
            } else {
                stopPlay();
                MyReproductor.getMr().currentFile = cancionesFragment.getCancionAleatoria();
                startPlay();
            }
        } else {
            if(MyReproductor.getMr().repeatState==MyReproductor.NO_REPETIR){
                stopPlay();
            } else if(MyReproductor.getMr().repeatState==MyReproductor.REPETIR_TODAS){
                posCancion += 1;
                String canPath = cancionesFragment.getCancionPos(posCancion);
                stopPlay();
                if(canPath!=null){
                    MyReproductor.getMr().currentFile = canPath;
                    startPlay();
                } else {
                    posCancion = 0;
                    canPath = cancionesFragment.getCancionPos(posCancion);
                    if(canPath!=null){
                        MyReproductor.getMr().currentFile = canPath;
                        startPlay();
                    }
                }
            } else if(MyReproductor.getMr().repeatState==MyReproductor.REPETIR_CANCION){
                stopPlay();
                startPlay();
            }
        }
    }

    private void startPlay() {
        String file = MyReproductor.getMr().currentFile;
        MyReproductor.getMr().isFileOk = true;
        selectedFile.setTextColor(Color.BLACK);
        selectedFile.setText(file);
        seekBar.setProgress(0);
        MyReproductor.getMr().player.stop();
        MyReproductor.getMr().player.reset();

        try{
            MyReproductor.getMr().player.setDataSource(file);
            MyReproductor.getMr().player.prepare();
            MyReproductor.getMr().player.start();
            seekBar.setMax(MyReproductor.getMr().player.getDuration());
            play.getBackground().setTint(getResources().getColor(R.color.miniBlue,null));
            play.setImageResource(android.R.drawable.ic_media_pause);
            MyReproductor.getMr().updatePosition();
            MyReproductor.getMr().isStarted = true;
        }catch (IOException e){
            MyReproductor.getMr().isFileOk = false;
            selectedFile.setTextColor(Color.RED);
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void stopPlay(){
        MyReproductor.getMr().player.stop();
        MyReproductor.getMr().player.reset();
        play.getBackground().setTint(getResources().getColor(R.color.superBlue,null));
        play.setImageResource(android.R.drawable.ic_media_play);
        MyReproductor.getMr().handler.removeCallbacks(MyReproductor.getMr().updatePositinRunnable);
        seekBar.setProgress(0);
        MyReproductor.getMr().isStarted = false;
    }

    @Override
    public void onListasFragmentInteraction(ListasFragment frag, int mreqid, String[] args) {
        Log.d(TAG,"onListasFragmentInteraction");
        if(mreqid == CancionesFragment.REQUEST_INIT){
            listasFragment = frag;
            listasFragment.actualizarFragment(user);
        }
    }

    @Override
    public void onDownloadFragmentInteraction(DownloadFragment frag, int mreqid, String[] args) {
        Log.d(TAG,"onDownloadFragmentInteraction");
        if(mreqid == CancionesFragment.REQUEST_INIT){
            downloadFragment = frag;
            downloadFragment.actualizarFragment(user);
        }
    }

    private int posCancion = 0;

    @Override
    public void onCancionesFragmentInteraction(CancionesFragment frag, int mreqid, String[] args) {
        Log.d(TAG,"onCancionesFragmentInteraction");
        if(mreqid == CancionesFragment.REQUEST_INIT){
            cancionesFragment = frag;
            cancionesFragment.actualizarFragment(user);
        }
        else if(mreqid == CancionesFragment.REQUEST_SONG_PATH){
            MyReproductor.getMr().currentFile = args[0];
            posCancion = Integer.parseInt(args[1]);
            startPlay();
        }
    }
}
