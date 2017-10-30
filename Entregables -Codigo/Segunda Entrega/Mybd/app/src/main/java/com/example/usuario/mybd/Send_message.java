package com.example.usuario.mybd;

import android.content.Intent;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class Send_message extends AppCompatActivity {

    Bundle bundle;

    private TextView to;
    private EditText asunto;
    private EditText mensaje;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private String amigoEnviar="";
    public static final String PATH_USERS="users/";
    public static final String PATH_Mensajes="mensajes/";
    FirebaseUser user;

    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        database = FirebaseDatabase.getInstance();

        user= FirebaseAuth.getInstance().getCurrentUser();


        asunto = (EditText) findViewById(R.id.subjecttxt);
        mensaje = (EditText) findViewById(R.id.msjtxt);

        send= (Button) findViewById(R.id.send_btn);
        bundle = getIntent().getBundleExtra("b2");
        to= (TextView) findViewById(R.id.totxt);
        amigoEnviar = getIntent().getExtras().get("to").toString();
        to.setText(amigoEnviar);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean control = true;
                if(TextUtils.isEmpty(asunto.getText().toString())  )
                {
                    asunto.setError( "Requires" );
                    control = false;
                }
                if(TextUtils.isEmpty(mensaje.getText().toString()) )
                {
                    mensaje.setError( "Requires" );
                    control = false;
                }

                if(control)
                    loadUsers();



            }
        });

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
                    if(myUser.getCorreo().equalsIgnoreCase(amigoEnviar)) {
                        // amigo
                        amigo= myUser;

                        amigoExiste = true;

                    }

                    if(myUser.getCorreo().equalsIgnoreCase(user.getEmail())) {
                        //soy yo
                        //Toast.makeText(getBaseContext(),"Se encontro al usuario Yo "+myUser.getNombres(),Toast.LENGTH_SHORT).show();
                        yo = myUser;

                    }
                }

                enviarMensaje(yo,amigo);

               /* if(amigoExiste){
                    String mensaje = "El correo fue encontrado, ";

                    sonAmigos = yaAmigos(yo);
                    if(sonAmigos){
                        mensaje += "pero ya son amigos";
                    }else{


                        yo.getAmigos().add(amigo.getCorreo());
                        myRef=database.getReference(PATH_USERS+user.getUid());
                        myRef.setValue(yo);

                        mensaje += " y fue agregado correctamente";
                    }
                    Toast.makeText(getBaseContext(),mensaje,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getBaseContext(),"No existe el usuario " + amigoAgregar,Toast.LENGTH_SHORT).show();
                }

                */


            }
            @Override public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
                Toast.makeText(getBaseContext(),"Error lectura user",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void enviarMensaje(MyUser yo, MyUser amigo) {

        if(yo.getBuzonSalida() == null)
            yo.setBuzonSalida(new HashMap<String, String>());


        String enviar = "Para: "+to.getText().toString()+"\n"+"De: "+yo.getCorreo()+"\n"+"Asunto: "+asunto.getText().toString()+"\n"+ "Mensaje: "+mensaje.getText().toString();

        yo.getBuzonSalida().put( myRef.push().getKey(),enviar);
        myRef=database.getReference(PATH_USERS+user.getUid());
        myRef.setValue(yo);


        String key = myRef.push().getKey();
        myRef=database.getReference(PATH_Mensajes+key);
        myRef.setValue(enviar);

        Toast.makeText(getBaseContext(),"Mensaje Enviado", Toast.LENGTH_LONG).show();
        Intent i= new Intent(Send_message.this, FriendList.class);
        startActivity(i);


    }


}