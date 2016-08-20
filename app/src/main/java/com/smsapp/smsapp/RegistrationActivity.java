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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by pawim on 01.04.2016.
 */
public class RegistrationActivity extends AppCompatActivity {
    EditText NewLogin;
    EditText NewPassword;
    EditText RepeatNewPassword;
    EditText Email_text;
    EditText Number_text;
    String login_str;
    String password_str;
    String repassword_str;
    String number_str;
    String email_str;

    String registrationComplete;
    private Button buttonRegister;
    private static final String TAG = "RegistrationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        NewLogin = (EditText) findViewById(R.id.activity_registration_edittext_login);
        NewPassword = (EditText) findViewById(R.id.activity_registration_edittext_password);
        RepeatNewPassword = (EditText) findViewById(R.id.activity_registration_edittext_repassword);
        Email_text = (EditText) findViewById(R.id.activity_registration_edittext_email);
        Number_text = (EditText) findViewById(R.id.activity_registration_edittext_phonenumber);
        buttonRegister = (Button) findViewById(R.id.activity_registration_button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        login_str = NewLogin.getText().toString();
        password_str = NewPassword.getText().toString();
        repassword_str = RepeatNewPassword.getText().toString();
        number_str = Number_text.getText().toString();
        email_str = Email_text.getText().toString();
            Log.d("Registration ", "Ready? ");
            register(login_str, password_str, email_str, number_str);


    }
    private void register(String login, String password , String email , String number){


        class RegisterUser extends AsyncTask<String,Void,String>{
            ProgressDialog loading;
            RegistrationService mRegistrationService = new RegistrationService();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegistrationActivity.this,"Trwa rejestracja",null,true,true);
                buttonRegister.setEnabled(false);
            }
            @Override
            protected String doInBackground(String... params) {
                String result;
                if (!password_str.isEmpty() && !login_str.isEmpty() && !email_str.isEmpty() && !repassword_str.isEmpty() && !number_str.isEmpty()) {
                    if(password_str.equals(repassword_str)){
                        if(number_str.length() == 9){
                            HashMap<String, String> data = new HashMap<String, String>();
                            data.put("user_login", params[0]);
                            data.put("user_password", params[1]);
                            data.put("user_email", params[2]);
                            data.put("user_phone_number", params[3]);
                            setUserparams(params[0],params[1]); // to powinno się odbyć po potwierdzeniu zalogowania
                            try{
                                registrationComplete = mRegistrationService.sendPostRequest(Variables.pathToServerRegister,data);
                            }catch (IOException e){
                                Log.e(TAG,"Failed to register" + e);
                            }
                            if(registrationComplete.equals(Variables.FAILURE))
                                result = "Nie udało się zarejestrować";
                            else
                                result = registrationComplete;
                        }else{
                            result = "Zły numer telefonu";
                        }
                    }else{
                        result = "Hasła nie są takie same";
                    }
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
                if(registrationComplete.equalsIgnoreCase(Variables.RegisterPrompt)){
                    Context context = getApplicationContext();
                    Intent i = new Intent(context, LoginActivity.class);
                    finish();
                    startActivity(i);
                }
            }
        }
        RegisterUser ru = new RegisterUser();
        ru.execute(login, password, email, number);
    }
     private void setUserparams(String login , String password){
        Context mContext = getApplicationContext();
        SharedPreferences sharedPref = mContext.getSharedPreferences("User_Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Variables.PrefsUserLogin, login);
        editor.putString(Variables.PrefsUserPassword, password);
        editor.commit();
    }
}