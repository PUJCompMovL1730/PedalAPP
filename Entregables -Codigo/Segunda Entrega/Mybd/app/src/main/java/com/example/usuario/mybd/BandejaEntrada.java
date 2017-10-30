package com.example.usuario.mybd;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BandejaEntrada extends AppCompatActivity {


    FirebaseDatabase database;
    DatabaseReference myRef;
    public static final String PATH_USERS="users/";
    public static final String PATH_Mensajes="mensajes/";
    FirebaseUser user;

    public static MyUser yo ;
    private ListView lv;

    private List<String> mensajes = new ArrayList<String>() ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandeja_entrada);

        lv = (ListView) findViewById(R.id.lv_mensajes_entrada);
        database = FirebaseDatabase.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();
        loadUsers();


    }

    public void loadUsers() {

        myRef = database.getReference(PATH_USERS);


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {

                boolean sonAmigos = false;
                boolean amigoExiste = false;

                MyUser amigo = new MyUser();
                MyUser yo = new MyUser();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    MyUser myUser = singleSnapshot.getValue(MyUser.class);

                    if(myUser.getCorreo().equalsIgnoreCase(user.getEmail())) {
                        //soy yo
                        yo = myUser;

                    }
                }

                buscarMensajes (yo);

            }
            @Override public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
                Toast.makeText(getBaseContext(),"Error lectura user",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void buscarMensajes(MyUser yo) {

        myRef = database.getReference(PATH_Mensajes);


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {



                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    String mensaje = singleSnapshot.getValue(String.class);
                    int tamcorreo =0;
                    for(int i=1;i<=mensaje.length(); i++){

                        if(mensaje.charAt(i+1)=='D' && mensaje.charAt(i+2)=='e' && mensaje.charAt(i+3)==':' ){
                            tamcorreo = i;
                            if(mensaje.substring(0,tamcorreo).substring(6).equals(user.getEmail())){
                                mensajes.add(mensaje);
                                Log.i("msj", mensaje.substring(0,tamcorreo).substring(6));
                            }


                            break;
                        }
                    }




                }

                crearBandejaDeEntrada();


            }
            @Override public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
                Toast.makeText(getBaseContext(),"Error lectura user",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void crearBandejaDeEntrada()
    {

        myRef = database.getReference(PATH_USERS);


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {

                MyUser yo = new MyUser();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    MyUser myUser = singleSnapshot.getValue(MyUser.class);

                    if(myUser.getCorreo().equalsIgnoreCase(user.getEmail())) {
                        //soy yo
                        yo = myUser;

                        yo.setBuzonEntrada(new HashMap<String, String>()); // SIEMPRE SE INICIALIZA Y SE VUELVE A LLENAR

                        for(String e: mensajes)
                        {
                            myRef = database.getReference(PATH_USERS);
                            yo.getBuzonEntrada().put( myRef.push().getKey(),e);
                        }


                        myRef=database.getReference(PATH_USERS+user.getUid());
                        myRef.setValue(yo);

                        if( yo.getBuzonEntrada() !=null){

                            List<String> l = listaAsuntos(yo);
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                    getBaseContext(), android.R.layout.simple_list_item_1, l );

                            lv.setAdapter(arrayAdapter);



                        }else{

                            Toast.makeText(BandejaEntrada.this, "No tienes mensajes", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(BandejaEntrada.this,LocalizacionUser.class);
                            startActivity(i);
                        }


                    }
                }

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(BandejaEntrada.this, MensajeEnviado.class);
                        intent.putExtra("mensaje",mensajes.get(position));
                        startActivity(intent);

                    }
                });

            }
            @Override public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
                Toast.makeText(getBaseContext(),"Error lectura user",Toast.LENGTH_SHORT).show();
            }
        });




    }

    private List<String> listaAsuntos(MyUser u) {

        Map<String, String> map = u.getBuzonEntrada();
        List<String> retorna = new ArrayList<String>();
        int contador = 1;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String e= entry.getValue();
            mensajes.add(e);
            retorna.add("Mensaje "+contador+"...Click para Información");
            ++contador;


        }
        return retorna;
    }
}


