package com.example.usuario.mybd;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.List;

public class FriendList extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef;
    public static final String PATH_USERS="users/";

    FirebaseUser user;

    private MyUser yo;
    private ListView lv;

    boolean ya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        database = FirebaseDatabase.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();
        lv = (ListView) findViewById(R.id.lv_friend_list);

        loadUsers();






    }




    public void loadUsers() {

        myRef = database.getReference(PATH_USERS);



        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                yo = new MyUser();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                {
                    MyUser myUser = singleSnapshot.getValue(MyUser.class);
                    if( myUser.getCorreo().equalsIgnoreCase(user.getEmail()) ) {
                        yo = myUser;

                        if( yo.getAmigos() !=null){


                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                    getBaseContext(), android.R.layout.simple_list_item_1, yo.getAmigos() );

                            lv.setAdapter(arrayAdapter);



                        }else{

                            Toast.makeText(FriendList.this, "No tienes amigos", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(FriendList.this,LocalizacionUser.class);
                            startActivity(i);
                        }

                        break;
                    }

                }

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(FriendList.this,VistaAmigo.class);
                        intent.putExtra("correoAmigo",lv.getItemAtPosition(position).toString());
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


}