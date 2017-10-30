package com.example.usuario.mybd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListHistorial extends AppCompatActivity {
    public static final String PATH_historal="historial/";
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    private FirebaseAuth mAuth;

    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_historial);

        mAuth = FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        database= FirebaseDatabase.getInstance();
        linearLayout=(LinearLayout)findViewById(R.id.linearHistorial);
        Log.d("ver","path: "+PATH_historal+user.getUid()+"/");
        myRef=database.getReference(PATH_historal+user.getUid()+"/");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    Ruta myRuta = singleSnapshot.getValue(Ruta.class);

                    TextView text=new TextView(ListHistorial.this);
                    TextView text1;
                    i++;
                    text1=new TextView(ListHistorial.this);
                    text1.setText("___________________________________");
                    text1.setGravity(Gravity.CENTER);

                    linearLayout.addView(text1);
                    text1=new TextView(ListHistorial.this);
                    text1.setText("Recorrido "+i+":");
                    linearLayout.addView(text1);

                    text.setText("Duracion: "+ myRuta.getHoras()+" horas "+ myRuta.getMinutos()+" minutos");
                    linearLayout.addView(text);
                    text=new TextView(ListHistorial.this);
                    text.setText("Fecha: "+ myRuta.getFecha());
                    linearLayout.addView(text);
                    text=new TextView(ListHistorial.this);
                    text.setText("Kilometros: "+ myRuta.getKilometros());
                    linearLayout.addView(text);
                    text=new TextView(ListHistorial.this);

                    text.setText("Clima: "+ myRuta.getClima());
                    linearLayout.addView(text);

                    text=new TextView(ListHistorial.this);
                    text.setText("Inicio: "+ myRuta.getLatitudIncial()+","+ myRuta.getLongitudInicial());
                    linearLayout.addView(text);

                    text=new TextView(ListHistorial.this);
                    text.setText("Fin: "+ myRuta.getLatitudFinal()+","+ myRuta.getLongitudFinal());
                    linearLayout.addView(text);


                }
            }
            @Override public void onCancelled(DatabaseError databaseError)
            {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
            }
        });


    }
}
