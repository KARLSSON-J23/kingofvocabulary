
package com.example.kingofvocabulary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {

    private TextView btn_register ;
    private Button btn_login, btn_back;
    private EditText edt_email, edt_password;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        setTitle("Login");

        btn_register = findViewById(R.id.btn_register_ID);
        btn_login = findViewById(R.id.btn_loginInLayout_ID);
        btn_back = findViewById(R.id.btn_back_ID);

        edt_email = findViewById(R.id.edt_loginEmail_ID);
        edt_password = findViewById(R.id.edt_loginPassword_ID);

        btn_register.setOnClickListener(onClickListener);
        btn_login.setOnClickListener(onClickListener);
        btn_back.setOnClickListener(onClickListener);



    }
    private Activity activity;
    private AlertDialog alertDialog;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.btn_register_ID:
                {
                    Intent intent = new Intent();
                    intent.setClass(loginActivity.this,RegisterActivity.class);
                    startActivity(intent);
                }
                break;
                case R.id.btn_loginInLayout_ID:
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity.this);
                    LayoutInflater layoutInflater = loginActivity.this.getLayoutInflater();
                    builder.setView(layoutInflater.inflate(R.layout.login_prograssbar,null));
                    builder.setCancelable(false);

                    alertDialog = builder.create();
                    alertDialog.show();

                    //Toast.makeText(loginActivity.this, "登入中...",Toast.LENGTH_SHORT).show();
                    String email, password;
                    email = edt_email.getText().toString();
                    password = edt_password.getText().toString();
                    System.out.println(email);
                    System.out.println(password);




                    login(email, password);

                }
                break;
                case R.id.btn_back_ID:
                {
                    Intent intent = new Intent();
                    intent.setClass(loginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;

            }
        }
    };
    private void login(String email, String password)
    {
        if(email.isEmpty() || password.isEmpty())
        {
            Toast toast = Toast.makeText(loginActivity.this, "請輸入電子郵件及密碼" , Toast.LENGTH_SHORT);
            toast.show();
            alertDialog.dismiss();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();


                   Intent intent = new Intent();
                   Toast toast = Toast.makeText(loginActivity.this, "登入成功" , Toast.LENGTH_SHORT);
                   toast.show();
                   intent.setClass(loginActivity.this, MainActivity.class);

                   startActivity(intent);
                   finish();
                   alertDialog.dismiss();
                }else {
                    Toast toast = Toast.makeText(loginActivity.this, "登入失敗" , Toast.LENGTH_SHORT);
                    toast.show();
                    alertDialog.dismiss();
                }
            }
        });
    }

}
