package com.api.mp3paradise;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btLogin = (Button)findViewById(R.id.bt_login);
        Button btRegister = (Button)findViewById(R.id.bt_register_login);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(v);
            }
        });
    }

    private void login(View v){
        EditText etUser = (EditText) findViewById(R.id.et_user_login);
        EditText etPass = (EditText) findViewById(R.id.et_pass_login);
        String user = etUser.getText().toString();
        String pass = etPass.getText().toString();
        //TODO conectar server
        if(user.equals("jorge")&&pass.equals("1234")){
            Intent i = new Intent(this,ReproductorActivity.class);
            i.putExtra("user",user);
            startActivity(i);
        }
    }

    private void register(View v){
        Intent i = new Intent(this,RegisterActivity.class);
        startActivity(i);
    }
}
