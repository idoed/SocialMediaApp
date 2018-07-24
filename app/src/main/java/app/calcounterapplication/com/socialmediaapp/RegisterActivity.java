package app.calcounterapplication.com.socialmediaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private final String  TAG="RegisterActivity";
    private EditText mUserEmail, mUserPassword, mUserConfirmPassword;
    private Button mCreateAccountButt;
    private FirebaseAuth mAuth;
    private ProgressDialog mLoadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.v(TAG,"");
        mAuth = FirebaseAuth.getInstance();
        mCreateAccountButt = findViewById(R.id.register_create_account);
        mUserEmail = findViewById(R.id.register_email);
        mUserPassword = findViewById(R.id.register_password);
        mLoadingBar=new ProgressDialog(this);
        mUserConfirmPassword = findViewById(R.id.register_confirm_password);
        mCreateAccountButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {
        String mEmail = mUserEmail.getText().toString();
        String mPassword = mUserPassword.getText().toString();
        String mConfirmPassword = mUserConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(mEmail)) {
            Toast.makeText(this, "Please write your Email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mPassword)) {
            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mConfirmPassword)) {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
        } else if (!mPassword.equals( mConfirmPassword)) {
            Toast.makeText(this, "Your Password do not match with your confirm password", Toast.LENGTH_SHORT).show();

        } else {
            mLoadingBar.setTitle("Creating new Account!");
            mLoadingBar.setMessage("Please wait till we are creating your new Account..");
            mLoadingBar.show();
            mLoadingBar.setCanceledOnTouchOutside(true);
            mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        SendUserToSetupActivity();
                        mLoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        mLoadingBar.dismiss();
                        String message=task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Error Occurred: "+message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void SendUserToSetupActivity() {
        Intent sendToSetupActivityIntent= new Intent(RegisterActivity.this,SetupActivity.class);
        sendToSetupActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sendToSetupActivityIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {
        Intent mainActivityIntent= new Intent(RegisterActivity.this,MainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }
}
