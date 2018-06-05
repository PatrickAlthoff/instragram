package de.hshl.softwareprojekt;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class StoriesBearbeitungsActivity extends AppCompatActivity implements View.OnClickListener {
    Button videoBtn;
    Button fotoBtn;
    Button galerieBtn;
    Button restartbtn;
    Button publishBtn;

    private boolean permissionGranted;
    private static final int PERMISSION_REQUEST = 1;

    private int BEARBEITUNG_CODE = 12;
    private int IMAGE_CAPTURE = 4;
    private int GALLERY_PICK = 5;
    private int REQUEST_GETSEND = 12;

    private int TITLE = 2;
    private int DESCRIPTION = 3;
    private Uri imageUri;


    ArrayList<Bitmap> bitmapList;
    ArrayList<Uri> uriList;
    ConstraintLayout storieBearCons;
    ConstraintLayout storieFrame;
    Intent intentCaptureImage;
    ProgressBar storiebar;
    int progressBarCount;
    ImageView storiePic;
    int i;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_bearbeitungs);

        //Check for permissions
        checkPermission();

        this.bitmapList = new ArrayList<>();
        this.uriList = new ArrayList<>();
        Intent data = getIntent();
        this.uriList = data.getParcelableArrayListExtra("UriList");
        this.bitmapList = convertToBitmapList(this.uriList);
        this.storieBearCons = findViewById(R.id.storieBearCons);

        this.videoBtn = findViewById(R.id.videoEdit);
        this.fotoBtn = findViewById(R.id.fotoEdit);
        this.galerieBtn = findViewById(R.id.galerieEdit);
        this.restartbtn = findViewById(R.id.restartBtn);
        this.publishBtn = findViewById(R.id.publishBtn);

        this.videoBtn.setOnClickListener(this);
        this.fotoBtn.setOnClickListener(this);
        this.galerieBtn.setOnClickListener(this);
        this.restartbtn.setOnClickListener(this);
        this.publishBtn.setOnClickListener(this);

        this.storiePic = findViewById(R.id.storieConent);
        this.storiebar = findViewById(R.id.storieProgress);

        if(this.uriList.size() > 0) {
            this.storiePic.setImageBitmap(getAndScaleBitmapNormal(this.uriList.get(0), -1, 330));
        }

        Toolbar toolbar = findViewById(R.id.toolbar4);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public ArrayList<Bitmap> convertToBitmapList(ArrayList<Uri> uriList){

        ArrayList<Bitmap> bitmapList =new ArrayList<>();
        int length = uriList.size();
        int i = 0;
        Bitmap bitmap;
        while (length > i){
            bitmap = getAndScaleBitmapNormal(uriList.get(i),-1,330);
            bitmapList.add(bitmap);
            i++;

        }
        return bitmapList;
    }

    public ArrayList<Uri> convertToUriList(ArrayList<Bitmap> bitmapList){

        ArrayList<Uri> uriList =new ArrayList<>();
        int length = bitmapList.size();
        int i = 0;
        Uri uri;
        while (length > i){
            uri = getImageUri(this,bitmapList.get(i));
            uriList.add(uri);
            i++;

        }
        return uriList;
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.videoEdit:

                break;
            case R.id.fotoEdit:
                startCamera();
                break;
            case R.id.galerieEdit:
                Intent intentGallerie = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentGallerie, GALLERY_PICK);
                break;
            case R.id.restartBtn:
                startBar();
                break;
            case R.id.publishBtn:
                Intent sendBackIntent = new Intent (StoriesBearbeitungsActivity.this, Main_Storie_Clicked.class);
                sendBackIntent.putParcelableArrayListExtra("UriList", convertToUriList(this.bitmapList));
                setResult(RESULT_OK, sendBackIntent);
                finish();
                break;
        }
    }

    //Methode zum Start der Camera
    private void startCamera(){
        if (this.permissionGranted){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.TITLE, TITLE);
            contentValues.put(MediaStore.Images.Media.DESCRIPTION, DESCRIPTION );
            contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intentCaptureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentCaptureImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intentCaptureImage, IMAGE_CAPTURE);
        }
    }

    //Fügt der Frontpage ein individuelles Post Fragment hinzu
    public void addStorieContent(Bitmap storieBitmap, String titel) {

        this.bitmapList.add(storieBitmap);
        //Gib den ImageViews eine generierte ID und fügt einen OnClick Listener hinzu
        this.storiePic.setImageBitmap(bitmapList.get(0));

        this.restartbtn.setText("Start");
        this.storiebar.setProgress(0);

    }

    public void startBar(){
        this.progressBarCount = this.bitmapList.size();
        this.i = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(i < progressBarCount){


                    storiePic.post(new Runnable() {
                        @Override
                        public void run() {
                            storiePic.setImageBitmap(bitmapList.get(i));
                            i++;
                        }
                    });

                    storiebar.post(new Runnable() {
                        @Override
                        public void run() {
                            storiebar.setProgress((i)* 100 / progressBarCount);

                        }
                    });
                    SystemClock.sleep(2000);

                }
            }
        }).start();

        this.restartbtn.setText("Restart");
    }


    //Methode zum Skallieren der zu übergebenen Bitmap
    private Bitmap getAndScaleBitmapNormal(Uri uri, int dstWidth, int dstHeight){
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

    //onActivityResult Methode zur Verarbeitung mehrerer Requests
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Verarbeitung der Image Capture Request
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    Bitmap myBitmap = getAndScaleBitmapNormal(this.imageUri, -1, 300);
                    Intent sendToBearbeitung = new Intent (StoriesBearbeitungsActivity.this, PostBearbeitungsActivity.class);
                    sendToBearbeitung.putExtra("BitmapImage", myBitmap);
                    int code = 2;
                    sendToBearbeitung.putExtra("Code", code);
                    startActivityForResult(sendToBearbeitung, BEARBEITUNG_CODE);
                }
            }
            else {
                int rowsDeleted = getContentResolver().delete(imageUri, null, null);
                Log.d(MainActivity.class.getSimpleName(), rowsDeleted + " rows deleted");
                //startCamera();
            }
        }

        //Verarbeitung der Gallerie Request
        if(requestCode == GALLERY_PICK){
            if(resultCode == RESULT_OK){
                if(data != null) {
                    Uri uri = data.getData();
                    Bitmap myBitmap = getAndScaleBitmapNormal(uri, -1, 300);
                    Intent sendToBearbeitung = new Intent (StoriesBearbeitungsActivity.this, PostBearbeitungsActivity.class);
                    sendToBearbeitung.putExtra("BitmapImage", myBitmap);
                    int code = 2;
                    sendToBearbeitung.putExtra("Code", code);
                    startActivityForResult(sendToBearbeitung, BEARBEITUNG_CODE);
                }
            }
            else{
                Log.d(MainActivity.class.getSimpleName(),"no picture selected");
            }
        }

        //Verarbeitung der Bearbeitungs Request
        if (requestCode == REQUEST_GETSEND) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Intent intentVerarbeitet = data;
                    Bitmap postImage;
                    String titel;
                    titel = intentVerarbeitet.getStringExtra("Titel");
                    postImage = intentVerarbeitet.getParcelableExtra("BitmapImage");
                    addStorieContent(postImage,titel);

                }
            }
            else {
                Log.d(MainActivity.class.getSimpleName(),"no picture selected");
            }
        }
    }

    //Öffnet Fenster zur Bestätigung der Zugriffsrechte / Prüft ob dies schon geschehen ist
    protected void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{   Manifest.permission.WRITE_EXTERNAL_STORAGE}, this.PERMISSION_REQUEST);
            }
        }
        else {
            this.permissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){

        switch (requestCode) {
            case PERMISSION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.permissionGranted = true;
                    startCamera();
                }
                else  {
                    this.permissionGranted = false;
                }
                return;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent sendBackIntent = new Intent (StoriesBearbeitungsActivity.this, MainActivity.class);
            setResult(RESULT_CANCELED, sendBackIntent);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}
