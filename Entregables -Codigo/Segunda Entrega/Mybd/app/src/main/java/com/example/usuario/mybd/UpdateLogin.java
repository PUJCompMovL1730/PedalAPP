package com.example.usuario.mybd;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;

public class UpdateLogin extends AppCompatActivity {

    //Info del Usuario
    EditText et_actualPassword;
    EditText et_newPassword;
    EditText et_passwordRepeat;
    EditText et_correo;

    //Switches
    Switch s_password;
    Switch s_email;

    //Botones
    Button btn_actualizar;

    //Para utilizar la base de datos
    FirebaseDatabase database;
    DatabaseReference myRef;

    //Autenticación del usuario actual
    FirebaseUser thisUser;

    //Para utilizar la autenticación de Firebase
    FirebaseAuth mAuth;

    //Paths para ubicar datos en la BD
    public static final String PATH_USERS="users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_login);

        //Inicializar info del usuario
        et_actualPassword = (EditText) findViewById(R.id.actualPassword);
        et_newPassword = (EditText) findViewById(R.id.updatePassword);
        et_passwordRepeat = (EditText) findViewById(R.id.updatePasswordSecond);
        et_correo = (EditText) findViewById(R.id.updateEmail);

        //Inicializar switches
        s_password = (Switch) findViewById(R.id.switchNewPassword);
        s_email = (Switch) findViewById(R.id.switchNewEmail);

        //Inicializar botón
        btn_actualizar = (Button) findViewById(R.id.btn_updateLogin);

        //Inicializar la base de datos
        database = FirebaseDatabase.getInstance();

        //Inicializar la autenticación
        mAuth = FirebaseAuth.getInstance();
        //Inicializar al usuario autenticado
        thisUser = FirebaseAuth.getInstance().getCurrentUser();

        //ACCIONES SOBRE LOS SWITCH
        s_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(s_password.isChecked()){
                    et_actualPassword.setEnabled(true);
                    et_newPassword.setEnabled(true);
                    et_passwordRepeat.setEnabled(true);
                    btn_actualizar.setEnabled(true);
                }else{
                    if(!s_email.isChecked()){
                        et_actualPassword.setEnabled(false);
                        et_correo.setEnabled(false);
                        btn_actualizar.setEnabled(false);
                    }
                    et_newPassword.setEnabled(false);
                    et_passwordRepeat.setEnabled(false);
                }
            }
        });

        s_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(s_email.isChecked()){
                    et_actualPassword.setEnabled(true);
                    et_correo.setEnabled(true);
                    btn_actualizar.setEnabled(true);
                }else{
                    if(!s_password.isChecked()){
                        et_actualPassword.setEnabled(false);
                        et_newPassword.setEnabled(false);
                        et_passwordRepeat.setEnabled(false);
                        btn_actualizar.setEnabled(false);
                    }
                    et_correo.setEnabled(false);
                }
            }
        });

        //ACCIONES SOBRE EL BOTÓN
        btn_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String actualPassword = et_actualPassword.getText().toString();

                if(!actualPassword.isEmpty()){

                    if(s_email.isChecked()){
                        final String correo = et_correo.getText().toString();
                        if(!correo.isEmpty()){
                            if (correo.matches("[_A-Za-z0-9-]*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*")){

                                mAuth.signInWithEmailAndPassword(thisUser.getEmail(), actualPassword).addOnCompleteListener(UpdateLogin.this, new OnCompleteListener<AuthResult>()
                                {@Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        //       Log.w(TAG, "signInWithEmail:failed", task.getException());
                                        Toast.makeText(UpdateLogin.this, "Fallo "+task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        actualizarCorreo();
                                    }
                                }
                                });
                            }else{
                                et_correo.setError("No es un correo.");
                            }
                        }else{
                            et_correo.setError("Required");
                            return;
                        }
                    }

                    if(s_password.isChecked()){
                        final String newPassword = et_newPassword.getText().toString();
                        String newPasswordRepeat = et_passwordRepeat.getText().toString();

                        if(!newPassword.isEmpty()){
                            if(!newPasswordRepeat.isEmpty()){
                                if(newPassword.equals(newPasswordRepeat)){
                                    mAuth.signInWithEmailAndPassword(thisUser.getEmail(), actualPassword).addOnCompleteListener(UpdateLogin.this, new OnCompleteListener<AuthResult>()
                                    {@Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            //       Log.w(TAG, "signInWithEmail:failed", task.getException());
                                            Toast.makeText(UpdateLogin.this, "Fallo "+task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            thisUser.updatePassword(newPassword);
                                            Toast.makeText(UpdateLogin.this,"Contraseña actualizada",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    });
                                }else{
                                    et_newPassword.setError("Deben coincidir");
                                    et_passwordRepeat.setError("Deben coincidir");
                                }
                            }else{
                                et_passwordRepeat.setError("Required");
                            }
                        }else{
                            et_newPassword.setError("Required");
                        }
                    }
                }else{
                    et_actualPassword.setError("Required");
                    return;
                }

            }
        });

    }

    //Actualizar el correo del usuario actual
    public void actualizarCorreo() {

        myRef = database.getReference(PATH_USERS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String correoDeseado = et_correo.getText().toString();
                //Bandera para saber si ya hay otro usuario con el correo deseado
                boolean yaAsignado = false;

                MyUser yo = new MyUser();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    MyUser myUser = singleSnapshot.getValue(MyUser.class);
                    if(myUser.getCorreo().equalsIgnoreCase(thisUser.getEmail())) {
                        //Soy yo

                        //Toast.makeText(getBaseContext(),"Se encontro al usuario Yo "+myUser.getNombres(),Toast.LENGTH_SHORT).show();
                        yo = myUser;
                    }

                    if(myUser.getCorreo().equals(correoDeseado)){
                        yaAsignado = true;
                    }
                }


                if(!yaAsignado){
                    yo.setCorreo(correoDeseado);
                    myRef = database.getReference(PATH_USERS+thisUser.getUid());
                    myRef.setValue(yo);

                    thisUser.updateEmail(correoDeseado);
                    Toast.makeText(UpdateLogin.this,"Correo electrónico actualizado",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(UpdateLogin.this,R.string.errorUpdateCorreoAsign,Toast.LENGTH_SHORT).show();
                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
                Toast.makeText(getBaseContext(),"Error lectura user",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
