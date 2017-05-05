package com.api.mp3paradise;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import DB.DoHTTPRequest;

public class RegisterActivity extends AppCompatActivity implements DoHTTPRequest.AsyncResponse{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btReg = (Button) findViewById(R.id.bt_reg);

        btReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(v);
            }
        });
    }

    private void register(View v){
        EditText etUser = (EditText) findViewById(R.id.et_usu);
        EditText etPass = (EditText) findViewById(R.id.et_pass);
        EditText etRepPass = (EditText) findViewById(R.id.et_rep_pass);

        String usu = etUser.getText().toString();
        String pass = etPass.getText().toString();
        String repPass = etRepPass.getText().toString();

        if(!usu.equals("")){
            if(pass.equals(repPass)){
                DoHTTPRequest doHTTP = new DoHTTPRequest(this,this,-1);
                doHTTP.prepComandRegister(usu,pass);
                doHTTP.execute();
            } else{
                Toast.makeText(this,getResources().getString(R.string.error_rep_pass),Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(this,getResources().getString(R.string.error_user_vacio),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void processFinish(String output, int mReqId) {
        try {
            if(mReqId == DoHTTPRequest.REGISTER){
                JSONObject json = new JSONObject(output);
                String status = json.getString("status");
                if(status.equals("ok")){
                    String user = json.getString("user");
                    Intent i = new Intent();
                    i.putExtra("user",user);
                    setResult(RESULT_OK,i);
                    finish();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
