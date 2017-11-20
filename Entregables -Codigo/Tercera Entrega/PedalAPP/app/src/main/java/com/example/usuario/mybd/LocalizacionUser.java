package com.example.usuario.mybd;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LocalizacionUser extends AppCompatActivity implements OnMapReadyCallback {
    Polyline finalP;
    String destino;
    Boolean modo=true;
    public static final String PATH_UBICATION="ubication/";
    public static final String PATH_historal="historial/";
    public static final String PATH_marcadores="marcadoresempresariales/";
    List<MarcadorEmpresarial> marcadores ;

    String firstDisplayName;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    public static final String PATH_USERS="users/";

    @Override
    protected void onResume() {
        super.onResume();
        /*t=new Time2();
        t.execute();
        t1=t;*/
        startLocationUpdates();
    }

    @Override
    protected void onPause() {

        Ubicacion u = new Ubicacion();
        u.setLatitude(latitudLast);
        u.setLongitude(longitudLast);
        u.setActive(false);
        u.setUsuario(user.getDisplayName());
        myRef = database.getReference(PATH_UBICATION + user.getUid());
        myRef.setValue(u);

        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        //t.cancel(true);
        t1.cancel(true);
        Log.d("onPreExecute------->","fin thread...");

        super.onPause();
    }


    @Override public boolean onCreateOptionsMenu(Menu menu){ getMenuInflater().inflate(R.menu.menu, menu); return true; }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemClicked = item.getItemId();

        if(itemClicked == R.id.menuLogOut){
            Ubicacion u = new Ubicacion();
            u.setLatitude(latitudLast);
            u.setLongitude(longitudLast);
            u.setActive(false);
            u.setUsuario(user.getDisplayName());
            myRef = database.getReference(PATH_UBICATION + user.getUid());
            myRef.setValue(u);

            mAuth.signOut();
            t1.cancel(true);
            Intent intent = new Intent(LocalizacionUser.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); startActivity(intent);

        }else if (itemClicked == R.id.menuAddFriend){
            Intent intent = new Intent(LocalizacionUser.this, AddFriend.class);
            startActivity(intent);

        }
        else if (itemClicked == R.id.menuList){
            Intent intent = new Intent(LocalizacionUser.this, FriendList.class);
            startActivity(intent);

        }

        else if (itemClicked == R.id.menuRedesSociales){
            Intent intent = new Intent(LocalizacionUser.this, AddSocialNetwork.class);
            startActivity(intent);

        }
        else if (itemClicked == R.id.menuHistorial){
            Intent intent = new Intent(LocalizacionUser.this, ListHistorial.class);
            startActivity(intent);

        }

        else if (itemClicked == R.id.menuActualizarPerfil){
            //Abrir actividad para configuración etc
            //Abrir actividad para configuración etc
            Intent intent = new Intent(LocalizacionUser.this, UpdateProfile.class);
            startActivity(intent);
        }
        else if (itemClicked == R.id.menuRutasProgramdas){
            //Abrir actividad para configuración etc
            //Abrir actividad para configuración etc
            Intent intent = new Intent(LocalizacionUser.this, ListRutasProgramadas.class);
            startActivity(intent);
        }

        else if (itemClicked == R.id.menuActualizarLogin){
            //Abrir actividad para configuración etc
            //Abrir actividad para configuración etc
            Intent intent = new Intent(LocalizacionUser.this, UpdateLogin.class);
            startActivity(intent);
        }

        else if (itemClicked == R.id.menuList){
            //Abrir actividad para configuración etc
        }

        else if (itemClicked == R.id.menuNocturno){
            //Abrir actividad para configuración etc
            if(modo){
                mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(this,R.raw.dia)
                );
                mSydney.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bicicleta2));

            }else{
                mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(this,R.raw.noche)
                );
                mSydney.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bicicleta));

            }
            modo=!modo;
        } return super.onOptionsItemSelected(item);
    }

    static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    static final int MY_PERMISSIONS_WRITE= 2;
    static final int REQUEST_CHECK_SETTINGS=3;
    private FusedLocationProviderClient mFusedLocationClient;
    public final static double RADIUS_OF_EARTH_KM = 6371;

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    LinearLayout linearL;
    double longitudLast;
    double latitudLast;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private FirebaseStorage storage;

    TextView usuario;
    TextView distancia;
    TextView correo;
    TextView ciudad;
    TextView edad;
    TextView sexo;
    TextView altura;
    TextView peso;
    ImageView fotoI;

    EditText texto;
    LatLng puntoInicio;
    Geocoder mGeocoder;
    static    Time2 t1;
    private List<RutaProgramada> rutas;

    Time2 t;

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    void pintarRuta(){
        limpiar();
        t1.cancel(true);
        Log.d("--->","Detenido");
        // t1.cancel(true);
        play.hide();
        LatLng origin = puntoInicio;

        LatLng dest = mDestino.getPosition();

        // Getting URL to the Google Directions API
        String url = getUrl(origin, dest);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();
        FetchUrl.execute(url);
        try {
            finalP.remove();

        }catch (
                Exception e
                ){

        }
        // Start downloading json data from Google Directions API
    }
    Boolean entra=true;
    FloatingActionButton play;
    FloatingActionButton stop;
    Date horainicio;
    ShareDialog shareDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.setApplicationId("551011411917634");
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_localizacion_user);

        shareDialog=new ShareDialog(this);
        firstDisplayName = getIntent().getStringExtra("displayName");

        marcadores = new ArrayList<MarcadorEmpresarial>();
        user=FirebaseAuth.getInstance().getCurrentUser();

        String displayName2;
        try {
            user.getDisplayName().equals(null);

            displayName2 = user.getDisplayName();
        } catch (Exception e) {
            displayName2 = this.firstDisplayName;
        }

        if(displayName2.length() > 5 &&  displayName2.substring(0,5).equals("-emp-")){

            Intent i= new Intent(LocalizacionUser.this, Usuario_Empresarial.class);
            startActivity(i);

        }else {

            rutas = new ArrayList<RutaProgramada>();
            loadProgrmadas();

            texto = (EditText) findViewById(R.id.texto);
            play = (FloatingActionButton) findViewById(R.id.play);
            play.hide();
            stop = (FloatingActionButton) findViewById(R.id.stop);
            stop.hide();

            play.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Toast.makeText(v.getContext(), "Iniciando viaje...", Toast.LENGTH_SHORT);
                        horainicio = new Date();
                        finalP.setColor(Color.GREEN);
                        Log.d("hola v ", "aaa " + horainicio.toString());
                        play.hide();
                        stop.show();
                    }
                    return true;
                }
            });
            stop.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        horainicio = null;
                        finalP.setColor(Color.RED);
                        stop.hide();
                        play.show();

                    }
                    return true;
                }
            });
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map1);
            mapFragment.getMapAsync(this);

            mLocationRequest = createLocationRequest();
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);// **


            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    Log.i("LOCATION", "Location	update	in	the	callback:	" + location);
                    if (location != null) {


                        longitudLast = location.getLongitude();
                        latitudLast = location.getLatitude();

                        //4.598156094243696
                        //-74.07604694366455 //long
                        //lat // long
//
                        //  distancia.setText("Distancia: "+distance( latitudLast,longitudLast
                        if(horainicio!=null)calularDistancia();
                        //else calularDistancia();
                        // if(entra){
                        Ubicacion u = new Ubicacion();
                        u.setLatitude(latitudLast);
                        u.setLongitude(longitudLast);
                        u.setActive(true);
                        u.setUsuario(user.getDisplayName());
                        myRef = database.getReference(PATH_UBICATION + user.getUid());
                        myRef.setValue(u);
                        entra = !entra;
                        //}


                        if (ready) {
                            LatLng sydney = new LatLng(latitudLast, longitudLast);

                            mSydney.setPosition(sydney);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                        }
                    }
                }
            };
            database = FirebaseDatabase.getInstance();
            storage = FirebaseStorage.getInstance();
            mStorage = storage.getReference();
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                obtenerLocalizacion();
            }
            mGeocoder = new Geocoder(getBaseContext());
            texto.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                        String addressString = texto.getText().toString();
                        if (!addressString.isEmpty()) {
                            addressString = addressString.trim();
                            texto.setText(addressString);
                            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                            inputMethodManager.hideSoftInputFromWindow(texto.getWindowToken(), 0);
                        }
                    }
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                        String addressString = texto.getText().toString();
                        if (!addressString.isEmpty()) {
                            addressString = addressString.trim();
                            // texto.setText(addressString);
                            try {
                                List<Address> addresses =
                                        mGeocoder.getFromLocationName(addressString, 2, lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude, upperRigthLongitude);
                                if (addresses != null && !addresses.isEmpty()) {
                                    Address addressResult = addresses.get(0);
                                    LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                                    if (mMap != null) { //AgregarMarcadoral mapa
                                        destino=addressString;
                                        mDestino.setPosition(position);
                                        puntoInicio = new LatLng(latitudLast, longitudLast);
                                        mInicio.setPosition(puntoInicio );
                                        mInicio.setVisible(true);

                                        calularDistancia(true);

                                        pintarRuta();
                                        //  play.show();
                                        mDestino.setVisible(true);
                                        Toast.makeText(LocalizacionUser.this, "Destino encotrado, incia tu recorrido con el botón '>' ", Toast.LENGTH_SHORT);
                                    }
                                } else {
                                    Toast.makeText(LocalizacionUser.this, "Direcciónno encontrada", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(LocalizacionUser.this, "La direcciónestavacía", Toast.LENGTH_SHORT).show();
                        }

                    }
                    return false;
                }
            });
            mAuth = FirebaseAuth.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser();
            usuario = (TextView) findViewById(R.id.txtusuario);
            distancia = (TextView) findViewById(R.id.txtDistancia);
            correo = (TextView) findViewById(R.id.txtCorreo);
            ciudad = (TextView) findViewById(R.id.txtCiudad);
            edad = (TextView) findViewById(R.id.txtEdad);
            sexo = (TextView) findViewById(R.id.txtSexo);
            altura = (TextView) findViewById(R.id.txtAltura);
            peso = (TextView) findViewById(R.id.txtPeso);

            fotoI = (ImageView) findViewById(R.id.imageViewProfile);
            if (user != null) {

                String displayName;
                try {
                    user.getDisplayName().equals(null);

                    displayName = user.getDisplayName();
                } catch (Exception e) {
                    displayName = this.firstDisplayName;
                }

                if (displayName.length() > 5 && displayName.substring(0, 5).equals("-emp-")) {
                    usuario.setText("Bienvenido " + displayName.substring(5));
                } else {
                    usuario.setText("Bienvenido " + displayName);
                }


                correo.setText(user.getEmail());


                //myRef = database.getReference(PATH_USERS+user.getUid());
//            myRef.
                //mostrar demas atributostry {
                myRef = database.getReference(PATH_USERS);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            MyUser myUser = singleSnapshot.getValue(MyUser.class);
                            if (myUser.getCorreo().equalsIgnoreCase(user.getEmail())) {
                                ciudad.setText(myUser.getCiudad());
                                sexo.setText(myUser.getSexo());
                                edad.setText(myUser.getEdad() + " años");
                                altura.setText(myUser.getAltura() + "");
                                peso.setText(myUser.getPeso() + "");

                                //CARGAR FOTO DE PERFIL
                                try {

                                    // StorageReference ref=mStorage.child("images/"+user.getUid()+".jpg");
                                    //ref
                                    // ImageUploadInfo UploadInfo = MainImageUploadInfoList.get(position);

                                    // holder.imageNameTextView.setText(UploadInfo.getImageName());

                                    //Loading image from Glide library.
                                    //  Glide.with(context).load(UploadInfo.getImageURL()).into(holder.imageView);
                                    // Uri =fd.get


                                    mStorage.child("images/" + user.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Got the download URL for 'users/me/profile.png'
                                            // generatedFilePath = downloadUri.toString(); /// The string(file link) that you need
                                            Picasso.with(LocalizacionUser.this).load(uri).into(fotoI);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                        }
                                    });


                                } catch (Exception e) {
                                    Toast.makeText(getBaseContext(), "No se pudo cargar la imágen", Toast.LENGTH_SHORT).show();
                                }

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
            try {
                Bundle bundle = getIntent().getBundleExtra("bundle");
                RutaProgramada objeto = (RutaProgramada) bundle.getSerializable("ruta");
                int position=bundle.getInt("position");
                Toast.makeText(this,"Ruta "+position,Toast.LENGTH_SHORT).show();
                Existruta=true;
                rutaProgramada=objeto;

                List<Address> addresses =
                mGeocoder.getFromLocation(mDestino.getPosition().latitude ,mDestino.getPosition().longitude,2 );
                if (addresses != null && !addresses.isEmpty()) {
                    Address addressResult = addresses.get(0);
                    destino=addressResult.getFeatureName();

                }}catch (Exception e){
                Log.d("----->","Error "+e);
            }
        }
    }
    private void loadProgrmadas() {

        database= FirebaseDatabase.getInstance();
        myRef=database.getReference("rutasprogramadas/");

        myRef.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rutas= new ArrayList<RutaProgramada>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    RutaProgramada myRuta = singleSnapshot.getValue(RutaProgramada.class);
                    rutas.add(myRuta);
                }

                //  Toast.makeText(getBaseContext(),""+rutas.size(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    Boolean Existruta=false;
    RutaProgramada rutaProgramada;
    Map<LatLng, Marker> mapMarcadores = new HashMap<LatLng, Marker>();
    private void loadEMarkers() {

        database= FirebaseDatabase.getInstance();
        myRef = database.getReference(PATH_marcadores);
        myRef. addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                marcadores = new ArrayList<MarcadorEmpresarial>();
                // mMap.clear();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    marcadores.add(child.getValue(MarcadorEmpresarial.class));
                }

                addMarkers(marcadores);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log.w(TAG, "error en la consulta", databaseError.toException());
            }
        });
    }

    private void addMarkers(List<MarcadorEmpresarial> marcadores) {
        Iterator it = mapMarcadores.keySet().iterator();
        while(it.hasNext()){
            LatLng key =(LatLng) it.next();
            mapMarcadores.get(key).setVisible(false);
        }
        int i=0;

        for (MarcadorEmpresarial me : marcadores) {


            LatLng pos = new LatLng(me.getLatitude(), me.getLongitude());
            if (mapMarcadores.containsKey(pos)){
                mapMarcadores.get(pos).setVisible(true);
            }else {
                mapMarcadores.put(pos, mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.point2)).title(me.getTitle())
                        .snippet(me.getInfo())));
            }i++;
        }

