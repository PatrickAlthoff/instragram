package de.hshl.softwareprojekt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class BearbeitungsActivity extends AppCompatActivity implements View.OnClickListener {
   private int IMAGE_FROM_CROP = 1;
    private ImageView imageView;
    private Uri imageUri;
    private Bitmap bitty;
    final int PIC_CROP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bearbeitungs);
        Button scaleBtn = findViewById(R.id.scaleBtn);
        Button grayBtn = findViewById(R.id.bwBtn);
        Button sendBtn = findViewById(R.id.sendBtn);
        this.imageView = findViewById(R.id.imageView2);
        Intent intentG = getIntent();
        this.bitty = intentG.getParcelableExtra("BitmapImage");
        grayBtn.setOnClickListener(this);
        scaleBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        this.imageView.setImageBitmap(this.bitty);

    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.scaleBtn:
                this.imageUri = getImageUri(this, this.bitty);
                cropImage(this.imageUri);

                break;
            case R.id.bwBtn:
                this.bitty = changeToGreyscale(this.bitty);
                this.imageView.setImageBitmap(this.bitty);
                break;
            case R.id.sendBtn:
                Intent sendBackImage = new Intent (BearbeitungsActivity.this, MainActivity.class);
                sendBackImage.putExtra("BitmapImage", this.bitty);
                setResult(RESULT_OK, sendBackImage);
                finish();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if(requestCode == PIC_CROP){
                Uri uri = data.getData();
                this.bitty = getAndScaleBitmap(uri, -1,300);
                this.imageView.setImageBitmap(this.bitty);
            }
        }
    }

    private Bitmap getAndScaleBitmap(Uri uri, int dstWidth, int dstHeight){
        try {
            Bitmap src = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

            float   srcWidth = src.getWidth(),
                    srcHeight = src.getHeight();

            if (dstWidth < 1) {
                dstWidth = (int) (srcWidth / srcHeight * dstHeight);
            }
            Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
            return dst;
        }
        catch (IOException e) {
            Log.e(MainActivity.class.getSimpleName(), "setBitmap", e);
        }
        return null;
    }

    private Bitmap changeToGreyscale(Bitmap src){
        int width = src.getWidth(), height = src.getHeight();

        Bitmap dst = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(dst);
        Paint paint = new Paint();

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);

        canvas.drawBitmap(src, 0 ,0,paint);
        return  dst;
    }

    private void cropImage(Uri uri) {
        try{

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(uri, "image/jpeg");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, IMAGE_FROM_CROP);
        }
        catch(Exception e){
            Log.e(BearbeitungsActivity.class.getSimpleName(), "cropImage()", e);
        }
    }


}
