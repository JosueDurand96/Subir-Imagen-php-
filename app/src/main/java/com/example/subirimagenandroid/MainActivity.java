package com.example.subirimagenandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    Button obtenerImagendelagaleria, subirImagenAlServidor;
    ImageView mostrarImagenSeleccionada;
    EditText imageName;
    Bitmap bitmap;
    String ImageTag = "nombre";
    String ImageName = "foto";
    ProgressDialog progressDialog;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage;
    String obtenernombredeleditText;
    HttpURLConnection httpURLConnection;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter;
    int RC;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    boolean check = true;
    private int GALLERY = 1, CAMERA = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        obtenerImagendelagaleria = findViewById(R.id.selectButton);
        subirImagenAlServidor = findViewById(R.id.uploadButton);
        mostrarImagenSeleccionada = findViewById(R.id.imageView);
        imageName = findViewById(R.id.imageName);

        byteArrayOutputStream = new ByteArrayOutputStream();

        obtenerImagendelagaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCuadroDialogo();
            }
        });

        subirImagenAlServidor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCuadroDialogo(); //Esto es un método

            }
        });

        subirImagenAlServidor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenernombredeleditText = imageName.getText().toString();
                UploadImageToServer(); //Esto es un método
            }
        });

        //aquí vamos a trabajar los permisos dentro del OnCreate()

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 5);
            }
        }
    }//fin del onCreate


    //Aqui rellenamos el cuadro del diálogo coon los elementos que se van a mostrar
    private void mostrarCuadroDialogo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        String[] pictureDialogItem = {"Photo Gallery", "Camera"};
        alertDialog.setItems(pictureDialogItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case 0:
                        choosePhotoFromGallery();
                        break;
                    case 1:
                        takePhotoFromCamera();
                        break;
                }
            }
        });
        alertDialog.show();
    }


    //Con este método elegimos la imagen de la galeria de imagenes
    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }


    //Con este metodo hacemos una foto con la camara del teléfono
    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    //Método para traer la imagen de la galería o de la cámara

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==this.RESULT_CANCELED)
        {
            return;
        }
        if (requestCode==GALLERY)
        {
            if (data!=null)
            {
                Uri contentURI=data.getData();
                try
                {
                    //guardamos la imagen de la galería como mapa de bits
                    bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),contentURI);
                    //y ahora la mostramos en nuestro ImageView
                    mostrarImagenSeleccionada.setImageBitmap(bitmap);
                    //Hacemos visible el boton Upload
                    subirImagenAlServidor.setVisibility(View.VISIBLE);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"Se ha producido un Error",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        else if (requestCode==CAMERA)
        {
            //Guardamos la imagen conseguida de la cámara en un mapa de bits
            bitmap=(Bitmap) data.getExtras().get("data");
            //la mostramos en nuestro ImageView
            mostrarImagenSeleccionada.setImageBitmap(bitmap);
            subirImagenAlServidor.setVisibility(View.VISIBLE);
        }
    }

    //----------------------------------------------------------------------------------------------------
    //Ahora vamos a trrabajar el metodo que nos va a permitir subir la imageen al servidor

    public void UploadImageToServer()
    {
        bitmap.compress(Bitmap.CompressFormat.JPEG,40,byteArrayOutputStream);
        byteArray=byteArrayOutputStream.toByteArray();
        //Ahora codificamos la imagen y la guardamos en la variable convetImage
        ConvertImage= Base64.encodeToString(byteArray,Base64.DEFAULT);
        //AsyncTask

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(MainActivity.this,
                        "Se esta subiendo la imagen ", "Por favor espere...",
                        false, false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Se subió la imagen correctamente", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }

            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass=new ImageProcessClass();
                HashMap<String,String> HashMapParams=new HashMap<>();
                HashMapParams.put(ImageTag,obtenernombredeleditText);
                HashMapParams.put(ImageName,ConvertImage);

                String finalData=imageProcessClass.ImageHttpRequest("http://josuedurand-001-site2.btempurl.com/josueAndroid/mimercado/Admin/cargarfoto.php",HashMapParams);
                return finalData;
            }


        }


        AsyncTaskUploadClass asyncTaskUploadClass=new AsyncTaskUploadClass();
        asyncTaskUploadClass.execute();

    }


    //---------------------------------------------------------------------------------------

    //ImageProcessClass
    public class ImageProcessClass
    {
        public String ImageHttpRequest(String requestURL, HashMap<String,String>PData)
        {
            StringBuilder stringBuilder=new StringBuilder();
            try
            {
                url=new URL(requestURL);
                httpURLConnection=(HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                outputStream=httpURLConnection.getOutputStream();
                bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write(bufferedWriterDataFN(PData));
                bufferedWriter.flush();
                bufferedWriter.close();
                RC=httpURLConnection.getResponseCode();
                if (RC==HttpURLConnection.HTTP_OK){
                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2 ;
                    while ((RC2=bufferedReader.readLine())!=null){
                        stringBuilder.append(RC2);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }
        private String bufferedWriterDataFN(HashMap<String,String> HashMapParams)
            throws UnsupportedEncodingException{
            stringBuilder = new StringBuilder();
            for (Map.Entry<String,String>Key:HashMapParams.entrySet()){
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode(Key.getKey(),"UTF-8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(Key.getValue(),"UTF-8"));
            }
            return stringBuilder.toString();

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==5){
            Toast.makeText(this, "Gracias por permitirnos usar tu cama", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Prende tu camara pee ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        isFinishing();
    }
}



