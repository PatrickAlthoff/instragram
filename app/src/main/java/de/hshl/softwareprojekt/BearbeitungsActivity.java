package de.hshl.softwareprojekt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BearbeitungsActivity extends AppCompatActivity implements View.OnClickListener {
    private int IMAGE_FROM_CROP = 1;
    private ImageView imageView;
    private Uri imageUri;
    private Bitmap bearbeitungsBitmap;
    final int PIC_CROP = 1;
    private EditText editT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bearbeitungs);
        Intent intentG = getIntent();

        Button scaleBtn = findViewById(R.id.scaleBtn);
        Button grayBtn = findViewById(R.id.bwBtn);
        Button sendBtn = findViewById(R.id.sendBtn);
        scaleBtn.setOnClickListener(this);
        grayBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

        this.imageView = findViewById(R.id.imageView2);
        this.bearbeitungsBitmap = intentG.getParcelableExtra("BitmapImage");
        this.imageView.setImageBitmap(this.bearbeitungsBitmap);
        this.editT = findViewById(R.id.editTitel);

        TextWatcher test = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        this.editT.addTextChangedListener(test);


    }
    //Verpackt die übergebene Bitmap in eine Uri
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //Enthält die Funktionen der Buttons
    @Override
    public void onClick(View v){
        switch (v.getId()){
            //Ruft beim Klick auf den Button die Skalierung auf
            case R.id.scaleBtn:
                this.imageUri = getImageUri(this, this.bearbeitungsBitmap);
                cropImage(this.imageUri);

                break;
            //Ruft beim Klick auf den Button die B/W Funktion auf
            case R.id.bwBtn:
                this.bearbeitungsBitmap = changeToGreyscale(this.bearbeitungsBitmap);
                this.imageView.setImageBitmap(this.bearbeitungsBitmap);
                break;
            //Ruft beim Klick auf den Button das SendBackIntent auf
            case R.id.sendBtn:
                Intent sendBackIntent = new Intent (BearbeitungsActivity.this, MainActivity.class);
                sendBackIntent.putExtra("BitmapImage", this.bearbeitungsBitmap);
                String sendTitel = this.editT.getText().toString();
                sendBackIntent.putExtra("Titel", sendTitel);
                setResult(RESULT_OK, sendBackIntent);
                finish();
                break;
        }
    }
    //Enthält die Funktion, welche die "Croppen"-Funktion für eine Uri aufruft
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if(requestCode == PIC_CROP){
                Uri uri = data.getData();
                this.bearbeitungsBitmap = getAndScaleBitmap(uri, -1,300);
                this.imageView.setImageBitmap(this.bearbeitungsBitmap);
            }
        }
    }
    //Enthält die Funktionen zum Skalieren einer Bitmap
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
    //Enthält die Funktion für den Schwarz/Weiß Filter
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
    //Enthält die Funktion zum "Croppen" einer übergebenen Uri
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
