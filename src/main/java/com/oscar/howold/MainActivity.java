package com.oscar.howold;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_CODE = 0X110;
    private ImageView mIvPhoto;
    private Button mBtnGetImage;
    private Button mBtnDetect;
    private TextView mTvTip;

    private String mCurrentPhotoStr;

    private Bitmap mPhotoImg;



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

                break;
        }
    }
}
