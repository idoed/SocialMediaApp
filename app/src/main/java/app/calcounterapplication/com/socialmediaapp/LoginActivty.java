package app.calcounterapplication.com.socialmediaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivty extends AppCompatActivity {
    private Button mLogBut;
    private EditText userEmail, userPassword;
    private TextView needNewAccountLink,mForgetPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog mLoadingBar;
    private final String TAG = "LoginActivity";
    private ImageView mGoogleSignBut;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mgooglesignClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activty);
        Log.v(TAG, "");
        mForgetPassword=findViewById(R.id.forget_password_link);
        needNewAccountLink = findViewById(R.id.login_need_new_account);
        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        mGoogleSignBut = findViewById(R.id.google_signing_button);
        mLogBut = findViewById(R.id.login_create_account);
        mLoadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        needNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    SendUserToRegisterActivity();
                }
            }
        });
        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivty.this,ResetPasswordActivity.class));
            }
        });
        mLogBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogin();
            }
        });
    }

    private void AllowUserToLogin() {
        String mEmail = userEmail.getText().toString().trim();
        String mPass = userPassword.getText().toString().trim();

        if (TextUtils.isEmpty(mEmail)) {
            Toast.makeText(this, "Please write your Email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mPass)) {
            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();
        } else {
            mLoadingBar.setTitle("Log In!");
            mLoadingBar.setMessage("Please wait while your logging in");
            mLoadingBar.show();
            mLoadingBar.setCanceledOnTouchOutside(true);
            mAuth.signInWithEmailAndPassword(mEmail, mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mLoadingBar.dismiss();
                        SendUserToMainActivity();
                    } else {
                        mLoadingBar.dismiss();
                        String message = task.getException().getMessage();
                        Toast.makeText(LoginActivty.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mgooglesignClient=new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(LoginActivty.this, "Connection to Google Account Failed", Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        mGoogleSignBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mgooglesignClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            mLoadingBar.setTitle("Google Sign In!");
            mLoadingBar.setMessage("Please wait while your logging in with your Google Account");
            mLoadingBar.show();
            mLoadingBar.setCanceledOnTouchOutside(true);
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount account=result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else {
                Toast.makeText(this, "Cant get Auth Result", Toast.LENGTH_SHORT).show();
                mLoadingBar.dismiss();
            }

        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            SendUserToMainActivity();
                            mLoadingBar.dismiss();

                        } else {
                            mLoadingBar.dismiss();
                            SendUserToLoginActivity();

                        }

                    }
                });
    }

    private void SendUserToLoginActivity() {
        Intent mainActivityIntent = new Intent(LoginActivty.this, LoginActivty.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {
        Intent mainActivityIntent = new Intent(LoginActivty.this, MainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivty.this, RegisterActivity.class);
        startActivity(registerIntent);

    }
}
