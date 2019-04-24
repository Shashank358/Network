package com.ssproduction.shashank.network;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextView linkToReg;
    private FirebaseAuth mAuth;
    private Button loginBtn;
    private TextInputLayout userEmailMob, userPassword;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialog = new ProgressDialog(this);

        userEmailMob = (TextInputLayout) findViewById(R.id.login_user_email);
        userPassword = (TextInputLayout) findViewById(R.id.login_user_password);
        linkToReg = (TextView) findViewById(R.id.link_of_register_text);
        loginBtn = (Button) findViewById(R.id.login_button);

        mAuth = FirebaseAuth.getInstance();

        linkToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                regIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(regIntent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email = userEmailMob.getEditText().getText().toString();
                String password = userPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(email) &&
                        !TextUtils.isEmpty(password))
                {
                    dialog.setMessage("Logging");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    LoginAccount( email, password);
                }
                if (TextUtils.isEmpty(email) ||
                        TextUtils.isEmpty(password))
                {
                    Toast.makeText(LoginActivity.this, "Please fill the following details.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void LoginAccount(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            dialog.dismiss();
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();

                        }
                        else {
                            dialog.hide();
                            Toast.makeText(LoginActivity.this, "Error..please  try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}
