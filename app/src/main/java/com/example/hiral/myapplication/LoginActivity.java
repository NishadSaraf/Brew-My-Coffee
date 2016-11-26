package com.example.hiral.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;
/**
 * Created by Hiral on 7/28/2016.
 */
/**
 * A login screen that offers login via email/password.
 * Provides Functionality to Signup for new account
 */
public class LoginActivity extends AppCompatActivity {

    // UI references
    private EditText mEmail;
    private EditText mPasswordView;
    private Button mSignUpButton;
    Button mSignInButton;
    LoginDataBaseAdapter loginDataBaseAdapter;
    private String userName;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            userName = intent.getStringExtra(userName);
            password = intent.getStringExtra(password);
        }

        // Creates object for LoginDataBaseAdapter to gain access to database
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();

        mEmail = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        //OnClick Listener for SignInButton
        Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // Code below gets the User name and Password
                 userName = mEmail.getText().toString();
                 password = mPasswordView.getText().toString();

                String passwordfromdb = loginDataBaseAdapter.getSingleEntry(userName);
                if(passwordfromdb.equals("NOT EXIST")){
                    Toast.makeText(getBaseContext(),R.string.username_does_not_exist,LENGTH_SHORT).show();
                }
                else{
                    if(password.equals(passwordfromdb)){
                        Intent intent = new Intent(LoginActivity.this,LocationUpdate.class);
                        intent.putExtra("tx_user_name",userName);
                        startActivity(intent);
                    }
                    else
                    {
                        //show toast
                        Toast.makeText(getBaseContext(),R.string.password_does_not_match,LENGTH_SHORT).show();
                    }
                }
            }
        });

        mSignUpButton = (Button)findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_up =new Intent(LoginActivity.this,SignUPActivity.class);
                startActivity(sign_up);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString("username", userName);
        savedInstanceState.putString("password", password);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        loginDataBaseAdapter.close();
    }
}