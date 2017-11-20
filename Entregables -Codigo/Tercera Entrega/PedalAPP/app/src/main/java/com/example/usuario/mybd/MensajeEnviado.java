package com.example.usuario.mybd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MensajeEnviado extends AppCompatActivity {

    private TextView mensajeEnviado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje_enviado);

        mensajeEnviado = (TextView) findViewById(R.id.mensaje);
        mensajeEnviado.setText(getIntent().getExtras().get("mensaje").toString());
    }
}
