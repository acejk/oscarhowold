package com.oscar.howold.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.oscar.howold.Constant;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2016/8/6 0006.
 */
public class FaceppDetect {
    public interface CallBack {
        void success(JSONObject jsonObject);
        void error(FaceppParseException e);
    }


    public static void detect(final Bitmap bitmap, final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpRequests requests = new HttpRequests(Constant.KEY, Constant.SECEATE, true, true);
                    Bitmap smallBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    smallBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                    byte[] arrays = baos.toByteArray();
                    PostParameters parameters = new PostParameters();
                    parameters.setImg(arrays);

                    JSONObject jsonObject = requests.detectionDetect(parameters);

                    Log.e("TAG", jsonObject.toString());

                    if(callBack != null) {
                        callBack.success(jsonObject);
                    }

                } catch (FaceppParseException e) {
                    e.printStackTrace();
                    if(callBack != null) {
                        callBack.error(e);
                    }
                }
            }
        }).start();
    }
}
