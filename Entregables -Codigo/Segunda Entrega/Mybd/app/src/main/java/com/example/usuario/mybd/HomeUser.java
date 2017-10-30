package com.example.usuario.mybd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeUser extends AppCompatActivity  {
    private FirebaseAuth mAuth;
    TextView usaurio;
    TextView correo;
    TextView foto;

    @Override public boolean onCreateOptionsMenu(Menu menu){ getMenuInflater().inflate(R.menu.menu, menu); return true; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);
        mAuth = FirebaseAuth.getInstance();
        usaurio=(TextView)findViewById(R.id.txtusuario) ;
        correo=(TextView) findViewById(R.id.txtCorreo);
        foto=(TextView) findViewById(R.id.txtFoto);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            usaurio.setText(user.getDisplayName());
            correo.setText(user.getEmail());
            try{
            foto.setText(user.getPhotoUrl().toString());
            }catch (Exception e){

            }
        }

    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemClicked = item.getItemId();

        if(itemClicked == R.id.menuLogOut){ mAuth.signOut(); Intent intent = new Intent(HomeUser.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); startActivity(intent);
        }//else if (itemClicked == R.id.menuSettings){
            //Abrir actividad para configuración etc
        return super.onOptionsItemSelected(item);
    }

}
