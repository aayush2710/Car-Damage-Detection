package com.technologies.cardamage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity3 extends AppCompatActivity {
    private RecyclerView gallery;
    private Button save , menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        save = findViewById(R.id.button);
        menu = findViewById(R.id.button2);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity2.Images.clear();
                MainActivity2.Box.clear();
                startActivity(new Intent(MainActivity3.this , MainActivity.class));
                finish();
            }
        });
        final ArrayList<Bitmap> Images = MainActivity2.Images;
        final ArrayList<ArrayList <ArrayList<Float>>> Box = MainActivity2.Box;

        gallery = findViewById(R.id.Gallery);
        MyRecyclerViewAdapter2 myRecyclerViewAdapter = new MyRecyclerViewAdapter2(this, Images , Box , false);
        gallery.setLayoutManager(new GridLayoutManager(this, 2));
        gallery.setAdapter(myRecyclerViewAdapter);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(MainActivity3.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(MainActivity3.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }else{
                        MyRecyclerViewAdapter2 myRecyclerViewAdapter = new MyRecyclerViewAdapter2(MainActivity3.this, Images , Box , true);
                        gallery.setLayoutManager(new GridLayoutManager(MainActivity3.this, 2));
                        gallery.setAdapter(myRecyclerViewAdapter);

                        Toast.makeText(getApplicationContext(),"Saved Successfully", Toast.LENGTH_LONG).show();

                    }


                }else{

                MyRecyclerViewAdapter2 myRecyclerViewAdapter = new MyRecyclerViewAdapter2(MainActivity3.this, Images , Box , true);
                gallery.setLayoutManager(new GridLayoutManager(MainActivity3.this, 2));
                gallery.setAdapter(myRecyclerViewAdapter);

                Toast.makeText(getApplicationContext(),"Saved Successfully", Toast.LENGTH_LONG).show();}


            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}