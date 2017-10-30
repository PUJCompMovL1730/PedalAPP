package com.example.usuario.mybd;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.RelativeDateTimeFormatter;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    public static final String PATH_USERS="users/";

    //Atributos
    FirebaseDatabase database;
    DatabaseReference myRef;
    private StorageReference mStorage;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    EditText txtEmail;
    EditText txtPasswd;
    EditText txtComprbPasswd;
    EditText txtNombre;
    EditText txtApellido;
    Spinner ciudad;
    Spinner sexo;
    Spinner tipo;
    DatePicker fechaNacimiento;

    static final int REQUEST_IMAGE_CAPTURE = 2;
    static final int REQUEST_IMAGE_CAPTURE2 = 1;
    static final int STORAGE = 3;
    static final int  IMAGE_PICKER_REQUEST =4;
    String urlPhoto="path/to/pic";

    @Override
    protected void onStart()
    { super.onStart(); mAuth.addAuthStateListener(mAuthListener); }
    @Override
    public void onStop()
    { super.onStop(); if (mAuthListener!= null) { mAuth.removeAuthStateListener(mAuthListener); } }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    startActivity(new Intent(SignUp.this, LocalizacionUser.class));
                }
                else { // User is signed out
                    // Log.d(TAG, "onAuthStateChanged:signed_out");
                  //  Toast.makeText(getBaseContext(),"No se ha podido inicar session.",Toast.LENGTH_SHORT).show();

                }
            }
        };
        storage=FirebaseStorage.getInstance();
        mStorage=storage.getReference();
        database= FirebaseDatabase.getInstance();
        txtEmail=(EditText)findViewById(R.id.signCorreo);
        txtPasswd=(EditText)findViewById(R.id.signPasswd);
        txtComprbPasswd = (EditText)findViewById(R.id.signPasswdVerification);
        txtNombre=(EditText)findViewById(R.id.signNombre);
        txtApellido=(EditText)findViewById(R.id.signApellido);
        ciudad=(Spinner)findViewById(R.id.s_cities);
        tipo=(Spinner)findViewById(R.id.s_user_type);
        sexo=(Spinner)findViewById(R.id.s_gender);
        fechaNacimiento=(DatePicker) findViewById(R.id.datePicker);



        Button btnReg =(Button) findViewById(R.id.btnRegistrarse);
        btnReg .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    Toast.makeText(getBaseContext(),"GG.",Toast.LENGTH_SHORT).show();
                String email="", password="",password2="";
                try {
                    email=txtEmail.getText().toString();
                    if(email.isEmpty()) {txtEmail.setError("Required."); return ;}
                     if (!email.matches("[_A-Za-z0-9-]*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*")){
                         txtEmail.setError("No es un correo."); return ;
                     }
                    txtEmail.setError(null);
                } catch (Exception e) {
                    txtEmail.setError("Required.");
                }

                 password="";
                try {
                    password=txtPasswd.getText().toString();
                    password2 = txtComprbPasswd.getText().toString();
                    if(password.isEmpty()) {txtPasswd.setError("Required."); return ;}

                    if(!password2.equals(password)){
                        txtPasswd.setError("No coinciden");
                        txtComprbPasswd.setError("No coinciden");
                        return;
                    }

                    txtPasswd.setError(null);
                } catch (Exception e) {
                    txtPasswd.setError("Required.");
                }


                mAuth.createUserWithEmailAndPassword(email, password) .addOnCompleteListener(SignUp.this,
                            new OnCompleteListener<AuthResult>()
                    { @Override public void onComplete(@NonNull Task<AuthResult> task) {


                        if(task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null) { //Update user Info
                              //  mStorage=FirebaseDatabase.getInstance().getReference();

                                UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                                upcrb.setDisplayName(txtNombre.getText()+" "+txtApellido.getText());
        //                        upcrb.setDisplayName("usuario 2");
                                upcrb.setPhotoUri(Uri.parse(urlPhoto));//fake uri, real one coming soon
                                user.updateProfile(upcrb.build());

                                StorageReference ref=mStorage.child("images/"+user.getUid()+".jpg");
                                ref.putFile(Uri.parse(urlPhoto));
                                MyUser myUser = new MyUser();
                                myUser.setNombres(txtNombre.getText().toString());
                                myUser.setApellidos(txtApellido.getText().toString());
                                myUser.setCiudad(ciudad.getSelectedItem().toString());
                                myUser.setTipo(tipo.getSelectedItem().toString());
                                myUser.setSexo(sexo.getSelectedItem().toString());
                                myUser.setCorreo(txtEmail.getText().toString());
                                myUser.setAltura(1.80f);
                                myUser.setPeso(80);


                                int day = fechaNacimiento.getDayOfMonth();
                                int month = fechaNacimiento.getMonth();
                                int year =  fechaNacimiento.getYear();

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, month, day);

                                Calendar today = Calendar.getInstance();
                                int diff_year = today.get(Calendar.YEAR) -  year;
                                int diff_month = today.get(Calendar.MONTH) - month;
                                int diff_day = today.get(Calendar.DAY_OF_MONTH) - day;

                                //Si está en ese año pero todavía no los ha cumplido
                                if (diff_month < 0 || (diff_month == 0 && diff_day < 0)) {
                                    diff_year = diff_year - 1; //no aparecían los dos guiones del postincremento :|
                                }
                                myUser.setEdad(diff_year);
                                myUser.setFechaNacimiento(calendar.getTime());
                                myUser.setAmigos(new ArrayList<String>());
                                myUser.setBuzonEntrada(new HashMap<String, String>());

                                myUser.setBuzonSalida(new HashMap<String, String>());
                                myRef=database.getReference(PATH_USERS+user.getUid());
                                myRef.setValue(myUser);
                                startActivity(new Intent(SignUp.this, LocalizacionUser.class)); //o  en el listener
                            }
                        }
                        else{
                            Toast.makeText(SignUp.this,  "Failed: "+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }});


        }

        });

        Button btn=(Button)findViewById(R.id.btnCamara);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(v.getContext(),
                        android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {


                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) v.getContext(), android.Manifest.permission.CAMERA)) {
                        // Show an expanation to the user *asynchronously   
                        Toast.makeText(v.getContext(), "Es necesario para activar la camara", Toast.LENGTH_LONG).show();
                    }
                    // Request the permission.
                    ActivityCompat.requestPermissions((Activity) v.getContext(),
                            new String[]{android.Manifest.permission.CAMERA},
                            REQUEST_IMAGE_CAPTURE);


                }else camara();
            }
        });

        Button btn2=(Button)findViewById(R.id.btnSeleccionar);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(v.getContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {


                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) v.getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Show an expanation to the user *asynchronously   
                        Toast.makeText(v.getContext(), "Es necesario para listar los contactos", Toast.LENGTH_LONG).show();
                    }
                    // Request the permission.
                    ActivityCompat.requestPermissions((Activity) v.getContext(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE);




                }else seleccionar();
            }
        });

    }






    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, continue with task related to permission
                    camara();
                }
            }
            case STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, continue with task related to permission
                    seleccionar();
                }
            }

        }
    }
    void camara(){

        Intent takePictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE2);
        }

    }
    void seleccionar(){

        Intent pickImage = new Intent(Intent.ACTION_PICK); pickImage.setType("image/*");
        startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);

    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE2 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            urlPhoto=data.getData().toString();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView mImageView=(ImageView) findViewById(R.id.imageView);
            mImageView.setImageBitmap(imageBitmap);
        }
        if(resultCode == RESULT_OK && requestCode == IMAGE_PICKER_REQUEST ){
            try { final Uri imageUri = data.getData();
                urlPhoto=imageUri.toString();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ImageView mImageView=(ImageView) findViewById(R.id.imageView);
                mImageView.setImageBitmap(selectedImage); }
            catch (FileNotFoundException e)
            { e.printStackTrace(); }
        }

    }

}