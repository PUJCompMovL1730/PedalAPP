package com.example.usuario.mybd;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText txtEmail ;
    EditText txtPasswd;
    Button btnLogin;
    Button btnSign;

    @Override
    protected void onStart()
        { super.onStart(); mAuth.addAuthStateListener(mAuthListener); }
    @Override
    public void onStop()
        { super.onStop(); if (mAuthListener!= null) { mAuth.removeAuthStateListener(mAuthListener); } }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtEmail=(EditText)findViewById(R.id.email);
        txtPasswd=(EditText)findViewById(R.id.password);
        btnSign =(Button) findViewById(R.id.btnSigup);
        btnSign .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    Toast.makeText(getBaseContext(),"GG.",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, SignUp.class));

            }
        });

        btnLogin=(Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    Toast.makeText(getBaseContext(),"GG.",Toast.LENGTH_SHORT).show();
                login();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                 //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                startActivity(new Intent(MainActivity.this, LocalizacionUser.class));
            }
            else { // User is signed out
                // Log.d(TAG, "onAuthStateChanged:signed_out");
            //    Toast.makeText(getBaseContext(),"No se ha podido inicar session.",Toast.LENGTH_SHORT).show();

            }
            }
        };

    }



    public  void login(){
        try {

            String email,password;
            email=txtEmail.getText().toString();
            if(email.isEmpty()) {txtEmail.setError("Required."); return ;}
            if (!email.matches("[_A-Za-z0-9-]*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*")){
                txtEmail.setError("No es un correo."); return ;
            }

            txtEmail.setError(null);

            password=txtPasswd.getText().toString();

            if(password.isEmpty()) {txtPasswd.setError("Required."); return ;}

            txtPasswd.setError(null);

            Toast.makeText(getBaseContext(),"Logeando: "+email,Toast.LENGTH_SHORT).show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            { @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    //       Log.w(TAG, "signInWithEmail:failed", task.getException());
                    Toast.makeText(MainActivity.this, "Fallo \n"+task.getException(), Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivity(new Intent(MainActivity.this, LocalizacionUser.class));

                }
            }
            });

        }catch (Exception e){
            Toast.makeText(MainActivity.this, "Fallo \n"+e, Toast.LENGTH_SHORT).show();

        }
    }
}
