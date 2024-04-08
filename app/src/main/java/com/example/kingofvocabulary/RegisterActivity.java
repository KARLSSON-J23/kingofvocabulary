package com.example.kingofvocabulary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity {
    private Button btn_register;
    private EditText edt_email, edt_password, edt_againPassword;
    private FirebaseAuth mAuth ;
    protected void onCreate(Bundle saveInstanceState)
    {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.layout_register);
        setTitle("Register");

        btn_register = findViewById(R.id.btn_sendRegister_ID);

        edt_email = findViewById(R.id.edt_registerEmail_ID);
        edt_password = findViewById(R.id.edt_registerPassword_ID);
        edt_againPassword = findViewById(R.id.edt_registerPasswordAgain_ID);

        btn_register.setOnClickListener(onClickListener);

        mAuth = FirebaseAuth.getInstance();




    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.btn_sendRegister_ID:
                {
                    if (checkFormat())
                    {
                        Toast toast = Toast.makeText(RegisterActivity.this, "連線中" , Toast.LENGTH_SHORT);
                        toast.show();
                        registerNewUser(edt_email.getText().toString(), edt_password.getText().toString());//註冊

                    }
                    else
                    {

                    }
                }
                break;
            }
        }
    };
    private boolean checkFormat()
    {
        boolean ans = false;
        String pwd, pwdAgain, email;
        pwd = edt_password.getText().toString();
        pwdAgain = edt_againPassword.getText().toString();
        email = edt_email.getText().toString();

        if(!email.isEmpty() && !pwd.isEmpty() && !pwdAgain.isEmpty()) {
            if (pwd.equals(pwdAgain)) {
                if (pwd.length() >= 8) {

                    ans = true;
                } else {
                    toastShow("密碼需大於等於八位");
                }

            } else {
                toastShow("密碼不一致");
            }
        }
        else {
            toastShow("電子郵件或密碼不得為空");
        }
        return ans;
    }
    private void toastShow(String str)
    {
        Toast toast = Toast.makeText(RegisterActivity.this,str,Toast.LENGTH_SHORT);
        toast.show();
    }
    private boolean registerNewUser(String email, String password)
    {
        boolean ans =false;



        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast toast = Toast.makeText(RegisterActivity.this, "註冊成功", Toast.LENGTH_LONG);
                    toast.show();

                    Intent intent = new Intent();
                    intent.setClass(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);finish();
                } else {
                    Toast toast = Toast.makeText(RegisterActivity.this, "註冊失敗," + task.getException().getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });


        return ans;
    }

}
