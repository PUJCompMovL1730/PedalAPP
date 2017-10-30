package com.example.usuario.mybd;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddFriend extends AppCompatActivity {

    private EditText nombreAmigo;
    private Button registrar;

    FirebaseDatabase database;
    DatabaseReference myRef;

    FirebaseUser user;

    public static final String PATH_USERS="users/";
    public static  String amigoAgregar="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        database = FirebaseDatabase.getInstance();

        user= FirebaseAuth.getInstance().getCurrentUser();

        nombreAmigo = (EditText) findViewById(R.id.et_friend_username);
        registrar = (Button) findViewById(R.id.bt_add_friend_commit);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amigo = nombreAmigo.getText().toString();

                if(TextUtils.isEmpty(amigo))
                {
                    nombreAmigo.setError( "Requires" );

                }else{

                    amigoAgregar = amigo;
                    loadUsers();

                }
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

                    if(myUser.getCorreo().equalsIgnoreCase(amigoAgregar)) {
                        // amigo
                        amigo= myUser;

                        amigoExiste = true;

                    }

                    if(myUser.getCorreo().equalsIgnoreCase(user.getEmail())) {
                        //soy yo

                        //Toast.makeText(getBaseContext(),"Se encontro al usuario Yo "+myUser.getNombres(),Toast.LENGTH_SHORT).show();
                        yo = myUser;
                        if(yo.getAmigos() == null)
                            yo.setAmigos(new ArrayList<String>());
                    }
                }


                if(amigoExiste){
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


            }
            @Override public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
                Toast.makeText(getBaseContext(),"Error lectura user",Toast.LENGTH_SHORT).show();
            }
        });


    }

    public boolean yaAmigos(MyUser user){
        for (String correo: user.getAmigos()) {
            if(correo.equals(amigoAgregar)){
                return true;
            }
        }
        return false;
    }


}
