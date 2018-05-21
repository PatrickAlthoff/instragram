package de.hshl.softwareprojekt;

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

import java.io.IOException;

public class BearbeitungsActivity extends AppCompatActivity implements View.OnClickListener {
   private int IMAGE_FROM_CROP = 1;
    private ImageView imageView;
    private Uri imageUri;
    private Bitmap bitty;

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

    private Bitmap getAndScaleBitmap(Bitmap bitmap, int dstWidth, int dstHeight){


            float   srcWidth = bitmap.getWidth(),
                    srcHeight = bitmap.getHeight();

            if (dstWidth < 1) {
                dstWidth = (int) (srcWidth / srcHeight * dstHeight);
            }
            Bitmap dst = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
            return dst;


    }

    @Override
    public void onClick(View v){
        Uri imageUri = this.imageUri;

        switch (v.getId()){
            case R.id.scaleBtn:
                cropImage(this.imageUri);
                break;
            case R.id.bwBtn:
                this.imageView.setImageBitmap(changeToGreyscale(getAndScaleBitmap(this.bitty, -1,300)));
                break;
            case R.id.sendBtn:
                Bitmap myBitmap = getAndScaleBitmap(this.bitty,-1,300);
                Intent sendBackImage = new Intent (BearbeitungsActivity.this, MainActivity.class);
                sendBackImage.putExtra("BitmapImage", myBitmap);
                setResult(RESULT_OK, sendBackImage);
                finish();
                break;

        }

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
