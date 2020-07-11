package com.technologies.cardamage;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import org.pytorch.IValue;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.util.ArrayList;

public class Model extends AsyncTask<Bitmap, Integer, ArrayList<ArrayList<Float>>> {

    @Override
    protected ArrayList<ArrayList<Float>> doInBackground(Bitmap... bitmaps) {
        Bitmap photo = bitmaps[0];
        Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(photo,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);
        Tensor outputTensor = Detection.Darknet.forward(IValue.from(inputTensor)).toTensor();
        float[] scores = outputTensor.getDataAsFloatArray();
        ArrayList <ArrayList<Float>> output1 = new ArrayList<>();
        for(int k=0 ; k<22743 ; k++){
            ArrayList <Float> temp = new ArrayList<>();
            for (int j=0; j<6 ; j++){
                temp.add(scores[k*6+j]);
            }
            output1.add(temp);
        }
        return output1;
    }


}
