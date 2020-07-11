package com.technologies.cardamage;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyRecyclerViewAdapter2 extends RecyclerView.Adapter<MyRecyclerViewAdapter2.ViewHolder> {

    private ArrayList<Bitmap> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ArrayList<ArrayList<ArrayList<Float>>> Box;
    private Boolean save;
    private Context context;
    // data is passed into the constructor
    MyRecyclerViewAdapter2(Context context, ArrayList<Bitmap> data , ArrayList<ArrayList<ArrayList<Float>>> Box, Boolean save) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.Box = Box;
        this.save = save;
        this.context = context;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.image, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap tempBitmap = Bitmap.createScaledBitmap(mData.get(position), 608, 608, true);
        Canvas canvas = new Canvas(tempBitmap);
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setAntiAlias(true);
        p.setFilterBitmap(true);
        p.setDither(true);
        p.setColor(Color.RED);
        ArrayList<ArrayList<Float>> Coord = Box.get(position);
        for(int t=0 ; t<Coord.size() ; t++) {
            float x_min = Coord.get(t).get(0);
            float y_min = Coord.get(t).get(1);
            float x_max = Coord.get(t).get(2);
            float y_max = Coord.get(t).get(3);

            canvas.drawLine(x_min, y_min, x_min, y_max, p);//up
            canvas.drawLine(x_min, y_max, x_max, y_max, p);//left
            canvas.drawLine(x_max, y_max, x_max, y_min, p);//down
            canvas.drawLine(x_max, y_min, x_min, y_min, p);

        }

        holder.imageView.setImageBitmap(tempBitmap);
        holder.imageView.draw(canvas);
        if(save){
                SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyyMMddDhhmmss");
                String date = simpleDataFormat.format(new Date());
                String name = "output"+date+".jpg";


            try {
                saveImage(tempBitmap,name );


            } catch (IOException e) {
                System.out.println("FAIL");
                e.printStackTrace();
            }

        }
    }

    private void saveImage(Bitmap bitmap, @NonNull String name) throws IOException {
        boolean saved;
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = this.context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/Car Damage Detector");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).toString() + File.separator + "Car Damage Detector";

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, name + ".png");
            fos = new FileOutputStream(image);

        }

        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }

    // total number of cells
        @Override
        public int getItemCount() {
            return mData.size();
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView imageView;

            ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView2);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        // convenience method for getting data at click position
        Bitmap getItem(int id) {
            return mData.get(id);
        }

        // allows clicks events to be caught


        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, int position);
        }
    }