package com.example.usuario.mybd;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LocalizacionUser extends AppCompatActivity implements OnMapReadyCallback {
    Polyline finalP;
    Boolean modo=true;
    public static final String PATH_UBICATION="ubication/";
    public static final String PATH_historal="historial/";

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    public static final String PATH_USERS="users/";

    @Override public boolean onCreateOptionsMenu(Menu menu){ getMenuInflater().inflate(R.menu.menu, menu); return true; }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemClicked = item.getItemId();

        if(itemClicked == R.id.menuLogOut){
            mAuth.signOut();
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

    TextView usaurio;
    TextView distancia;
    TextView correo;
    TextView foto;
    TextView edad;
    TextView sexo;
    ImageView fotoI;
    TextView ciudad;
    EditText texto;
    LatLng puntoInicio;
    Geocoder mGeocoder;

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

        LatLng origin = mSydney.getPosition();

        LatLng dest = mDestino.getPosition();

        // Getting URL to the Google Directions API
        String url = getUrl(origin, dest);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();
        try {
            finalP.remove();

        }catch (
                Exception e
                ){

        }
        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
    }
    Boolean entra=true;
    FloatingActionButton play;
    Date horainicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacion_user);
        texto=(EditText)findViewById(R.id.texto);
        play=(FloatingActionButton)findViewById(R.id.play);
        play.hide();
        play.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==   MotionEvent.ACTION_DOWN){
                    Toast.makeText(v.getContext(),"Iniciando viaje...", Toast.LENGTH_SHORT);
                    horainicio=new Date();
                    Log.d("hola v ","aaa "+horainicio.toString());
                    play.hide();

                }
                return true;
            }
        });

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map1);
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
                    calularDistancia();
                    // if(entra){
                    Ubicacion u=new Ubicacion();
                    u.setLatitude(latitudLast);
                    u.setLongitude(longitudLast);
                    myRef=database.getReference(PATH_UBICATION+user.getUid());
                    myRef.setValue(u);
                    entra=!entra;
                    //}



                    if(ready){
                        LatLng sydney = new LatLng(latitudLast,longitudLast);

                        mSydney.setPosition(sydney);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                    }
                }
            }
        };
        database= FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        mStorage=storage.getReference();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);


        } else {
            obtenerLocalizacion();
        }
        mGeocoder= new Geocoder(getBaseContext());
        texto.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction()==KeyEvent.ACTION_UP && keyCode==KeyEvent.KEYCODE_ENTER) {
                    String addressString= texto.getText().toString();
                    if (!addressString.isEmpty()) {
                        addressString = addressString.trim();
                        texto.setText(addressString);
                    }
                }
                if(event.getAction()==KeyEvent.ACTION_DOWN && keyCode==KeyEvent.KEYCODE_ENTER){

                    String addressString= texto.getText().toString();
                    if (!addressString.isEmpty()) {
                        addressString=addressString.trim();
                        // texto.setText(addressString);
                        try {
                            List<Address> addresses =
                                    mGeocoder.getFromLocationName(addressString, 2,lowerLeftLatitude, lowerLeftLongitude, upperRightLatitude, upperRigthLongitude);
                            if (addresses != null && !addresses.isEmpty()) { Address addressResult= addresses.get(0);
                                LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                                if (mMap!= null) { //AgregarMarcadoral mapa
                                    mDestino.setPosition(position);
                                    puntoInicio=new LatLng(latitudLast,longitudLast);
                                    calularDistancia(true);

                                    pintarRuta();
                                    play.show();
                                    mDestino.setVisible(true);
                                } } else {
                                Toast.makeText(LocalizacionUser.this, "Direcciónno encontrada", Toast.LENGTH_SHORT).show();}
                        } catch (IOException e) { e.printStackTrace(); } }
                    else {Toast.makeText(LocalizacionUser.this, "La direcciónestavacía", Toast.LENGTH_SHORT).show();}

                }
                return false;
            }
        });
        mAuth = FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        usaurio=(TextView)findViewById(R.id.txtusuario) ;
        distancia=(TextView)findViewById(R.id.txtDistancia) ;
        correo=(TextView) findViewById(R.id.txtCorreo);
        foto=(TextView) findViewById(R.id.txtFoto);
        ciudad=(TextView) findViewById(R.id.txtCiudad);
        edad=(TextView) findViewById(R.id.txtEdad);
        sexo=(TextView) findViewById(R.id.txtSexo);
        fotoI=(ImageView) findViewById(R.id.imageViewProfile);
        if(user!=null){
            usaurio.setText(user.getDisplayName());
            correo.setText(user.getEmail());
            try{
                foto.setText(user.getPhotoUrl().toString());

                // StorageReference ref=mStorage.child("images/"+user.getUid()+".jpg");
                //ref
                // ImageUploadInfo UploadInfo = MainImageUploadInfoList.get(position);

                // holder.imageNameTextView.setText(UploadInfo.getImageName());

                //Loading image from Glide library.
                //  Glide.with(context).load(UploadInfo.getImageURL()).into(holder.imageView);
                // Uri =fd.get


                mStorage.child("images/"+user.getUid()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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


            }catch (Exception e){

            }

            //myRef = database.getReference(PATH_USERS+user.getUid());
//            myRef.
            //mostrar demas atributostry {
            myRef = database.getReference(PATH_USERS);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                    {
                        MyUser myUser = singleSnapshot.getValue(MyUser.class);
                        if(myUser.getCorreo().equalsIgnoreCase(user.getEmail())){
                            ciudad.setText(myUser.getCiudad());
                            sexo.setText(myUser.getSexo());
                            edad.setText(myUser.getEdad()+" años");
                            break;
                        }
                    }
                }
                @Override public void onCancelled(DatabaseError databaseError) {
                    //Log.w(TAG, "error en la consulta", databaseError.toException());
                    Toast.makeText(getBaseContext(),"Error lectura user",Toast.LENGTH_SHORT).show();
                }
            });






        }

    }
    //fin oncreate
    public void calularDistancia(Boolean entrada){
        Double d=distance( latitudLast,longitudLast, mDestino.getPosition().latitude, mDestino.getPosition().longitude);
        distancia.setText("Distancia: "+d + " km");
        kilometros=Float.parseFloat(d+"");
    }

    public void calularDistancia(){
        Double d=distance( latitudLast,longitudLast, mDestino.getPosition().latitude, mDestino.getPosition().longitude);
        distancia.setText("Distancia: "+d + " km");
        if(d<=5 && horainicio != null){
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

            Toast.makeText(getBaseContext(),"Finalizacion de la ruta en "+horas+":"+minutos,Toast.LENGTH_SHORT).show();

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
            ruta.setClima("Soleado");

            myRef=database.getReference(PATH_historal+user.getUid());

            //            myRef.setValue("");
            myRef=database.getReference(PATH_historal+user.getUid()+"/"+key);
            myRef.setValue(ruta);

            horainicio=null;
        }
    }
    float kilometros;
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

                    //  mMap.addMarker(new MarkerOptions()
                    //        .position(new LatLng(myUbication.getLatitude(),myUbication.getLongitude()))
                    //      .title("usuario ..." )
                    //    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bicicleta)));

                } }
            @Override public void onCancelled(DatabaseError databaseError)
            {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
            }
        });
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
        LatLng sydney = new LatLng(4.598156094243696, -74.07604694366455);
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
        mDestino.setVisible(true);
        //mSydney.setTag(0);

        //  mMap.addMarker(mSydney);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
//        Toast.makeText(this,"Cargando marcador",Toast.LENGTH_SHORT).show();
        ready=true;
    }
    private GoogleMap mMap;
    private Marker mSydney;
    private Marker mDestino;
    boolean ready =false;
    public static final double lowerLeftLatitude = 4.49;
    public static final double lowerLeftLongitude= -74.22;
    public static final double upperRightLatitude= 4.86;
    public static final double upperRigthLongitude= -73.969905;


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
        public void limpiar(){
            try {
                finalP.remove();

            }catch (
                    Exception e
                    ){

            }

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

}

// Fetches data from url passed

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

