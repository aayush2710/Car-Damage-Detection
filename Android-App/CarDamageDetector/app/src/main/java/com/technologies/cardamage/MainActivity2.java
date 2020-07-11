package com.technologies.cardamage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity2 extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 2;
    private static final int PICK_IMAGE = 199 ;
    private RecyclerView gallery;
    private Button capt ,proceed ,Select , clear;
    public static ArrayList <Bitmap> Images = new ArrayList<>();
    public static TextView textView;
    public static ProgressBar progressBar;
    private Uri imageUri;
    public static ArrayList<ArrayList <ArrayList<Float>>> Box;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Create");
        setContentView(R.layout.activity_main2);
        capt = findViewById(R.id.capt);
        gallery = findViewById(R.id.Gallery);
        proceed = findViewById(R.id.proceed);
        Select = findViewById(R.id.capt2);
        clear = findViewById(R.id.clear);
        progressBar = findViewById(R.id.progressBar2);

        Intent mIntent = getIntent();
        //Bitmap photo = mIntent.getParcelableExtra("Image");
        Bitmap photo = null;
        Uri uri = (Uri) mIntent.getExtras().get("Uri");
        try {
            photo = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), uri);
            photo = Bitmap.createScaledBitmap(photo, 608, 608, true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Images.clear();
        Images.add(photo);
        int w = photo.getWidth();
        int h = photo.getHeight();
        System.out.println(w);
        System.out.println(h);
        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, Images);
        gallery.setLayoutManager(new GridLayoutManager(this, 2));
        gallery.setAdapter(myRecyclerViewAdapter);
        capt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermission();
                dispatchTakePictureIntent();

            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Detection detection = new Detection(MainActivity2.this ,Images);
                MainActivity2.Box = detection.StartProcess();

                Intent myIntent = new Intent(MainActivity2.this , MainActivity3.class);
                startActivity(myIntent);

            }
        });

        Select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Images.clear();
                MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(MainActivity2.this, Images);
                gallery.setLayoutManager(new GridLayoutManager(MainActivity2.this, 2));
                gallery.setAdapter(myRecyclerViewAdapter);
            }
        });

    }
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                Bitmap photo = null;
                try {
                    photo = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    photo = Bitmap.createScaledBitmap(photo, 608, 608, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int w = photo.getWidth();
                int h = photo.getHeight();
                System.out.println(w);
                System.out.println(h);
                Images.add(photo);
            }
            if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
                imageUri = data.getData();
                Bitmap photo = null;
                try {
                    photo = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    photo = Bitmap.createScaledBitmap(photo, 608, 608, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int w = photo.getWidth();
                int h = photo.getHeight();
                System.out.println(w);
                System.out.println(h);
                Images.add(photo);
            }

                MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, Images);
                gallery.setLayoutManager(new GridLayoutManager(this, 2));
                gallery.setAdapter(myRecyclerViewAdapter);



        }



    private void dispatchTakePictureIntent() {
            ContentValues values = new ContentValues();

            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }

        private void getPermission(){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);

            }

        }


    }
