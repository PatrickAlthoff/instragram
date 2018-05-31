package de.hshl.softwareprojekt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BearbeitungsActivity extends AppCompatActivity implements View.OnClickListener {
    private int IMAGE_FROM_CROP = 1;
    private ImageView bigImage;
    private ImageView miniImage1;
    private ImageView miniImage2;
    private ImageView miniImage3;
    private ImageView miniImage4;
    private ImageView miniImage5;
    private ImageView miniImage6;
    private ImageView miniImage7;
    private Uri imageUri;
    private Bitmap bearbeitungsBitmap;
    private Bitmap normalImage;
    final int PIC_CROP = 1;
    private EditText editT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bearbeitungs);
        Intent intentG = getIntent();

        Button scaleBtn = findViewById(R.id.scaleBtn);
        Button sendBtn = findViewById(R.id.sendBtn);
        scaleBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

        this.bigImage = findViewById(R.id.imageView2);

        this.bearbeitungsBitmap = intentG.getParcelableExtra("BitmapImage");
        this.normalImage = this.bearbeitungsBitmap;
        this.bigImage.setImageBitmap(this.bearbeitungsBitmap);
        this.editT = findViewById(R.id.editTitel);

        this.miniImage1 = findViewById(R.id.miniImage1);
        this.miniImage1.setImageBitmap(this.normalImage);
        this.miniImage1.setOnClickListener(this);

        this.miniImage2 = findViewById(R.id.miniImage2);
        this.miniImage2.setImageBitmap(changeToGreyscale(this.bearbeitungsBitmap));
        this.miniImage2.setOnClickListener(this);

        this.miniImage3 = findViewById(R.id.miniImage3);
        this.miniImage3.setImageBitmap(higherSaturation(this.bearbeitungsBitmap));
        this.miniImage3.setOnClickListener(this);

        this.miniImage4 = findViewById(R.id.miniImage4);
        this.miniImage4.setImageBitmap(higherContrast(this.bearbeitungsBitmap,50));
        this.miniImage4.setOnClickListener(this);

        this.miniImage5 = findViewById(R.id.miniImage5);
        this.miniImage5.setImageBitmap(invertEffect(this.bearbeitungsBitmap));
        this.miniImage5.setOnClickListener(this);

        this.miniImage6 = findViewById(R.id.miniImage6);
        this.miniImage6.setImageBitmap(applySepiaToningEffect(this.bearbeitungsBitmap,25,3,1,2));
        this.miniImage6.setOnClickListener(this);

        this.miniImage7 = findViewById(R.id.miniImage7);
        this.miniImage7.setImageBitmap(invertEffect(applySepiaToningEffect(this.bearbeitungsBitmap,25,3,1,2)));
        this.miniImage7.setOnClickListener(this);

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

        Toolbar toolbar = findViewById(R.id.toolbar3);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);


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
            //Ruft beim Klick auf den Button das SendBackIntent auf
            case R.id.sendBtn:
                Intent sendBackIntent = new Intent (BearbeitungsActivity.this, MainActivity.class);
                sendBackIntent.putExtra("BitmapImage", this.bearbeitungsBitmap);
                String sendTitel = this.editT.getText().toString();
                sendBackIntent.putExtra("Titel", sendTitel);
                setResult(RESULT_OK, sendBackIntent);
                finish();
                break;
                //Normales Bild
            case R.id.miniImage1:
                this.bearbeitungsBitmap = this.normalImage;
                this.bigImage.setImageBitmap(this.bearbeitungsBitmap);
                break;
                //Schwarz/Weiß Effekt
            case R.id.miniImage2:
                this.bearbeitungsBitmap = changeToGreyscale(this.bearbeitungsBitmap);
                this.bigImage.setImageBitmap(this.bearbeitungsBitmap);
                break;
                //Erhöhte Saturation
            case R.id.miniImage3:
                this.bearbeitungsBitmap = higherSaturation(this.bearbeitungsBitmap);
                this.bigImage.setImageBitmap(this.bearbeitungsBitmap);
                break;
                //Erhöhter Konstrast
            case R.id.miniImage4:
                this.bearbeitungsBitmap = higherContrast(this.bearbeitungsBitmap, 50);
                this.bigImage.setImageBitmap(this.bearbeitungsBitmap);
                break;
                //Invert Effekt
            case R.id.miniImage5:
                this.bearbeitungsBitmap = invertEffect(this.bearbeitungsBitmap);
                this.bigImage.setImageBitmap(this.bearbeitungsBitmap);
                break;
                //Rosa Effekt
            case R.id.miniImage6:
                this.bearbeitungsBitmap = applySepiaToningEffect(this.bearbeitungsBitmap,25,3,1,2);
                this.bigImage.setImageBitmap(this.bearbeitungsBitmap);
                break;
                //Nightmode Effekt
            case R.id.miniImage7:
                this.bearbeitungsBitmap = invertEffect(applySepiaToningEffect(this.bearbeitungsBitmap,25,3,1,2));
                this.bigImage.setImageBitmap(this.bearbeitungsBitmap);
                break;

        }
    }
    //Enthält die Funktion, welche die "Croppen"-Funktion für eine Uri aufruft
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if(requestCode == PIC_CROP){
                Uri uri = data.getData();
                this.bearbeitungsBitmap = getAndScaleBitmap(uri, -1,300);
                this.bigImage.setImageBitmap(this.bearbeitungsBitmap);
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

    private Bitmap higherSaturation(Bitmap src){

        int width = src.getWidth(), height = src.getHeight();

        Bitmap dst = src.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(dst);
        Paint paint = new Paint();

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(2);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);

        canvas.drawBitmap(src, 0, 0,paint);

        return dst;
    }

    private Bitmap higherContrast(Bitmap src, double value){

        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.red(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.red(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }



    private Bitmap invertEffect(Bitmap src){

        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        int A, R, G, B;
        int pixelColor;

        int height = src.getHeight();
        int width = src.getWidth();


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                pixelColor = src.getPixel(x, y);

                A = Color.alpha(pixelColor);

                R = 255 - Color.red(pixelColor);
                G = 255 - Color.green(pixelColor);
                B = 255 - Color.blue(pixelColor);

                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final bitmap
        return bmOut;
    }

    public  Bitmap applySepiaToningEffect(Bitmap src, int depth, double red, double green, double blue) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // constant grayscale
        final double GS_RED = 0.3;
        final double GS_GREEN = 0.59;
        final double GS_BLUE = 0.11;
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                // get color on each channel
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                // apply grayscale sample
                B = G = R = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);

                // apply intensity level for sepid-toning on each channel
                R += (depth * red);
                if(R > 255) { R = 255; }

                G += (depth * green);
                if(G > 255) { G = 255; }

                B += (depth * blue);
                if(B > 255) { B = 255; }

                // set new pixel color to output image
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent sendBackIntent = new Intent (BearbeitungsActivity.this, MainActivity.class);
            setResult(RESULT_CANCELED, sendBackIntent);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}