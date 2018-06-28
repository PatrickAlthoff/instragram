package de.hshl.softwareprojekt;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class Stories_BearbeitungsActivity extends AppCompatActivity implements View.OnClickListener {
    //Variablen zur Verarbeitung der Inhalte in der Activity
    private boolean permissionGranted;
    private static final int PERMISSION_REQUEST = 1;
    private int BEARBEITUNG_CODE = 12;
    private int IMAGE_CAPTURE = 4;
    private int GALLERY_PICK = 5;
    private int REQUEST_GETSEND = 12;
    private int TITLE = 2;
    private int DESCRIPTION = 3;
    private int i;
    private int progressBarCount;
    private int id;
    private User user;
    private Uri imageUri;
    private Button fotoBtn;
    private Button galerieBtn;
    private Button restartbtn;
    private Button publishBtn;
    private ArrayList<Bitmap> bitmapList;
    private ArrayList<String> titelList;
    private ImageView storiePic;
    private TextView storieTitel;
    private ProgressBar storiebar;
    private Intent intentCaptureImage;
    private DatabaseHelperPosts database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_bearbeitungs);

        checkPermission();
        Intent data = getIntent();
        this.titelList = new ArrayList<>();
        this.bitmapList = new ArrayList<>();
        this.database = new DatabaseHelperPosts(this);
        this.user = (User) data.getSerializableExtra("User");
        this.id = data.getIntExtra("id",0);

        this.fotoBtn = findViewById(R.id.fotoEdit);
        this.galerieBtn = findViewById(R.id.galerieEdit);
        this.restartbtn = findViewById(R.id.restartBtn);
        this.publishBtn = findViewById(R.id.publishBtn);
        this.storieTitel = findViewById(R.id.storieTitel2);
        this.storiePic = findViewById(R.id.storieConent);
        this.storiebar = findViewById(R.id.storieProgress);

        this.fotoBtn.setOnClickListener(this);
        this.galerieBtn.setOnClickListener(this);
        this.restartbtn.setOnClickListener(this);
        this.publishBtn.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar4);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        storiePic.post(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> storyList = database.getStory(user.getId());

                if (storyList.size() == 0){
                    storiePic.setVisibility(View.VISIBLE);
                } else {

                    String story = storyList.get(0);
                    String[] pieces = story.split(" : ");
                    String ids = pieces[0];
                    int id = Integer.parseInt(ids);
                    String base64 = pieces[2];
                    String[] base64Strings = base64.split(":");
                    int i = 1;
                    ArrayList<Bitmap> base64Bitmap = new ArrayList<>();
                    while (i < base64Strings.length) {
                        base64Bitmap.add(ImageHelper.base64ToBitmap(base64Strings[i]));
                        i++;
                    }
                    String titel = pieces[3];
                    String[] titleStringsSplit = titel.split(":");
                    int d = 1;
                    ArrayList<String> titleStrings = new ArrayList<>();

                    while (d < titleStringsSplit.length) {
                        titleStrings.add(titleStringsSplit[d]);
                        d++;
                    }
                    bitmapList = base64Bitmap;
                    titelList = titleStrings;
                    storiePic.setImageBitmap(bitmapList.get(0));
                    storieTitel.setText(titelList.get(0));
                }
            }

        });

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {

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

                if(bitmapList.size() > 0 && titelList.size() > 0){
                    Intent sendBackIntent = new Intent (Stories_BearbeitungsActivity.this, Main_Story_Clicked.class);
                    setResult(RESULT_OK, sendBackIntent);
                    String base64 = convertBitMapListtoBase64(this.bitmapList);
                    String titel = convertStringListtoString(this.titelList);
                    int c = this.id;
                    if(c == 0){
                        String d = Long.toString(System.currentTimeMillis()/1000);
                        c = Integer.parseInt(d);

                    }

                    long storyID = System.currentTimeMillis()/1000;
                    ArrayList<String> storyList = database.getStory(user.getId());

                    if (storyList.size() == 0) {
                        database.insertStory(c, this.user.getUsername(), base64, titel, "", "", true, this.user.getId());

                    }
                    else{
                        database.updateStory(user.getId(),base64, titel);
                    }
                    uploadStory(storyID, user.getId(), titel, base64);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Ihre Story ist noch leer!", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }

    private void uploadStory(long id, long userKey, String titels, String base64){
        String dstAdress = "http://intranet-secure.de/instragram/uploadStory.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.uploadStory(id,userKey, titels, base64));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.execute();
    }

    public String convertStringListtoString (ArrayList<String> stringList){
        int i = 0;
        String titel = "";
        while(i<stringList.size()){
            titel= titel + ":" + stringList.get(i);
            i++;
        }
        return titel;
    }

    public String convertBitMapListtoBase64 (ArrayList<Bitmap> bitmapList){
        int i = 0;
        String base64 = "";
        while (i < bitmapList.size()){
            base64 = base64 + ":" +  ImageHelper.bitmapToBase64(bitmapList.get(i));
            i++;
        }
        return base64;
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

        this.titelList.add(titel);
        this.bitmapList.add(storieBitmap);
        //Gib den ImageViews eine generierte ID und fügt einen OnClick Listener hinzu
        this.storiePic.setImageBitmap(bitmapList.get(0));
        this.storieTitel.setText(titelList.get(0));
        this.restartbtn.setText("Start");
        this.storiebar.setProgress(0);

    }
    //Enthält die Methode zur Visualisierung der Progressbar
    public void startBar(){
        this.i = 0;
        this.progressBarCount = this.bitmapList.size();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(i < progressBarCount){


                    storiePic.post(new Runnable() {
                        @Override
                        public void run() {
                            storiePic.setImageBitmap(bitmapList.get(i));
                            storieTitel.setText(titelList.get(i));
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

    //Methode zum Skalieren der zu übergebenen Bitmap
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
                    int code = 2;
                    Bitmap myBitmap = getAndScaleBitmapNormal(this.imageUri, -1, 300);
                    Intent sendToBearbeitung = new Intent (Stories_BearbeitungsActivity.this, BearbeitungsActivity.class);
                    sendToBearbeitung.putExtra("BitmapImage", myBitmap);
                    sendToBearbeitung.putExtra("Code", code);
                    startActivityForResult(sendToBearbeitung, BEARBEITUNG_CODE);
                }
            }
            else {
                int rowsDeleted = getContentResolver().delete(imageUri, null, null);
                Log.d(MainActivity.class.getSimpleName(), rowsDeleted + " rows deleted");
            }
        }

        //Verarbeitung der Gallerie Request
        if(requestCode == GALLERY_PICK){
            if(resultCode == RESULT_OK){
                if(data != null) {
                    int code = 2;
                    Uri uri = data.getData();
                    Bitmap myBitmap = getAndScaleBitmapNormal(uri, -1, 300);
                    Intent sendToBearbeitung = new Intent (Stories_BearbeitungsActivity.this, BearbeitungsActivity.class);
                    sendToBearbeitung.putExtra("BitmapImage", myBitmap);
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

    //Kümmert sich um den Klick auf den BackButton
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent sendBackIntent = new Intent (Stories_BearbeitungsActivity.this, MainActivity.class);
            setResult(RESULT_CANCELED, sendBackIntent);
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

}
