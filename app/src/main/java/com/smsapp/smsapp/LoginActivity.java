package com.smsapp.smsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    EditText editTextUserName;
    EditText editTextPassword;
    Button buttonLogin;
    Button buttonRegister;
    String gcmRegID = "";
    String userLogin;
    String userPassword;
    String LoginComplete;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(checkPlayServices()){
            new GCMRegistrationTask().execute();
        }else{
            buttonLogin.setEnabled(false);
            buttonRegister.setEnabled(false);
            Toast.makeText(getApplicationContext(),"No google services :(",Toast.LENGTH_LONG).show();
        }
      /*  userLogin = getUserLogin();
        userPassword = getUserPassword();
        if(userLogin != null && userPassword != null ){
            Context context = getApplicationContext();
            Intent i = new Intent(context, MainActivity.class);
            finish();
            startActivity(i);
        }*/

        editTextUserName = (EditText) findViewById(R.id.login_activity_text_login);
        editTextPassword = (EditText) findViewById(R.id.login_activity_text_password);
        buttonLogin = (Button) findViewById(R.id.login_activity_button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        buttonRegister = (Button) findViewById(R.id.loginactivity_button_createNew);
        buttonRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
    private void loginUser(){
        userPassword = editTextPassword.getText().toString();
        userLogin = editTextUserName.getText().toString();
        Log.i(TAG,userLogin);
        Log.i(TAG,userPassword);
        login(userLogin,userPassword,gcmRegID);
    }
    private boolean checkPlayServices(){
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if(apiAvailability.isUserResolvableError(resultCode)){
                apiAvailability.getErrorDialog(this,resultCode,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }else{
                Log.i("Play services", "not supported");
                finish();
            }
            return false;
        }
        return true;
    }
    private void login(String login, String password,String gcmID ){


        class LoginUser extends AsyncTask<String,Void,String>{
            ProgressDialog loading;
            RegistrationService mRegistrationService = new RegistrationService();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this,"Trwa Logowanie",null,true,true);
                buttonRegister.setEnabled(false);
            }
            @Override
            protected String doInBackground(String... params) {
                String result;
                if (!userLogin.isEmpty() && !userPassword.isEmpty() && !gcmRegID.isEmpty()) {
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("user_login", params[0]);
                    data.put("user_password", params[1]);
                    data.put("gcm_regid",params[2]);
                    setUserparams(params[0],params[1]);
                    try{
                        LoginComplete = mRegistrationService.sendPostRequest(Variables.pathToServerLogin,data);
                    }catch (IOException e){
                        Log.e(TAG,"Failed to register" + e);
                    }
                    if(LoginComplete.equals(Variables.FAILURE))
                        result = "Nie udało się zalogować";
                    else
                        result = LoginComplete;
                }else{
                    result = "Wypełnij wszystkie pola";
                }
                return result;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                buttonRegister.setEnabled(true);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
               // if(LoginComplete.equalsIgnoreCase(Variables.LoginPrompt)){
                    Context context = getApplicationContext();
                    Intent i = new Intent(context, MainActivity.class);
                    finish();
                    startActivity(i);
               // }
            }
        }
        LoginUser lu = new LoginUser();
        lu.execute(login, password, gcmID);
    }
    private class GCMRegistrationTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... params) {
            Log.d("Registration token", "Executing ");
            try{
                InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                gcmRegID = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);
                Log.d("Registration token", "GCM registration Token " + gcmRegID);
            }catch (IOException e){
                e.printStackTrace();
            }
            return gcmRegID;
        }


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
        String password = sharedPref.getString(Variables.PrefsUserPassword, null);
        return password;
    }
    private void setUserparams(String login,String password){
        Context mContext = getApplicationContext();
        SharedPreferences sharedPref = mContext.getSharedPreferences("User_Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Variables.PrefsUserLogin,login);
        editor.putString(Variables.PrefsUserPassword,password);
        editor.commit();
    }

}