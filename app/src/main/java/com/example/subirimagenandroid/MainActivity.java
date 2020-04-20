package com.example.subirimagenandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
{
    Button obtenerImagendelagaleria,subirImagenAlServidor;
    ImageView mostrarImagenSeleccionada;
    EditText imageName;
    Bitmap bitmap;
    String ImageTag="image_tag";
    String ImageName="image_data";
    ProgressDialog progressDialog;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage;
    String obtenernombredeleditText;
    HttpsURLConnection httpsURLConnection;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter;
    int RC;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    boolean check=true;
    private int GALLERY=1,  CAMERA=2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        obtenerImagendelagaleria=findViewById(R.id.selectButton);
        subirImagenAlServidor=findViewById(R.id.uploadButton);
        mostrarImagenSeleccionada=findViewById(R.id.imageView);
        imageName=findViewById(R.id.imageName);

        byteArrayOutputStream=new ByteArrayOutputStream();

        obtenerImagendelagaleria.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mostrarCuadroDialogo();
            }
        });

        subirImagenAlServidor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    private void mostrarCuadroDialogo() {

    }
}