/*
        mSydney = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitudLast,longitudLast))
                .title("Posicion")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bicicleta2)));


        mDestino = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mDestino.getPosition().latitude,mDestino.getPosition().longitude))
                .title("Destino")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bandera)));
*/
    }
    //fin oncreate
    public void calularDistancia(Boolean entrada){
        Double d=distance( puntoInicio.latitude, puntoInicio.longitude,mDestino.getPosition().latitude, mDestino.getPosition().longitude);
        distancia.setText("Distancia: "+d + " km");
        kilometros=Float.parseFloat(d+"");

    }
    void  stop(){
        horainicio=null;
        //limpiar();
    }
    public void calularDistancia(){
        Double d=distance( latitudLast,longitudLast, mDestino.getPosition().latitude, mDestino.getPosition().longitude);
        distancia.setText("Distancia: "+d + " km");
        // if(d<=0.03 && horainicio != null){
        if(d<=0.03 && horainicio != null){ //rango_fin
            Date horafin=new Date();
            Log.d("hola v iini n","aaa "+horainicio.toString());
            Log.d("hola v fin n","aaa "+horafin.toString());

            /**Creamos una instancia de la clase calendar*/
            Calendar calFechaInicial=Calendar.getInstance();
            Calendar calFechaFinal=Calendar.getInstance();
            /**Le pasamos el objeto Date al metodo set time*/
            calFechaInicial.setTime(horainicio);
            calFechaFinal.setTime(horafin);
            //long segundos=cantidadTotalSegundos(calFechaInicial,calFechaFinal);
            long horas=diferenciaHoras(calFechaInicial,
                    calFechaFinal)+diferenciaHoras(calFechaInicial,calFechaFinal);
            long minutos=diferenciaMinutos(calFechaInicial,calFechaFinal);
            long s=cantidadTotalSegundos(calFechaInicial,calFechaFinal);

            Toast.makeText(getBaseContext(),"Finalización recorrido \n Tiempo empleado: "+horas+" horas, "+minutos+" minutos "+s+" segundos",Toast.LENGTH_LONG).show();
            stop.hide();
            mInicio.setVisible(false);

            String key = myRef.push().getKey();
            Ruta ruta =new Ruta();
            ruta.setFecha(new Date());
            ruta.setHoras((int) horas);
            ruta.setKilometros(kilometros);
            ruta.setMinutos((int)minutos);
            ruta.setLongitudInicial(puntoInicio.longitude);
            ruta.setLatitudIncial(puntoInicio.latitude);
            ruta.setLongitudFinal(mDestino.getPosition().longitude);
            ruta.setLatitudFinal(mDestino.getPosition().latitude);
 /*           ruta.setClima("Soleado");

            myRef=database.getReference(PATH_historal+user.getUid());

            //            myRef.setValue("");
            myRef=database.getReference(PATH_historal+user.getUid()+"/"+key);
            myRef.setValue(ruta);
*/
            rutaR=ruta;
            Weather w= new Weather();
            String url="https://api.openweathermap.org/data/2.5/weather?lat="+puntoInicio.latitude+"&lon="+puntoInicio.longitude+"&APPID=e5ac0ff5c4d549e262ddcfaeb8133652";

            // Start downloading json data from wheater API
            w.execute(url);

            horainicio=null;
            mDestino.setVisible(false);
            try {
                finalP.remove();

            }catch (
                    Exception e
                    ){

            }
        }
    }
    float kilometros;
    Ruta rutaR;
    /*Metodo que calcula la diferencia de las horas que han pasado entre dos fechas en java
*/
    public static long diferenciaHorasDias(Calendar fechaInicial , Calendar fechaFinal){
        //Milisegundos al día
        long diferenciaHoras=0;
        //Restamos a la fecha final la fecha inicial y lo dividimos entre el numero de milisegundos al dia
        diferenciaHoras=(fechaFinal.getTimeInMillis()-fechaInicial.getTimeInMillis());//milisegundos_dia;
        if(diferenciaHoras>0){
            // Lo Multiplicaos por 24 por que estamos utilizando el formato militar
            diferenciaHoras*=24;
        }
        return diferenciaHoras;
    }
    /*Metodo que calcula la diferencia de los minutos entre dos fechas
   */
    public static long diferenciaMinutos(Calendar fechaInicial ,Calendar fechaFinal){

        long diferenciaHoras=0;
        diferenciaHoras=(fechaFinal.get(Calendar.MINUTE)-fechaInicial.get(Calendar.MINUTE));
        return diferenciaHoras;
    }
    /*Metodo que devuelve el Numero total de minutos que hay entre las dos Fechas */
    public static long cantidadTotalMinutos(Calendar fechaInicial ,Calendar fechaFinal){

        long totalMinutos=0;
        totalMinutos=((fechaFinal.getTimeInMillis()-fechaInicial.getTimeInMillis())/1000/60);
        return totalMinutos;
    }
    /*Metodo que devuelve el Numero total de horas que hay entre las dos Fechas */
    public static long cantidadTotalHoras(Calendar fechaInicial ,Calendar fechaFinal){

        long totalMinutos=0;
        totalMinutos=((fechaFinal.getTimeInMillis()-fechaInicial.getTimeInMillis())/1000/60/60);
        return totalMinutos;
    }
    /*Metodo que devuelve el Numero total de Segundos que hay entre las dos Fechas */
    public static long cantidadTotalSegundos(Calendar fechaInicial ,Calendar fechaFinal){

        long totalMinutos=0;
        totalMinutos=((fechaFinal.getTimeInMillis()-fechaInicial.getTimeInMillis())/1000);
        return totalMinutos;
    }
    /*Metodo que calcula la diferencia de las horas entre dos fechas*/
    public static long diferenciaHoras(Calendar fechaInicial ,Calendar fechaFinal){
        long diferenciaHoras=0;
        diferenciaHoras=(fechaFinal.get(Calendar.HOUR_OF_DAY)-fechaInicial.get(Calendar.HOUR_OF_DAY));

        return diferenciaHoras;
    }
    Map<String, Marker> mapUbicaciones = new HashMap<String, Marker>();

    public void loadUsers() {
        myRef = database.getReference(PATH_UBICATION);
        myRef. addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    Ubicacion myUbication = singleSnapshot.getValue(Ubicacion.class);

                    Log.d("Usuario   ", "Encontró usuario: "+myUbication.toString() );//+ user.getName());
                    //String name = myUser.getName(); int age = myUser.getAge();
                    // Toast.makeText(MapHomeActivity.this, name + ":" + age, Toast.LENGTH_SHORT).show();
                    if(!user.getDisplayName().equals(myUbication.getUsuario())) {
                        if(!mapUbicaciones.containsKey(myUbication.getUsuario())) {
                            mapUbicaciones.put(myUbication.getUsuario(),
                                    mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(myUbication.getLatitude(), myUbication.getLongitude()))
                                            .title(myUbication.getUsuario())
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.near))));
                            mapUbicaciones.get(myUbication.getUsuario()).setVisible(myUbication.getActive());

                        }
                        else{
                            mapUbicaciones.get(myUbication.getUsuario()).setPosition(new LatLng(myUbication.getLatitude(),myUbication.getLongitude()));
                            mapUbicaciones.get(myUbication.getUsuario()).setVisible(myUbication.getActive());

                        }
                    }
                } }
            @Override public void onCancelled(DatabaseError databaseError)
            {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
            }
        });



        List<Taller> lista=new ArrayList<>();
        lista.add(new Taller("Welcome Expertos en ciclismo",4.678918,-74.044987 ));
        lista.add(new Taller("GO Bikes",4.689970,-74.046056 ));
        lista.add(new Taller("El Tomacorriente 79",4.663787,-74.054316));
        lista.add(new Taller("Bicicletería Michael",4.674457,-74.047632));

        for (Taller t:lista){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(t.getLatitude(),t.getLongitud()))
                    .title(t.getNombre() )
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.wrench)));

        }

    }

    private void obtenerLocalizacion() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();    //Todaslas condiciones para	recibir localizaciones
            }
        });

        task.addOnFailureListener(this,	new	OnFailureListener()	{
            @Override public	void	onFailure(@NonNull Exception	e)	{
                int  statusCode =	((ApiException)	e).getStatusCode();
                switch	(statusCode)
                { case	CommonStatusCodes.RESOLUTION_REQUIRED: //	Location	settings	are	not	satisfied,	but	this	can	be	fixed	by	showing	the	user	a	dialog.
                    try	{//	Show	the	dialog	by	calling	startResolutionForResult(),	and	check	the	result	in	onActivityResult().
                        ResolvableApiException resolvable	=	(ResolvableApiException)	e;
                        resolvable.startResolutionForResult(LocalizacionUser.this, REQUEST_CHECK_SETTINGS);

                        // mLocationCallback.
                        startLocationUpdates();
                    }
                    catch	(IntentSender.SendIntentException sendEx)	{ //	Ignore	the	error.
                    }
                    break;
                    case	LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: //	Location	settings	are	not	satisfied.	No	way	to	fix	the	settings	so	we	won't	show	the	dialog.
                        break;
                }
            }
        });
    }



    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {                //Verificaciónde	permiso!!
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //concedido
                        obtenerLocalizacion();
                    }
                }
            }
            case MY_PERMISSIONS_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {

                        //concedido
                        writeJSONObject();
                        TextView txt;
                        txt = new TextView(getBaseContext());
                        Date d = new Date();
                        txt.setText("Lat: " + latitudLast + " Long:" + longitudLast + " " + d);
                        linearL.addView(txt);;
                    }
                }

            }

        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);    //tasa de	refresco en	milisegundos
        mLocationRequest.setFastestInterval(5000);    //máxima tasa de	refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result * 100.0) / 100.0;
    }

    //List<MyLocation> localizaciones=new ArrayList<>();
    JSONArray localizaciones=new JSONArray();
    private void writeJSONObject() {
        MyLocation myLocation=new	MyLocation();
        myLocation.setFecha(new	Date(System.currentTimeMillis()));
        myLocation.setLatitud(latitudLast);
        myLocation.setLongitud(longitudLast);
        localizaciones.put(myLocation.toJSON());
        Writer output	=	null;
        String	filename=	"locations.json";
        try
        {
            File file	=	new	File(getBaseContext().getExternalFilesDir(null),	filename);
            //Log.i(“LOCATION",	"Ubicacion de	archivo:	"+file);" +
            output	=	new BufferedWriter(new FileWriter(file)); output.write(localizaciones.toString());
            output.close();
            Toast.makeText(getApplicationContext(),	"Location	saved",	Toast.LENGTH_LONG).show();
        }
        catch	(Exception	e)	{
            Toast.makeText(getBaseContext(),	e.getMessage(),	Toast.LENGTH_LONG).show(); }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //4.598156094243696
        //-74.07604694366455
        mInicio=mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0,0))
                .title("Inicio" )
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.iniciomarker)));
        mInicio.setVisible(false);

        LatLng sydney = new LatLng(latitudLast, longitudLast);
        Date d=new Date();
        DateFormat format=new SimpleDateFormat("HH");
        int h=Integer.parseInt(format.format(d).toString());
        if(h>=18 || h <6){
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this,R.raw.noche)
            );
            mSydney=mMap.addMarker(new MarkerOptions()
                    .position(sydney)
                    .title("Posicion" )
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bicicleta)));
            modo=true;
        }else
        {
            modo=false;
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this,R.raw.dia)
            );
            mSydney=mMap.addMarker(new MarkerOptions()
                    .position(sydney)
                    .title("Posicion" )
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bicicleta2)));

        }


        mDestino=mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Destino" )
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bandera)));
        mDestino.setVisible(false);

        //mSydney.setTag(0);

        //  mMap.addMarker(mSydney);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
