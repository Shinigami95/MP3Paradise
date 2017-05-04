package com.api.mp3paradise;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        if(requestPerms()){
            return;
        }
        EditText etUser = (EditText) findViewById(R.id.et_user_login);
        EditText etPass = (EditText) findViewById(R.id.et_pass_login);
        String user = etUser.getText().toString();
        String pass = etPass.getText().toString();
        //TODO conectar server
        if(user.equals("jorge")&&pass.equals("1234")){
            Intent i = new Intent(this,ReproductorActivity.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
        }
    }

    private void register(View v){
        if(requestPerms()){
            return;
        }
        Intent i = new Intent(this,RegisterActivity.class);
        startActivity(i);
    }

    private boolean requestPerms(){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            return true;
        }
        return false;
    }
}
