package com.example.usuario.mybd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ListHistorial extends AppCompatActivity{
    public static final String PATH_historal="historial/";
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    ListView lista;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_historial);
        lista=(ListView)findViewById(R.id.ListaHistorialView);
        mAuth = FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        database= FirebaseDatabase.getInstance();
        Log.d("ver","path: "+PATH_historal+user.getUid()+"/");
        myRef=database.getReference(PATH_historal+user.getUid()+"/");
        myRef.addValueEventListener(new ValueEventListener() {

            List<Ruta> rutas=new ArrayList<Ruta>();
            String datos[];
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    Ruta myRuta = singleSnapshot.getValue(Ruta.class);
                    rutas.add(myRuta              );
                }
                datos=new String[rutas.size()];
                for ( Ruta r:rutas
                     ) {
                    datos[i]=(i+1)+". "+r.toString();
                    i++;
                }
                //      Toast.makeText(getBaseContext(),datos.length+"-----<",Toast.LENGTH_SHORT).show();

                ArrayAdapter<String> adapter
                        = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, datos);
                lista.setAdapter(adapter);

                lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  //      Toast.makeText(getBaseContext(),""+datos[position].toString(),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), DetailHistory.class);
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
}
