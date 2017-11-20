package com.example.usuario.mybd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EProgramarRuta extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button programar;
    private EditText titulo;
    private DatePicker date;
    private LatLng inicio;
    private LatLng fin;
    private int contador;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ArrayList<RutaProgramada> rutas;
    private TimePicker time;
    private EditText descrip;

    public	final	static	double	RADIUS_OF_EARTH_KM	=	6371;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eprogramar_ruta);

        programar = (Button) findViewById(R.id.bton_programar);
        titulo = (EditText) findViewById(R.id.RPtit);
        date=(DatePicker) findViewById(R.id.dpep);
        descrip= (EditText) findViewById(R.id.desc);
        time= (TimePicker) findViewById(R.id.timeRP);

        contador = 1;

        database= FirebaseDatabase.getInstance();

        rutas= new ArrayList<RutaProgramada>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        LatLng bogota = new LatLng(4.598156094243696, -74.07604694366455);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        loadList();



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                if(contador==1)
                {
                    mMap.addMarker(new MarkerOptions().position(point).title("Inicio").icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.startpng)));
                    inicio = point;

                    contador++;
                }
                else if(contador == 2)
                {
                    mMap.addMarker(new MarkerOptions().position(point).title("Fin").icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.end)));
                    fin = point;
                    contador++;
                }
                else if( contador ==3){

                    mMap.clear();
                    contador = 1;

                }

            }
        });

        programar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(titulo.getText().toString() == null || inicio == null || fin == null || descrip.getText().toString() == null )
                {
                    Toast.makeText(getBaseContext(), "Asegurese de llenar toda la informaciÃ³n", Toast.LENGTH_LONG).show();
                }else{
                    loadList();
                    Ruta r = new Ruta();
                    r.setLatitudIncial(inicio.latitude);
                    r.setLongitudInicial(inicio.longitude);
                    r.setLatitudFinal(fin.latitude);
                    r.setLongitudFinal(fin.longitude);

                    int   day  = date.getDayOfMonth();
                    int   month= date.getMonth();
                    int   year = date.getYear();



                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    String formatedDate = sdf.format(new Date(year, month, day,time.getCurrentHour(),time.getCurrentMinute()));

                    Date date2= new Date();
                    try {
                        date2 = sdf.parse(formatedDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    r.setFecha(date2);
                    r.setKilometros(calcularKm(inicio,fin).floatValue());

                    RutaProgramada rp= new RutaProgramada(r,titulo.getText().toString(),descrip.getText().toString());

                    rutas.add(rp);

                    persist();
                }

            }
        });


    }

    private Double calcularKm(LatLng inicio, LatLng fin) {

        double	latDistance =	Math.toRadians(fin.latitude	- inicio.latitude);
        double	lngDistance =	Math.toRadians(fin.longitude	 - inicio.longitude);
        double	a	=	Math.sin(latDistance /	2)	*	Math.sin(latDistance /	2)
                +	Math.cos(Math.toRadians(fin.latitude))	 *	Math.cos(Math.toRadians(inicio.latitude))
                *	Math.sin(lngDistance /	2)	*	Math.sin(lngDistance /	2);
        double	c	=	2	*	Math.atan2(Math.sqrt(a),	 Math.sqrt(1	- a));
        double	result	=	RADIUS_OF_EARTH_KM	*	c;
        return	Math.round(result*100.0)/100.0;
    }

    private void loadList() {

        myRef = database.getReference("rutasprogramadas/");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rutas = new ArrayList<RutaProgramada>();
                mMap.clear();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    rutas.add(child.getValue(RutaProgramada.class));
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
            }
        });
    }

    private void persist() {


        myRef=database.getReference("rutasprogramadas/");
        myRef.setValue(rutas);
        Toast.makeText(getBaseContext(),"Ruta programdada correctamente", Toast.LENGTH_SHORT).show();
    }
}
