package com.example.admin.logingoogle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Tag";
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton button_signin;
    Button button_signout;

    int RC_SIGN_IN = 1;

    TextView text_name, text_email;
    ImageView image_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControls();
        requestUser();

        button_signin.setOnClickListener(this);
        button_signout.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //checkUserExist();
        revokeAccess();
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(MainActivity.this, "Revoke Access", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            /* Signed in successfully, show authenticated UI.*/
            updateUI(account);
        } catch (ApiException e) {

            /* The ApiException status code indicates the detailed failure reason.
             Please refer to the GoogleSignInStatusCodes class reference for more information.*/
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void checkUserExist() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Toast.makeText(this, "User is exist", Toast.LENGTH_SHORT).show();
//        updateUI(account);
    }

    private void updateUI(GoogleSignInAccount account) {
        text_email.setText(account.getEmail().toString());
        text_name.setText(account.getDisplayName().toString());
        Picasso.with(this).load(account.getPhotoUrl()).into(image_avatar);
    }

    private void requestUser() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    }


    private void addControls() {

        button_signin = findViewById(R.id.button_signin);
        button_signin.setSize(button_signin.SIZE_STANDARD);

        text_email = findViewById(R.id.text_email);
        text_name = findViewById(R.id.text_name);
        image_avatar = findViewById(R.id.image_avatar);
        button_signout = findViewById(R.id.button_signout);

        button_signout.setVisibility(View.INVISIBLE);
        button_signin.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_signin :
                signIn();

                break;
            case R.id.button_signout:
                signOut();
                break;
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.w(TAG, "log out succes !");
                        text_email.setText("");
                        text_name.setText("");
                        image_avatar.setImageBitmap(null);
                        button_signout.setVisibility(View.INVISIBLE);
                        button_signin.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

        button_signin.setVisibility(View.INVISIBLE);
        button_signout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}