//        Toast.makeText(this,"Cargando marcador",Toast.LENGTH_SHORT).show();
        ready=true;

        loadEMarkers();


        loadUsers();
        t = new Time2 ();
        if(Existruta){
            mDestino.setPosition(new LatLng(rutaProgramada.getR().getLatitudFinal(),rutaProgramada.getR().getLongitudFinal()));
            puntoInicio = new LatLng(rutaProgramada.getR().getLatitudIncial(), rutaProgramada.getR().getLongitudInicial());
            mInicio.setPosition(puntoInicio );
            mInicio.setVisible(true);
            calularDistancia(true);
            mDestino.setVisible(true);
            //   play.show();
            pintarRuta();

        }else{
            t.execute();
            t1=t;
        }
    }
    private GoogleMap mMap;
    private Marker mSydney;
    private Marker mDestino;

    private Marker mInicio;
    boolean ready =false;
    public static final double lowerLeftLatitude = 4.49;
    public static final double lowerLeftLongitude= -74.22;
    public static final double upperRightLatitude= 4.86;
    public static final double upperRigthLongitude= -73.969905;

    class Weather extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;

        }

        @Override
        protected void onPostExecute(String s) {
            rutaR.setClima(s.substring(s.indexOf("description")+14,s.indexOf("icon")-3));
            myRef=database.getReference(PATH_historal+user.getUid());
            String key = myRef.push().getKey();
            myRef=database.getReference(PATH_historal+user.getUid()+"/"+key);
            myRef.setValue(rutaR);

            //share in fb
            Log.d("aqui facebbok","Entra o no ");
            // try {
            if(isLoggedIn()) {
                Log.d("aqui facebbok","pasa ");
                LoginManager lm = LoginManager.getInstance();

                if(!ShareDialog.canShow(ShareLinkContent.class))
                    lm.logInWithReadPermissions((Activity) getBaseContext(), Arrays.asList("public_profile"));
        /*    SharePhoto sharePhoto1 = new SharePhoto.Builder()
                    .setBitmap( BitmapFactory.decodeResource(getResources(), R.drawable.inicio))
                    .setCaption("My fist post from pedalAPP")
                    .           build();
            ShareContent content = new ShareMediaContent.Builder()
                    .addMedium(sharePhoto1)
                    .build();
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);*/

                SimpleDateFormat formatFecha=new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat formatHora=new SimpleDateFormat("hh:mm:ss");
                String h= rutaR.getHoras() < 10  ? "0"+rutaR.getHoras()+"" : rutaR.getHoras()+"";
                String m= rutaR.getMinutos() < 10  ? "0"+rutaR.getMinutos()+"" : rutaR.getMinutos()+"";


                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://raw.githubusercontent.com/PUJCompMovL1730/PedalAPP/master/Dise%C3%B1o%20de%20la%20aplicaci%C3%B3n/PedalAPP.png"))
                        .setQuote("Ruta finalizada "
                                        + "\nDestino: "+destino+
                                "\n" +
                                "Distancia: "+rutaR.getKilometros()
                               + "\nDuracion: "+h+":"+m
                                + "\nWeather: "+rutaR.getClima()
                                + "\nFecha: "+formatHora.format(rutaR.getFecha())
                                + "\nHora: "+formatHora.format(rutaR.getFecha())


                        )
//                        .setPlaceId(destino)
                        .build();
                shareDialog.show(content);

                //if(ShareDialog.canShow(ShareLinkContent.class)){
            /*ShareLinkContent linkContent=new ShareLinkContent.Builder()

                                .setContentTitle("Ruta Finalizada")
                                .setContentDescription("Duracion: "+ rutaR.getHoras()+":"+rutaR.getHoras() +"\n"+
                                        "Clima :"+rutaR.getClima())
                                .build();*/
                //}else{
                //  Toast.makeText(getBaseContext(),"Sin permisos ",Toast.LENGTH_SHORT).show();

//                }else{
                //                  Toast.makeText(getBaseContext(),"Sin session",Toast.LENGTH_SHORT).show();

            }
            //           } catch (Exception e){
            //             Toast.makeText(getBaseContext(),"error fb",Toast.LENGTH_SHORT).show();
            //       }


            //  callbackManager = CallbackManager.Factory.create();
        }
        public boolean isLoggedIn() {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            return accessToken != null;
        }
    }
    // CallbackManager callbackManager;


    class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data

            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }



        PolylineOptions lineOptions = null;
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            if(lineOptions!=null){
                lineOptions=null;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                finalP=mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
            play.show();
            t = new Time2 ();
            t.execute();
            t1=t;
        }
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public void eliminarVencidos ()
    {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void persistir() {

        myRef=database.getReference("marcadoresempresariales");
        myRef.setValue(marcadores);
    }
    private void persistir2() {

        myRef=database.getReference("rutasprogramadas/");
        myRef.setValue(rutas);
    }

    public void ejecutar()
    {
        List<MarcadorEmpresarial> vencidos = new ArrayList<MarcadorEmpresarial>();
        List<RutaProgramada> vencidos2 = new ArrayList<RutaProgramada>();


        for (MarcadorEmpresarial me :marcadores)
        {
            Date actual = new Date();
            if(actual.after(me.getDeadline())) // si se vencio
            {
                vencidos.add(me);
            }
        }

        for (RutaProgramada rp: rutas)
        {
            Date actual = new Date();
            Date deadline = rp.getR().getFecha();
            Calendar cal = Calendar.getInstance();
            cal.setTime(deadline);
            cal.add(Calendar.YEAR, -1900);
            deadline = cal.getTime();


            if(actual.after(deadline)){

                Toast.makeText(getBaseContext(), actual + " "+ deadline, Toast.LENGTH_LONG).show();
                vencidos2.add(rp);
            }

        }

        //marcadores.removeAll(vencidos); // saco todos los vencidos
        rutas.removeAll(vencidos2);
        persistir();
        persistir2();

        t= new Time2();
        t.execute();
        t1=t;
    }

    public class Time2 extends AsyncTask<Void, Integer, Boolean>
    {


        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d("onPreExecute------->","ejecuantdo actualizacion");

            for (int i = 1 ; i<3;i++)
            {

                eliminarVencidos();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) { // resultado: eliminar de la db los marcadores vencidos


            ejecutar();


        }
    }
    public void limpiar(){
        try {
            finalP.remove();

        }catch (
                Exception e
                ){

        }

    }
}

// Fetches data from url passed


class Taller {

    String  nombre;
    double longitud;
    double latitude;
    public Taller(){
    }

    public Taller(String nombre, double latitude,double longitud) {
        this.nombre = nombre;
        this.longitud = longitud;
        this.latitude = latitude;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
class MyLocation{

    Date fecha;
    double longitud;
    double latitud;

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }


    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public	JSONObject toJSON ()
    {
        JSONObject obj =	new	JSONObject();
        try	{
            obj.put("latitud",	getLatitud());
            obj.put("longitud",	getLongitud());
            obj.put("date",	getFecha());
        }	catch	(JSONException e)	{
            e.printStackTrace();
        }
        return	obj;
    }
}

