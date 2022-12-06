package com.example.ridealong;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ridealong.api.ApiPoints;
import com.example.ridealong.api.RetroClient;
import com.example.ridealong.api.models.User;
import com.example.ridealong.misc.SavedSharedPreference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    Intent i;
    TextView forgot, signup;
    Button loginBtn;
    EditText unField, pwdField;
    String un,pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(SavedSharedPreference.getProfile(LoginActivity.this)!= null)
        {
            if (SavedSharedPreference.getProfile(LoginActivity.this).getIsDriver())
                 i = new Intent(LoginActivity.this, DriverActivity.class);
            else
                 i = new Intent(LoginActivity.this, PassengerActivity.class);

            LoginActivity.this.startActivity(i);
            finish();
        }

        else{

            forgot = findViewById(R.id.forgotTv);
            signup = findViewById(R.id.signupTv);
            unField = findViewById(R.id.unTextLogin);
            pwdField = findViewById(R.id.pwdTxtLogin);
            loginBtn = findViewById(R.id.btnLogin);

            forgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + ApiPoints.BASE_IP + "/accounts/password_reset/"));
                    LoginActivity.this.startActivity(browserIntent);
                }
            });

            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                    LoginActivity.this.startActivity(i);

                }
            });

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    un = unField.getText().toString();
                    pwd = pwdField.getText().toString();

                    if (un.isEmpty() || pwd.isEmpty()){
                        Toast.makeText(LoginActivity.this, "Kindly enter your credentials", Toast.LENGTH_LONG).show();
                    }
                    else {

                        ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                        pd.setMessage("Loading");
                        pd.show();
                        String base = un + ":" + pwd;
                        String auth = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
                        Call<User> call = RetroClient.getInstance().getMyApi().login(auth);

                        call.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                Log.d("TEST", "1");
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (response.body() != null) {
                                            Log.d("TEST", response.body().toString());
                                            Toast.makeText(LoginActivity.this, "Login Succesful", Toast.LENGTH_LONG).show();
                                            SavedSharedPreference.setProfile(LoginActivity.this,response.body());
                                            SavedSharedPreference.setPassword(LoginActivity.this,pwd);
                                            Intent i;

                                            if (response.body().getIsDriver())
                                                i = new Intent(LoginActivity.this, DriverActivity.class);
                                            else
                                                i = new Intent(LoginActivity.this, PassengerActivity.class);

                                            i.putExtra("firsttime", false);

                                            pd.dismiss();
                                            LoginActivity.this.startActivity(i);
                                            LoginActivity.this.finish();
                                        }
                                    }


                                } else {
                                    Toast.makeText(LoginActivity.this, "Wrong Username or Password", Toast.LENGTH_LONG).show();
                                    pd.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Log.d("TEST", "1");
                                Toast.makeText(LoginActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                                pd.dismiss();
                            }
                        });
                    }
                }
            });

        }
    }
}