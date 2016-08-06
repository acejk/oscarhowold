package com.oscar.howold;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facepp.error.FaceppParseException;
import com.oscar.howold.utils.FaceppDetect;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int MSG_SUCCESS = 0x111;
    public static final int MSG_ERROR = 0X112;
    private static final int PICK_CODE = 0X110;
    private ImageView mIvPhoto;
    private Button mBtnGetImage;
    private Button mBtnDetect;
    private TextView mTvTip;
    private View mWaitting;

    private String mCurrentPhotoStr;

    private Bitmap mPhotoImg;



    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    mWaitting.setVisibility(View.GONE);
                    JSONObject jsonObject = (JSONObject) msg.obj;

                    preparedRsBitmap(jsonObject);

                    mIvPhoto.setImageBitmap(mPhotoImg);

                    break;
                case MSG_ERROR:
                    mWaitting.setVisibility(View.GONE);

                    String errorMsg = (String) msg.obj;
                    if(TextUtils.isEmpty(errorMsg)) {
                        mTvTip.setText("Error");
                    } else {
                        mTvTip.setText(errorMsg);
                    }
                    break;
            }

            super.handleMessage(msg);
        }
    };

    private void preparedRsBitmap(JSONObject jsonObject) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        initEvents();
    }

    private void initEvents() {
        mBtnGetImage.setOnClickListener(this);
        mBtnDetect.setOnClickListener(this);
    }

    private void initViews() {
        mIvPhoto = (ImageView) findViewById(R.id.iv_photo);
        mBtnDetect = (Button) findViewById(R.id.btn_detect);
        mBtnGetImage = (Button) findViewById(R.id.btn_getImage);
        mTvTip = (TextView) findViewById(R.id.tv_tip);
        mWaitting = findViewById(R.id.fl_waitting);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == PICK_CODE) {
            if(intent != null) {
                Uri uri = intent.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();

                int idIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                mCurrentPhotoStr = cursor.getString(idIndex);
                cursor.close();

                resizePhoto();//压缩图片

                mIvPhoto.setImageBitmap(mPhotoImg);
                mTvTip.setText("Click Detect ==>");

            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void resizePhoto() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoStr, options);

        double ratio = Math.max(options.outWidth * 1.0d / 1024f, options.outHeight * 1.0d / 1024f);
        options.inSampleSize = (int) Math.ceil(ratio);
        options.inJustDecodeBounds = false;
        mPhotoImg = BitmapFactory.decodeFile(mCurrentPhotoStr, options);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_getImage:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_CODE);
                break;
            case R.id.btn_detect:
                mWaitting.setVisibility(View.VISIBLE);

                FaceppDetect.detect(mPhotoImg, new FaceppDetect.CallBack() {
                    @Override
                    public void success(JSONObject jsonObject) {
                        Message msg = Message.obtain();
                        msg.what = MSG_SUCCESS;
                        msg.obj = jsonObject;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void error(FaceppParseException e) {
                        Message msg = Message.obtain();
                        msg.what = MSG_ERROR;
                        msg.obj = e.getErrorMessage();
                        mHandler.sendMessage(msg);
                    }
                });
                break;
        }
    }
}
