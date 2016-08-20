package com.smsapp.smsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by pawim on 06.04.2016.
 */
public class MainActivity extends AppCompatActivity {
    Button buttonLogout;
    String LogoutComplete;
    String userLogin;
    String userPassword;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonLogout = (Button) findViewById(R.id.mainctivity_button_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }
    void logout(){
        userLogin = getUserLogin();
        userPassword = getUserPassword();
        if(!userPassword.isEmpty() && !userLogin.isEmpty()) {
            performUserLogout(userLogin, userPassword);
        }
        else{
            Toast.makeText(getApplicationContext(),"Wystąpił problem",Toast.LENGTH_LONG).show();
        }
    }
    private void performUserLogout(String login, String password ){


        class LogoutUser extends AsyncTask<String,Void,String> {
            ProgressDialog loading;
            RegistrationService mRegistrationService = new RegistrationService();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this,"Trwa Wylogowanie",null,true,true);
                buttonLogout.setEnabled(false);
            }
            @Override
            protected String doInBackground(String... params) {
                String result;
                if (!userLogin.isEmpty() && !userPassword.isEmpty()) {
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("user_login", params[0]);
                    data.put("user_password", params[1]);
                    try{
                        LogoutComplete = mRegistrationService.sendPostRequest(Variables.pathToServerLogout,data);
                        setUserparamsNull(); // to powinno byc po potwierdzeniu
                    }catch (IOException e){
                        Log.e(TAG, "Failed to register" + e);
                    }
                    if(LogoutComplete.equals(Variables.FAILURE))
                        result = "Nie udało się zalogować";
                    else
                        result = LogoutComplete;
                }else{
                    result = "Wypełnij wszystkie pola";
                }
                return result;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                buttonLogout.setEnabled(true);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                if(LogoutComplete.equalsIgnoreCase(Variables.LogoutPrompt)){
                    Context context = getApplicationContext();
                    Intent i = new Intent(context, LoginActivity.class);
                    finish();
                    startActivity(i);
                }
            }
        }
        LogoutUser lu = new LogoutUser();
        lu.execute(login, password);
    }
    private String getUserLogin(){
        Context mContext = getApplicationContext();
        SharedPreferences sharedPref = mContext.getSharedPreferences("User_Preferences", Context.MODE_PRIVATE);
        String login = sharedPref.getString(Variables.PrefsUserLogin, null);
        return login;

    }
    private String getUserPassword(){
        Context mContext = getApplicationContext();
        SharedPreferences sharedPref = mContext.getSharedPreferences("User_Preferences", Context.MODE_PRIVATE);
        String password = sharedPref.getString(Variables.PrefsUserPassword,null);
        return password;
    }
    private void setUserparamsNull(){
        Context mContext = getApplicationContext();
        SharedPreferences sharedPref = mContext.getSharedPreferences("User_Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Variables.PrefsUserLogin, null);
        editor.putString(Variables.PrefsUserPassword,null);
        editor.commit();
    }
}
