package com.api.mp3paradise;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import DB.DoHTTPRequest;

public class MainActivity extends AppCompatActivity implements DoHTTPRequest.AsyncResponse{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getSharedPreferences("mp3paradise_preferences", Context.MODE_PRIVATE);
        if(prefs.contains("user_actual")){
            String userName = prefs.getString("user_actual",null);
            if(userName!=null) {
                //si existe el campo user_actual en preferencias y no es null, ir a la actividad del menu principal
                Intent i = new Intent(this, ReproductorActivity.class);
                i.putExtra("user", userName);
                startActivity(i);
                finish();
            }
        }
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

        DoHTTPRequest doHTTP = new DoHTTPRequest(this,this,-1);
        doHTTP.prepComandLogin(user,pass);
        doHTTP.execute();
    }

    private void register(View v){
        if(requestPerms()){
            return;
        }
        Intent i = new Intent(this,RegisterActivity.class);
        startActivityForResult(i,777);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== 777 && resultCode == RESULT_OK){
            String user = data.getExtras().getString("user");
            Intent i = new Intent(this,ReproductorActivity.class);
            i.putExtra("user",user);
            startActivity(i);
            finish();
        }
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

    @Override
    public void processFinish(String output, int mReqId) {
        try {
            if(mReqId == DoHTTPRequest.LOG_IN){
                JSONObject json = new JSONObject(output);
                String status = json.getString("status");
                if(status.equals("ok")){
                    String user = json.getString("user");
                    Intent i = new Intent(this,ReproductorActivity.class);
                    i.putExtra("user",user);
                    startActivity(i);
                    finish();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
