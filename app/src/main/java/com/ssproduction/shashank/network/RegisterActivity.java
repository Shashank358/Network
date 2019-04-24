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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputLayout userName, userId, userEmail, userPassword, userCnfmPass;
    private Button createBtn;
    private TextView linkToLogin;
    private ProgressDialog dialog;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        userName = (TextInputLayout) findViewById(R.id.register_user_name);
        userId = (TextInputLayout) findViewById(R.id.register_user_id);
        userEmail = (TextInputLayout) findViewById(R.id.register_email);
        userPassword = (TextInputLayout) findViewById(R.id.login_user_password);
        createBtn = (Button) findViewById(R.id.register_create_account_btn);
        linkToLogin = (TextView) findViewById(R.id.link_of_login_text);
        userCnfmPass = (TextInputLayout) findViewById(R.id.login_user_cnfm_password);

        linkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.setMessage("creating account");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                String name = userName.getEditText().getText().toString();
                String id = userId.getEditText().getText().toString();
                String email = userEmail.getEditText().getText().toString();
                String pass = userPassword.getEditText().getText().toString();
                String cnfmPass = userCnfmPass.getEditText().getText().toString();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(id) && !TextUtils.isEmpty(email)
                        && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(cnfmPass)) {
                    if (pass.equals(cnfmPass)){
                        userSignUp(name, id, email, pass);
                    }else {
                        dialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "check your password again", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(RegisterActivity.this, "please fill all the credentials", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private void userSignUp(final String name, final String id, String email, String pass) {

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                FirebaseUser currentUser = mAuth.getCurrentUser();
                String uid = currentUser.getUid();
                if (task.isSuccessful()){
                    dialog.dismiss();

                    Map map = new HashMap();
                    map.put("user_name", name);
                    map.put("user_id", id);
                    map.put("page_name", "your college");
                    map.put("user_thumbImage", "default");
                    map.put("user_image", "default");
                    map.put("online", true);
                    map.put("userKey_id", uid);
                    map.put("search", name.toLowerCase());

                    mDatabase.child("Users").child(uid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent MainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(MainIntent);
                            }else {
                                dialog.hide();
                                Toast.makeText(RegisterActivity.this, "error in creating account", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "error", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
