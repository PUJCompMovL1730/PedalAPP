package com.example.usuario.mybd;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class AddSocialNetwork extends AppCompatActivity {
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize Facebook SDK

        FacebookSdk.setApplicationId("551011411917634");
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_add_social_network);
        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton=(LoginButton)findViewById(R.id.login_button);
  /*      loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                ((EditText) findViewById(R.id.login_button)).setText("Conectado");
                //  LoginManager.getInstance().logInWithReadPermissions((Activity) getBaseContext(), Arrays.asList("public_profile"));

            }

            @Override
            public void onCancel() {
                // App code
                ((EditText) findViewById(R.id.login_button)).setText("Cancel ");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                ((EditText) findViewById(R.id.login_button)).setText("error "+exception);

            }
        });
*/

    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
