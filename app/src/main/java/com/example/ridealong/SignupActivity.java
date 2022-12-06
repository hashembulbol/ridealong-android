package com.example.ridealong;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ridealong.api.RetroClient;
import com.example.ridealong.api.models.User;
import com.example.ridealong.misc.SavedSharedPreference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    EditText etUn;
    EditText etPwd;
    EditText etPwd2;
    Button btnSignup;
    ToggleButton tglSignup;
    TextView tvAlready;
    boolean usertype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUn = (EditText) findViewById(R.id.unSignupText);
        etPwd = (EditText) findViewById(R.id.pwdSignupTxt);
        etPwd2 = (EditText) findViewById(R.id.pwd2SignupTxt);
        tvAlready = findViewById(R.id.tvLogin);
        tglSignup = findViewById(R.id.tglSignup);
        btnSignup = findViewById(R.id.signupBtn);

        tvAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                SignupActivity.this.startActivity(i);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String un = etUn.getText().toString();
                String pwd = etPwd.getText().toString();
                String pwd2 = etPwd2.getText().toString();
                String user = tglSignup.getText().toString();
                if (user.matches("Passenger")) usertype = false;
                else usertype = true;
                Log.d("Usertype", user);
                if (un.matches("") || pwd.matches("") || pwd2.matches("")){
                    Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_LONG).show();
                }
                else
                {
                    if (!pwd.equals(pwd2)){
                        Toast.makeText(SignupActivity.this, "Passwords doesn't match", Toast.LENGTH_LONG).show();
                    }
                    else{

                        if (isComplex(pwd)){
                            if (usernameIsFine(un))
                                signup(un, pwd, usertype);

                            else
                                Toast.makeText(SignupActivity.this, "Username is invalid", Toast.LENGTH_SHORT).show();
                        }

                        else
                            Toast.makeText(SignupActivity.this, "Password must contain 8 Characters, lower and upper case and one special character", Toast.LENGTH_LONG).show();

                    }
                }
            }
        });


    }

    private void signup(String un, String pwd, boolean isDriver){


        Call<User> createprofile = RetroClient.getInstance().getMyApi().signup(un, pwd, isDriver);
        ProgressDialog pd = new ProgressDialog(SignupActivity.this);
        pd.setMessage("Loading");
        pd.show();

        createprofile.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("SignupActivity", response.toString());
                if(response.isSuccessful() && response.body() !=null && response.body().getUsername() != null){
                    Toast.makeText(SignupActivity.this, "Created successfully", Toast.LENGTH_LONG).show();
                    SavedSharedPreference.setProfile(SignupActivity.this,response.body());
                    SavedSharedPreference.setPassword(SignupActivity.this, pwd);
                    Intent i;
                    if (isDriver)
                        i = new Intent(SignupActivity.this, DriverActivity.class);
                    else
                        i = new Intent(SignupActivity.this, PassengerActivity.class);

                    i.putExtra("firsttime", true);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    SignupActivity.this.startActivity(i);
                    SignupActivity.this.finish();

                }

                else{
                    Toast.makeText(SignupActivity.this, "Username already exists", Toast.LENGTH_LONG).show();
                }

                pd.dismiss();

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("TEST", t.getMessage());
                Toast.makeText(SignupActivity.this, t.getMessage().toString(), Toast.LENGTH_LONG).show();
                //Toast.makeText(SignupActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                pd.dismiss();

            }
        });


    }

    private boolean isComplex(String pwd){

        return pwd.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"); // at least 8 characters, one lowercase and one upper case and one special char
    }

    private boolean usernameIsFine(String un){

        return un.matches("^(?=.{8,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$"); // username is 8-20 characters long, no _ or . at the beginning,  no __ or _. or ._ or .. inside, ,  no _ or . at the end
    }
}


