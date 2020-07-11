package com.technologies.cardamage;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Detection {
    private static final double NMS_T = 0.5;
    private static final double CONF_T = 0.55;
    private ArrayList<Bitmap> Images;

    public static Module Darknet;

    private ArrayList<ArrayList <ArrayList<Float>>> Boxes = new ArrayList<>();
    Detection(Context context, ArrayList<Bitmap> data) {
        this.Images = data;
        try {
            Darknet = Module.load(assetFilePath(context, "model.pt"));
        } catch (IOException e) {
            Log.e("PytorchHelloWorld", "Error reading assets", e);

        }




    }

    public ArrayList<ArrayList <ArrayList<Float>>> StartProcess(){
        int Progress = 100/Images.size();
        for(int i=0 ; i<Images.size() ; i++){
            Bitmap photo = Images.get(i);
            photo = Bitmap.createScaledBitmap(photo, 608, 608, false);

            Model model = new Model();
            ArrayList <ArrayList<Float>> output1 = model.doInBackground(photo);
            output1 = xywh2xyxy(output1);
            output1 = filter(output1);
            output1 = NMS_filter(output1);
            System.out.println(output1.size());
            Boxes.add(output1);

            MainActivity2.progressBar.setProgress(Progress*(i+1));



        }
        return Boxes;


    }

    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            System.out.println(file.getAbsolutePath());
            return file.getAbsolutePath();

        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            System.out.println(file.getAbsolutePath());
            return file.getAbsolutePath();
        }
    }



    public ArrayList <ArrayList<Float>> xywh2xyxy(ArrayList <ArrayList<Float>> x){
        ArrayList <ArrayList<Float>> res = new ArrayList<>();
        for(int k=0 ; k<22743 ; k++){
            ArrayList<Float> temp = new ArrayList<>();
            temp.add( x.get(k).get(0) -  x.get(k).get(2) / 2);
            temp.add( x.get(k).get(1) -  x.get(k).get(3) / 2);
            temp.add( x.get(k).get(0) +  x.get(k).get(2) / 2);
            temp.add( x.get(k).get(1) +  x.get(k).get(3) / 2);
            temp.add( x.get(k).get(4));
            temp.add( x.get(k).get(5));

            res.add(temp);

        }
        return res;


    }
    public ArrayList <ArrayList<Float>> filter(ArrayList <ArrayList<Float>> x){
        int l=0;
        while(l < x.size()){
            if(x.get(l).get(4) < CONF_T){
                x.remove(l);
            }else{
                l++;
            }
        }

        return x;

    }

    public ArrayList <ArrayList<Float>> NMS_filter(ArrayList <ArrayList<Float>> x){
        for(int i=0;i<x.size();i++){
            x.get(i).add(x.get(i).get(4)*x.get(i).get(5));
        }
        Collections.sort(x, new Comparator<ArrayList<Float>>() {
            @Override
            public int compare(ArrayList<Float> floats, ArrayList<Float> t1) {
                return t1.get(6).compareTo(floats.get(6));
            }
        });
        int i=1;
        while(i<x.size()){
            for(int j=0; j<i;j++){
                Point l1 = new Point(x.get(i).get(0) , x.get(i).get(1));
                Point r1 = new Point(x.get(i).get(2) , x.get(i).get(3));
                Point l2 = new Point(x.get(j).get(0) , x.get(j).get(1));
                Point r2 = new Point(x.get(j).get(2) , x.get(j).get(3));
                if (overlappingArea(l1, r1, l2, r2) > NMS_T ){
                    x.remove(i);
                    i--;
                    break;
                }


            }
            i++;
        }

        return x;
    }


    static class Point
    {
        float x, y;

        public Point(float x, float y)
        {
            this.x = x;
            this.y = y;
        }
    };

    // Returns Total Area of two overlap
// rectangles
    static float overlappingArea(Point l1, Point r1,
                               Point l2, Point r2)
    {
        // Area of 1st Rectangle
        float area1 = Math.abs(l1.x - r1.x) *
                Math.abs(l1.y - r1.y);

        // Area of 2nd Rectangle
        float area2 = Math.abs(l2.x - r2.x) *
                Math.abs(l2.y - r2.y);

        // Length of intersecting part i.e
        // start from max(l1.x, l2.x) of
        // x-coordinate and end at min(r1.x,
        // r2.x) x-coordinate by subtracting
        // start from end we get required
        // lengths
        float areaI = (Math.min(r1.x, r2.x) -
                Math.max(l1.x, l2.x)) *
                (Math.min(r1.y, r2.y) -
                        Math.max(l1.y, l2.y));
        if (areaI == area1 || areaI == area2){
            return 1;
        }

        return (areaI)/(area1 + area2 - areaI);
    }

}
