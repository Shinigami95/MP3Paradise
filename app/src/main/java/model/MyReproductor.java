package model;

import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.SeekBar;

/**
 * Created by Jorge on 05/05/2017.
 */

public class MyReproductor {

    private static MyReproductor mr;
    public MediaPlayer player = null;

    public final Runnable updatePositinRunnable = new Runnable() {
        @Override
        public void run() {
            updatePosition();
        }
    };

    public void updatePosition() {
        MyReproductor.getMr().handler.removeCallbacks(updatePositinRunnable);
        seekBar.setProgress(MyReproductor.getMr().player.getCurrentPosition());
        MyReproductor.getMr().handler.postDelayed(updatePositinRunnable,MyReproductor.UPDATE_FREQUENCY);
    }

    public SeekBar seekBar;

    public static final int UPDATE_FREQUENCY = 500;
    public static final int STEP_VALUE = 4000;

    public boolean isMovingSeekBar = false;

    public boolean isStarted = true;

    public int repeatState;
    public boolean esAleatorio;

    public String currentFile = "";

    public boolean isFileOk = true;

    public final Handler handler = new Handler();

    public static final int REPETIR_TODAS = 100;
    public static final int REPETIR_CANCION = 101;
    public static final int NO_REPETIR = 102;

    public boolean isStateSaved;

    private MyReproductor(){
        player = new MediaPlayer();
        isStateSaved = false;
        repeatState = NO_REPETIR;
        esAleatorio = false;
    }

    public static MyReproductor getMr(){
        if(mr == null){
            mr = new MyReproductor();
        }
        return mr;
    }

    public static void destroy(){
        mr = null;
    }
}
