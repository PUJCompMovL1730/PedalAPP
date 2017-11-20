package com.example.usuario.mybd;

import android.*;
import android.Manifest;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    EditText txtPeso;
    EditText txtAltura;
    Spinner ciudad;
    Spinner sexo;
    Spinner tipo;
    DatePicker fechaNacimiento;


    ImageView iv_profilePicture;
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
        txtPeso = (EditText) findViewById(R.id.signPeso);
        txtAltura = (EditText) findViewById(R.id.signAltura);
        ciudad=(Spinner)findViewById(R.id.s_cities);
        tipo=(Spinner)findViewById(R.id.s_user_type);
        sexo=(Spinner)findViewById(R.id.s_gender);
        fechaNacimiento=(DatePicker) findViewById(R.id.datePicker);

        iv_profilePicture = (ImageView) findViewById(R.id.imageView);



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

                String checkNombre = "", checkApellido ="", checkPeso = "", checkAltura = "";
                try {
                    checkNombre = txtNombre.getText().toString();
                    checkApellido = txtApellido.getText().toString();
                    checkPeso = txtPeso.getText().toString();
                    checkAltura = txtAltura.getText().toString();

                    if(checkNombre.isEmpty()) {txtNombre.setError("Required."); return ;}
                    if(checkApellido.isEmpty()) {txtApellido.setError("Required."); return ;}
                    if(checkPeso.isEmpty()) {txtPeso.setError("Required."); return ;}
                    if(checkAltura.isEmpty()) {txtAltura.setError("Required."); return ;}

                    if(checkNombre.charAt(0) == '_') {txtNombre.setError("No puede contener caracteres especiales al comienzo"); return ;}

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

                                String tipoStr = tipo.getSelectedItem().toString();

                                String tipoUsu = "";
                                if(tipoStr.equals("Empresa")){
                                    tipoUsu += "-emp-";
                                }

                                UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                                String displayName = tipoUsu+txtNombre.getText()+" "+txtApellido.getText();
                                upcrb.setDisplayName(displayName);
        //                        upcrb.setDisplayName("usuario 2");
                                upcrb.setPhotoUri(Uri.parse(urlPhoto));//fake uri, real one coming soon
                                user.updateProfile(upcrb.build());

                                /*StorageReference ref=mStorage.child("images/"+user.getUid()+".jpg");
                                ref.putFile(Uri.parse(urlPhoto));*/

                                StorageReference ref = mStorage.child("images/" + user.getUid());

                                // Get the data from an ImageView as bytes
                                iv_profilePicture.setDrawingCacheEnabled(true);
                                iv_profilePicture.buildDrawingCache();
                                Bitmap bitmap = iv_profilePicture.getDrawingCache();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
                                byte[] data = baos.toByteArray();

                                UploadTask uploadTask = ref.putBytes(data);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    }
                                });

                                MyUser myUser = new MyUser();
                                myUser.setNombres(txtNombre.getText().toString());
                                myUser.setApellidos(txtApellido.getText().toString());
                                myUser.setCiudad(ciudad.getSelectedItem().toString());

                                float alt = Float.parseFloat(txtAltura.getText().toString());
                                myUser.setAltura(alt);
                                float pes = Float.parseFloat(txtPeso.getText().toString());
                                myUser.setPeso(pes);

                                myUser.setSexo(sexo.getSelectedItem().toString());
                                myUser.setCorreo(txtEmail.getText().toString());



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

                                Intent intent = new Intent(SignUp.this, LocalizacionUser.class);
                                intent.putExtra("displayName",displayName);
                                startActivity(intent); //o  en el listener
                            }
                        }
                        else{
                            Toast.makeText(SignUp.this,  "Failed: "+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }});


        }

        });


        //___________ SELECCIONAR FOTO DE PERFIL __________
        Button btn=(Button)findViewById(R.id.btnCamara);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                camara();
            }
        });

        Button btn2=(Button)findViewById(R.id.btnSeleccionar);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seleccionar();
            }
        });
        //_______________________________________________________

    }






    //PARA ESCOGER LA FOTO DE PERFIL ----------------------------------------------------------------------------------------

    void camara(){

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)){
                Toast.makeText(getBaseContext(),"Necesitamos permisos para poder acceder a su cámara", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);


            }
        }else{
            //Abrir cámara para tomar foto
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    void seleccionar(){

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(getBaseContext(),"Necesitamos permisos para acceder a su galería de imágenes", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_PICKER_REQUEST);
            }else{
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_PICKER_REQUEST);

            }
        }else{
            //Abrir galeria para escoger imagen
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case IMAGE_PICKER_REQUEST:
                if(resultCode == RESULT_OK){
                    try {
                        Uri imageUri = data.getData();

                        urlPhoto = imageUri.toString();

                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imageStream.close();
                        iv_profilePicture.setImageBitmap(selectedImage);

                        Toast.makeText(getBaseContext(),"¡Imágen cargada!", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();

                    urlPhoto = data.getData().toString();

                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    iv_profilePicture.setImageBitmap(imageBitmap);
                }
                break;

        }

    }
    //--------------------------------------------------------------------------------------------------------------------------

}