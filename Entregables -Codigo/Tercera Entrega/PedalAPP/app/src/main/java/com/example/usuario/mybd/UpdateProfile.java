package com.example.usuario.mybd;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UpdateProfile extends AppCompatActivity {

    //Info del Usuario
    EditText et_nombres;
    EditText et_apellidos;
    EditText et_peso;
    EditText et_altura;
    Spinner sp_cities;
    Spinner sp_gender;
    Spinner sp_userType;
    DatePicker dp_birthday;
    ImageView iv_profilePicture;

    //Info previa del usuario
    String antNombre;
    String antApellido;
    String antCorreo;
    String antCiudad;
    String antGenero;
    String antTipoUsuario;
    Date antFechaNacimiento;
    float antPeso;
    float antAltura;

    //Botones
    Button btn_actualizar;
    Button btn_galeria;
    Button btn_camara;

    //Codigos de solicitudes
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int STORAGE = 2;
    static final int IMAGE_PICKER_REQUEST = 3;

    //URL foto de perfil
    String urlPhoto = "path/to/pic";

    //Para utilizar la base de datos
    FirebaseDatabase database;
    DatabaseReference myRef;

    //Paths para ubicar datos en la BD
    public static final String PATH_USERS = "users/";

    //Autenticación del usuario actual
    FirebaseUser thisUser;

    //Para utilizar el almacenaje de FireBase
    FirebaseStorage storage;
    StorageReference mStorage;

    //Bandera para cambio de imágen de perfil
    boolean cambioImagen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        //Inicializar info del usuario
        et_nombres = (EditText) findViewById(R.id.updateNombre);
        et_apellidos = (EditText) findViewById(R.id.updateApellido);
        et_peso = (EditText) findViewById(R.id.updatePeso);
        et_altura = (EditText) findViewById(R.id.updateAltura);
        sp_cities = (Spinner) findViewById(R.id.updateCity);
        sp_gender = (Spinner) findViewById(R.id.updateGender);
        sp_userType = (Spinner) findViewById(R.id.updateUserType);
        dp_birthday = (DatePicker) findViewById(R.id.updateBirthday);
        iv_profilePicture = (ImageView) findViewById(R.id.updateProfilePicture);

        //Inicializar botones
        btn_actualizar = (Button) findViewById(R.id.btn_update);
        btn_galeria = (Button) findViewById(R.id.btn_galeria);
        btn_camara = (Button) findViewById(R.id.btn_camara);

        //Inicializar la base de datos
        database = FirebaseDatabase.getInstance();

        //Inicializar al usuario autenticado
        thisUser = FirebaseAuth.getInstance().getCurrentUser();

        //Inicializar el almacenamiento de Firebase
        storage = FirebaseStorage.getInstance();
        mStorage = storage.getReference();

        //CARGAR DATOS DEL USUARIO
        loadUser();

        //Inicializar bandera
        cambioImagen = false;


        btn_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef = database.getReference(PATH_USERS + thisUser.getUid());
                MyUser myUser = new MyUser();

                updateUser();
            }
        });

        //CARGAR IMÁGEN DE PERFIL
        btn_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                camara();
            }
        });

        btn_galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seleccionar();
            }
        });


    }


    //Leer datos de la base de datos
    public void loadUser() {

        myRef = database.getReference(PATH_USERS);


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                MyUser yo = new MyUser();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    MyUser myUser = singleSnapshot.getValue(MyUser.class);

                    if (myUser.getCorreo().equalsIgnoreCase(thisUser.getEmail())) {
                        //Soy yo

                        //Toast.makeText(getBaseContext(),"Se encontro al usuario Yo "+myUser.getNombres(),Toast.LENGTH_SHORT).show();
                        yo = myUser;
                        break;
                    }
                }

                antNombre = yo.getNombres();
                et_nombres.setText(antNombre);

                antApellido = yo.getApellidos();
                et_apellidos.setText(antApellido);

                antPeso = yo.getPeso();
                et_peso.setText(antPeso + "");

                antAltura = yo.getAltura();
                et_altura.setText(antAltura + "");

                antCiudad = yo.getCiudad();
                for (int i = 0; i < sp_cities.getAdapter().getCount(); ++i) {
                    if (antCiudad.equals(sp_cities.getItemAtPosition(i).toString())) {
                        sp_cities.setSelection(i);
                    }
                }

                antGenero = yo.getSexo();
                for (int i = 0; i < sp_gender.getAdapter().getCount(); ++i) {
                    if (antGenero.equals(sp_gender.getItemAtPosition(i).toString())) {
                        sp_gender.setSelection(i);
                    }
                }

                antTipoUsuario = tipo_DisplayATexto(thisUser.getDisplayName());

                for (int i = 0; i < sp_userType.getAdapter().getCount(); ++i) {
                    if (antTipoUsuario.equals(sp_userType.getItemAtPosition(i).toString())) {
                        sp_userType.setSelection(i);
                    }
                }

                antFechaNacimiento = yo.getFechaNacimiento();
                Calendar cal = Calendar.getInstance();
                cal.setTime(antFechaNacimiento);
                dp_birthday.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

                try {
                    mStorage.child("images/" + thisUser.getUid() ).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            // generatedFilePath = downloadUri.toString(); /// The string(file link) that you need
                            Picasso.with(UpdateProfile.this).load(uri).into(iv_profilePicture);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });


                } catch (Exception e) {

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
                Toast.makeText(getBaseContext(), "Error lectura user", Toast.LENGTH_SHORT).show();
            }
        });


    }

    //Leer y cambiar datos del usuario
    public void updateUser() {
        myRef = database.getReference(PATH_USERS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MyUser yo = new MyUser();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    MyUser myUser = singleSnapshot.getValue(MyUser.class);
                    if (myUser.getCorreo().equalsIgnoreCase(thisUser.getEmail())) {
                        //Soy yo

                        //Toast.makeText(getBaseContext(),"Se encontro al usuario Yo "+myUser.getNombres(),Toast.LENGTH_SHORT).show();
                        yo = myUser;
                        break;
                    }
                }

                String mensaje = "Se actualizaron los siguientes campos: ";

                String nombres = et_nombres.getText().toString();
                String apellidos = et_apellidos.getText().toString();
                float peso = Float.parseFloat(et_peso.getText().toString());
                float altura = Float.parseFloat(et_altura.getText().toString());
                String ciudad = sp_cities.getSelectedItem().toString();
                String genero = sp_gender.getSelectedItem().toString();
                String tipoUsuario = sp_userType.getSelectedItem().toString();

                //FECHA DE NACIMIENTO
                int dia = dp_birthday.getDayOfMonth();
                int mes = dp_birthday.getMonth();
                int anio = dp_birthday.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(anio, mes, dia);
                Date fechaNacimiento = calendar.getTime();

                Calendar cal = Calendar.getInstance();
                cal.setTime(antFechaNacimiento);
                int antDia = cal.get(Calendar.DAY_OF_MONTH);
                int antMes = cal.get(Calendar.MONTH);
                int antAnio = cal.get(Calendar.YEAR);
                //-----------------------------------------


                if (!nombres.isEmpty() && !nombres.equals(antNombre)) {
                    yo.setNombres(nombres);

                    //Modificar display name
                    UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                    String tipoUsuarioDisplayName = tipoUsuarioDisplay(thisUser.getDisplayName());
                    upcrb.setDisplayName(tipoUsuarioDisplayName + yo.getNombres() + " " + yo.getApellidos());
                    upcrb.setPhotoUri(thisUser.getPhotoUrl());
                    thisUser.updateProfile(upcrb.build());
                    //

                    mensaje += "\nNombre(s) ";
                }

                if (!apellidos.isEmpty() && !apellidos.equals(antApellido)) {
                    yo.setApellidos(apellidos);

                    //Modificar display name
                    UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                    String tipoUsuarioDisplayName = tipoUsuarioDisplay(thisUser.getDisplayName());
                    upcrb.setDisplayName(tipoUsuarioDisplayName + yo.getNombres() + " " + yo.getApellidos());
                    upcrb.setPhotoUri(thisUser.getPhotoUrl());
                    thisUser.updateProfile(upcrb.build());
                    //

                    mensaje += "\nApellidos";
                }

                if (peso != antPeso) {
                    yo.setPeso(peso);
                    mensaje += "\nPeso";
                }

                if (altura != antAltura) {
                    yo.setAltura(altura);
                    mensaje += "\nAltura";
                }

                if (!ciudad.equals(antCiudad)) {
                    yo.setCiudad(ciudad);
                    mensaje += "\nCiudad";
                }

                if (!genero.equals(antGenero)) {
                    yo.setSexo(genero);
                    mensaje += "\nGénero";
                }

                String tipoUsuarioDisplayName = "";
                if (!tipoUsuario.equals(antTipoUsuario)) {
                    //Modificar display name
                    UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                    tipoUsuarioDisplayName = tipo_TextoADisplay(tipoUsuario);
                    String newDisplayName = tipoUsuarioDisplayName + yo.getNombres() + " " + yo.getApellidos();
                    upcrb.setDisplayName(newDisplayName);
                    upcrb.setPhotoUri(thisUser.getPhotoUrl());
                    thisUser.updateProfile(upcrb.build());
                    //
                    mensaje += "\nTipo de usuario";
                }

                if (dia != antDia || mes != antMes || anio != antAnio) {
                    yo.setFechaNacimiento(fechaNacimiento);
                    mensaje += "\nFecha de nacimiento";
                }

                if (cambioImagen) {
                    UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                    upcrb.setDisplayName(thisUser.getDisplayName());
                    upcrb.setPhotoUri(Uri.parse(urlPhoto));
                    thisUser.updateProfile(upcrb.build());

                    //StorageReference ref = mStorage.child("images/" + thisUser.getUid() + ".jpg");
                    StorageReference ref = mStorage.child("images/" + thisUser.getUid() );

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

                    mensaje += "\nFoto de perfil";
                }

                myRef = database.getReference(PATH_USERS + thisUser.getUid());
                myRef.setValue(yo);


                Toast.makeText(UpdateProfile.this, mensaje, Toast.LENGTH_LONG).show();

                while (true) {
                    String codDisplayActual = tipoUsuarioDisplay(thisUser.getDisplayName());
                    if (tipoUsuarioDisplayName.equals(codDisplayActual)) {
                        Intent intent = new Intent(UpdateProfile.this, MainActivity.class);
                        startActivity(intent);

                        break;
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
                Toast.makeText(getBaseContext(), "Error lectura user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //PARA ESCOGER LA FOTO DE PERFIL ----------------------------------------------------------------------------------------

    void camara() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
                Toast.makeText(getBaseContext(), "Necesitamos permisos para poder acceder a su cámara", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);


            }
        } else {
            //Abrir cámara para tomar foto
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    void seleccionar() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getBaseContext(), "Necesitamos permisos para acceder a su galería de imágenes", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_PICKER_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_PICKER_REQUEST);

            }
        } else {
            //Abrir galeria para escoger imagen
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IMAGE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri imageUri = data.getData();

                        urlPhoto = imageUri.toString();

                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imageStream.close();
                        iv_profilePicture.setImageBitmap(selectedImage);

                        cambioImagen = true;

                        Toast.makeText(getBaseContext(), "¡Imágen cargada!", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();

                    Uri imageUri = data.getData();

                    urlPhoto = imageUri.toString();

                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    /*Bitmap imageBitmap = null;
                    try {
                        imageBitmap = handleSamplingAndRotationBitmap(iv_profilePicture.getContext(),imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    iv_profilePicture.setImageBitmap(imageBitmap);

                    /*Target mTarget;
                    mTarget = new Target() {
                        @Override
                        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                            //Do something

                            iv_profilePicture.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    };

                    Picasso.with(this)
                            .load("file:"+urlPhoto)
                            .into(mTarget);*/

                    cambioImagen = true;
                }
                break;

        }

    }
    //--------------------------------------------------------------------------------------------------------------------------

    public String tipo_DisplayATexto(String displayName) {

        if (displayName.length() > 5 && displayName.substring(0, 5).equals("-emp-")) {
            return "Empresa";
        }

        return "Persona";

    }

    public String tipo_TextoADisplay(String tipoUsuario_txt) {

        if (tipoUsuario_txt.equals("Empresa")) {
            return "-emp-";
        }

        return "";

    }

    public String tipoUsuarioDisplay(String displayName) {

        if (displayName.length() > 5 && displayName.substring(0, 5).equals("-emp-")) {
            return "-emp-";
        }

        return "";

    }


    //PARA ROTACIÓN DE IMÁGEN

    /**
     * This method is responsible for solving the rotation issue if exist. Also scale the images to
     * 1024x1024 resolution
     *
     * @param context       The current context
     * @param selectedImage The Image URI
     * @return Bitmap image results
     * @throws IOException
     */
    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);
        return img;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
}
