package com.example.usuario.mybd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Chat extends AppCompatActivity {

    private EditText mensajenuevo ;
    private ListView mensajes;
    private Button sendbtn;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private String amigoEnviar="";
    public static final String PATH_USERS="users/";
    private  ArrayList<ChatMessage> chats ;
    private  ArrayList<ChatMessage> chatsPantalla ;

    public static final String PATH_CHAT="chats/";
    public static final String PATH_Members="members/";
    public static final String PATH_Participantes="chats/participantes/";
    public static final String PATH_Mensjaes="chats/participantes/mensajes/";
    public static String chatID;
    public String chatKey ="";
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        database = FirebaseDatabase.getInstance();
        chats = new ArrayList<ChatMessage>();
        chatsPantalla = new ArrayList<ChatMessage>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        amigoEnviar = getIntent().getExtras().get("to").toString();
        mensajenuevo = (EditText) findViewById(R.id.msj);
        mensajes = (ListView) findViewById(R.id.listmsjs);
        sendbtn = (Button) findViewById(R.id.Send);
        myRef=database.getReference(PATH_CHAT);
        chatID = myRef.push().getKey();



        recuperarMensajes();

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadUsers();


            }
        });

    }

    private void recuperarMensajes() {

        myRef = database.getReference(PATH_Members);
        myRef. addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<ArrayList<String>> ListType = new GenericTypeIndicator<ArrayList<String>>() { };
                     ArrayList<String> as = singleSnapshot.getValue(ListType);

                    if(isMyChat(as))
                    {

                        chatKey = singleSnapshot.getKey();
                        displayMessaje(chatKey);
                    }


                }

                if(chatKey.matches(""))
                {
                  ArrayList<String> err = new ArrayList<String>();
                  err.add("No tienes ningún chat con "+ amigoEnviar + " enviale un mensaje!");

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            getBaseContext(), android.R.layout.simple_list_item_1,  err);

                    mensajes.setAdapter(arrayAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
            }
        });

    }

    private void displayMessaje(String chatKey) {
        myRef = database.getReference(PATH_CHAT+chatKey+"/");
        myRef. addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              chatsPantalla = new ArrayList<ChatMessage>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                     chatsPantalla.add(singleSnapshot.getValue(ChatMessage.class));


                }
                chats = new ArrayList<ChatMessage>();
                chats.addAll(chatsPantalla);
                format();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
            }
        });

    }

    private void format() {

        ArrayList<String> listchats = new ArrayList<String>();
        mensajes.setAdapter(null);
        for (ChatMessage ch : chatsPantalla)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");
            listchats.add(ch.getMessageUser()+":\n" + ch.getMessageText()+"\n"+sdf.format(new Date(ch.getMessageTime()))+"\n");
        }

        if(listchats.isEmpty()){
            listchats.add("No tienes ningún chat con "+ amigoEnviar + " enviale un mensaje!");

        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getBaseContext(), android.R.layout.simple_list_item_1,  listchats);

        mensajes.setAdapter(arrayAdapter);



    }

    private boolean isMyChat(ArrayList<String> as) {


       if(as.get(0).equals(user.getEmail()) && as.get(1).equals(amigoEnviar) )
           return true;

        if(as.get(0).equals(amigoEnviar) && as.get(1).equals(user.getEmail()) )
            return true;


        return false;
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


            }
            @Override public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "error en la consulta", databaseError.toException());
                Toast.makeText(getBaseContext(),"Error lectura user",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void enviarMensaje(MyUser yo, MyUser amigo) {

        String n1 = yo.getCorreo().replace("@","");
        String n2 = n1.replace(".","");

        String n3 = amigo.getCorreo().replace("@","");
        String n4 = n3.replace(".","");

       if(chatKey.equals("")){
        myRef=database.getReference(PATH_Members+n2+n4+"/");

        ArrayList<String> l = new ArrayList<String>();

        l.add(yo.getCorreo());
        l.add(amigo.getCorreo());
        myRef.setValue(l);
       }
        else
        myRef=database.getReference(PATH_Members+chatKey+"/");

    if(chatKey.equals(""))
        myRef=database.getReference(PATH_CHAT+n2+n4+"/");
        else
            myRef=database.getReference(PATH_CHAT+chatKey+"/");

        if(mensajenuevo.getText().toString().matches("")){
            Toast.makeText(this, "Mensaje Vacio", Toast.LENGTH_SHORT).show();
        }else{

            chats.add(new ChatMessage(mensajenuevo.getText().toString(),yo.getNombres()+" "+yo.getApellidos()));
            chatsPantalla.add(new ChatMessage(mensajenuevo.getText().toString(),yo.getNombres()+" "+yo.getApellidos()));
            format();
            myRef.setValue(chats); // eso será una lista
            mensajenuevo.setText("");
        }











    }


}
