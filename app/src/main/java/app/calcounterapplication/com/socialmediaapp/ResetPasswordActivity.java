package app.calcounterapplication.com.socialmediaapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private Button mResetButton;
    private EditText mEmailText;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mResetButton = findViewById(R.id.reset_password_button);
        mEmailText = findViewById(R.id.reset_password_input);
        mAuth = FirebaseAuth.getInstance();


        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mCurrentEmailText = mEmailText.getText().toString();
                if (TextUtils.isEmpty(mCurrentEmailText)) {
                    Toast.makeText(ResetPasswordActivity.this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.sendPasswordResetEmail(mCurrentEmailText).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this, "For Reset your password , check your email account", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivty.class));
                            } else {
                                String massage = task.getException().toString();
                                Toast.makeText(ResetPasswordActivity.this, "Error Occurred: " + massage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    }

}

