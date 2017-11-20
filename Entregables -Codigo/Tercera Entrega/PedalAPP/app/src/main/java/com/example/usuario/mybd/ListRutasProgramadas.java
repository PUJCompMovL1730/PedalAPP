package com.example.usuario.mybd;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListRutasProgramadas extends AppCompatActivity {
    public static final String PATH_historal="rutasprogramadas";
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_rutas_programadas);
        lista=(ListView)findViewById(R.id.ListaRutasProgramdas);
        mAuth = FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        database= FirebaseDatabase.getInstance();
        myRef=database.getReference("rutasprogramadas/");
        myRef.addValueEventListener(new ValueEventListener() {

            List<RutaProgramada> rutas=new ArrayList<RutaProgramada>();
            String datos[];
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                rutas=new ArrayList<RutaProgramada>();

                int i=0;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    RutaProgramada myRuta = singleSnapshot.getValue(RutaProgramada.class);
                    rutas.add(myRuta);
                }
                datos=new String[rutas.size()];
                for ( RutaProgramada r:rutas) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM HH:mm");

                    Date fecha = rutas.get(i).getR().getFecha();
                    simpleDateFormat.setLenient(false);
                    Calendar c = Calendar.getInstance();
                    c.setTime(fecha);
                    simpleDateFormat.setCalendar(c);

                    datos[i]=(i+1)+". Titulo: "+r.getTitulo()+
                            "\nInicia: " +  simpleDateFormat.format(c.getTime())+ " del: " + fecha.getYear()
                            +"\n"+"Kilometros: "+rutas.get(i).getR().getKilometros()
                            +"\n" +"Decripción: "+rutas.get(i).getDescrip();
                    i++;
                }
                //      Toast.makeText(getBaseContext(),datos.length+"-----<",Toast.LENGTH_SHORT).show();

                if(rutas.size()==0){

                    datos = new String[1];
                    datos[0] = "No hay rutas programadas";
                }

                ArrayAdapter<String> adapter
                        = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, datos);
                lista.setAdapter(adapter);

                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                    @Override public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        //      Toast.makeText(getBaseContext(),""+datos[position].toString(),Toast.LENGTH_SHORT).show();
                          Intent intent = new Intent(getBaseContext(), LocalizacionUser.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("position",position+1);
                                        bundle.putSerializable("ruta",rutas.get(position));
                                        intent.putExtra("bundle", bundle);
                                        startActivity(intent);



                    }
                });

            }
            @Override public void onCancelled(DatabaseError databaseError)
            {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
            }
        });


    }



    void Rutas(){
      /*  database= FirebaseDatabase.getInstance();
        myRef = database.getReference(PATH_marcadores);
        myRef. addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // mMap.clear();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                   // marcadores.add(child.getValue(MarcadorEmpresarial.class));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log.w(TAG, "error en la consulta", databaseError.toException());
            }
        });
        */
    }
}
