package com.example.usuario.mybd;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VistaAmigo extends AppCompatActivity {

    TextView tv_CorreoAmigo;

    Button btn_eliminarAmigo;
    Button btn_enviarMensaje;


    //FIREBASE
    FirebaseDatabase database;
    DatabaseReference myRef;

    FirebaseUser user;
    //

    public static final String PATH_USERS="users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_amigo);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        tv_CorreoAmigo = (TextView) findViewById(R.id.tv_correoAmigo);

        final String correoAmigo = getIntent().getExtras().getString("correoAmigo");
        tv_CorreoAmigo.setText(correoAmigo);

        btn_eliminarAmigo = (Button) findViewById(R.id.btn_eliminarAmigo);
        btn_enviarMensaje = (Button) findViewById(R.id.btn_enviarMensaje);

        btn_enviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VistaAmigo.this, Chat.class);
                i.putExtra("to", correoAmigo);
                startActivity(i);

            }
        });








        btn_eliminarAmigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myRef = database.getReference(PATH_USERS);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        MyUser yo = new MyUser();
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            MyUser myUser = singleSnapshot.getValue(MyUser.class);
                            if (myUser.getCorreo().equalsIgnoreCase(user.getEmail())) {
                                yo = myUser;

                                for (String correo : yo.getAmigos()) {
                                    if (correo.equals(correoAmigo)) {
                                        yo.getAmigos().remove(correoAmigo);
                                        break;
                                    }
                                }

                                myRef = database.getReference(PATH_USERS + user.getUid());
                                myRef.setValue(yo);

                                Intent intent = new Intent(VistaAmigo.this,FriendList.class);
                                startActivity(intent);

                                break;
                            }

                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });
    }
}