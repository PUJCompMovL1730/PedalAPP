package com.example.usuario.mybd;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Usuario_Empresarial extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng pointtoAdd;
    private ArrayList<MarcadorEmpresarial> lstLatLngs;
    private EditText mAddress;
    private ImageButton ib;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    public static final double lowerLeftLatitude = 4.480424;
    public static final double lowerLeftLongitude= -74.284058;
    public static final double upperRightLatitude= 4.755559;
    public static final double upperRigthLongitude= -73.896790;

    Time t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario__empresarial);

        lstLatLngs = new ArrayList<MarcadorEmpresarial>();
        mAddress = (EditText) findViewById(R.id.tvgeocoder);
        ib = (ImageButton) findViewById(R.id.imageButton2);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_empresarial, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemClicked = item.getItemId();

        if(itemClicked == R.id.menuLogoutEmp){
            mAuth.signOut();
            t.cancel(true);
            Intent intent = new Intent(Usuario_Empresarial.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else if (itemClicked == R.id.menuProgRoute){

            Intent intent = new Intent(Usuario_Empresarial.this, EProgramarRuta.class);
            startActivity(intent);

        }else if(itemClicked == R.id.menuActualizarLoginEmp){
            Intent intent = new Intent(Usuario_Empresarial.this, UpdateLogin.class);
            startActivity(intent);
        }else if(itemClicked == R.id.menuActualizarPerfilEmp){
            Intent intent = new Intent(Usuario_Empresarial.this, UpdateProfile.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        loadEMarkers();
        t= new Time();
        t.execute();



        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpGeoCoder();
            }
        });

        mMap = googleMap;
        LatLng bogota = new LatLng(4.598156094243696, -74.07604694366455);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));



        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                pointtoAdd = point;
                showDialogTitle();

            }
        });



    }

    private void setUpGeoCoder() {

        Geocoder mGeocoder = new Geocoder(getBaseContext());
        String addressString = mAddress.getText().toString();
        if (!addressString.isEmpty()) {
            try {
                List<Address> addresses = mGeocoder.getFromLocationName(
                        addressString, 2,
                        lowerLeftLatitude,
                        lowerLeftLongitude,
                        upperRightLatitude,
                        upperRigthLongitude);
                if (addresses != null && !addresses.isEmpty()) {
                    Address addressResult = addresses.get(0);
                    LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                    if (mMap != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                    }
                } else {Toast.makeText(Usuario_Empresarial.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();}
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {Toast.makeText(Usuario_Empresarial.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();}

    }

    private void showDialogTitle() {

        final String POPUP_MARKER_TITLE="Nuevo Marcador";
        final String POPUP_MARKER_TEXT="Porfavor llene los datos";
        final String Title_HINT="Titulo";
        final String Info_HINT="Información";

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(POPUP_MARKER_TITLE);
        alert.setMessage(POPUP_MARKER_TEXT);

        // Set an EditText view to get user input
        final EditText titulo = new EditText(this);
        titulo.setHint(Title_HINT);
        final EditText info = new EditText(this);
        info.setHint(Info_HINT);
        String[] types = {"1 dia", "1 semana", "1 mes"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        types);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);

        final Spinner duracion = new Spinner(this);
        duracion.setAdapter(spinnerArrayAdapter);

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(titulo);
        layout.addView(info);
        layout.addView(duracion);
        alert.setView(layout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {


                Date deadline = calcdeadline(duracion.getSelectedItemPosition());
                String tit = titulo.getText().toString();
                String inf = info.getText().toString();
                Date d = new Date();
                Calendar date = Calendar.getInstance();
                long t= date.getTimeInMillis();
                Date afterAddingTenMins=new Date(t + (60000)); // para probar

                if(tit==null)
                    tit="";
                if(inf==null)
                    inf="";
                MarcadorEmpresarial me =new MarcadorEmpresarial(pointtoAdd.longitude,pointtoAdd.latitude,deadline,tit,inf);
                lstLatLngs.add(me);
                mMap.addMarker(new MarkerOptions()
                        .position(pointtoAdd)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.point2)).title(tit)
                        .snippet(inf))
                ;

                SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");

                Toast.makeText(getBaseContext(),"El marcador vencerá el:" + sdf.format(deadline),Toast.LENGTH_SHORT).show();

                persistir();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(getBaseContext(),"Nuevo Marcador Cancelado",Toast.LENGTH_SHORT).show();
            }
        });

        alert.show();
    }

    private void persistir() {

        myRef=database.getReference("marcadoresempresariales");
        myRef.setValue(lstLatLngs);
    }

    private Date calcdeadline(int s) {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        if(s==0){ // 1 dia


            c.add(Calendar.DATE, 1);
            dt = c.getTime();

        }
        if(s==1){ // 1 semana

            c.add(Calendar.DATE, 7);
            dt = c.getTime();
        }
        if(s==2){ // 1 mes
            c.add(Calendar.MONTH, 1);
            dt = c.getTime();

        }

        return dt;
    }

    private void loadEMarkers() {

        database= FirebaseDatabase.getInstance();
        myRef = database.getReference( "marcadoresempresariales/");
        myRef. addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MarcadorEmpresarial> marcadores = new ArrayList<MarcadorEmpresarial>();
                lstLatLngs = new ArrayList<MarcadorEmpresarial>();
                mMap.clear();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    marcadores.add(child.getValue(MarcadorEmpresarial.class));
                    lstLatLngs.add(child.getValue(MarcadorEmpresarial.class));
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
        List<MarcadorEmpresarial> vencidos = new ArrayList<MarcadorEmpresarial>();
        for (MarcadorEmpresarial me: marcadores) {

            Date today = new Date();

            if(today.after(me.getDeadline())){ // si el deadline es superior a hoy
                vencidos.add(me);
            }else{

                LatLng pos = new LatLng(me.getLatitude(),me.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.point2)).title(me.getTitle())
                        .snippet(me.getInfo()));

            }
        }

        //marcadores.removeAll(vencidos); // saco todos los vencidos

    }


    public void eliminarVencidos ()
    {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ejecutar()
    {
        List<MarcadorEmpresarial> vencidos = new ArrayList<MarcadorEmpresarial>();
        Date actual = new Date();
        for (MarcadorEmpresarial me :lstLatLngs)
        {
            if(actual.after(me.getDeadline())) // si se vencio
            {
                vencidos.add(me);
            }
        }

        //lstLatLngs.removeAll(vencidos); // saco todos los vencidos
        persistir();

        t= new Time();
        t.execute();
    }

    public class Time extends AsyncTask<Void, Integer, Boolean>
    {


        @Override
        protected Boolean doInBackground(Void... params) {

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



}

