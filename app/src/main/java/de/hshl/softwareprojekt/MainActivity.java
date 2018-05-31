package de.hshl.softwareprojekt;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //Globale Variablen zur Request Identifizierung
    private boolean permissionGranted;
    private static final int PERMISSION_REQUEST = 1;
    private int BEARBEITUNG_CODE = 12;
    private int IMAGE_CAPTURE = 4;
    private int GALLERY_PICK = 5;
    private int REQUEST_GETSEND = 12;
    private int IMAGE_CLICKED = 13;

    //Globale Variablen zur Beschreibung der Bilder
    private int TITLE = 2;
    private int DESCRIPTION = 3;
    private Uri imageUri;

    //Globale Variablen für die Layouts
    float dpi;
    Intent intentCaptureImage;
    LinearLayout innerLayout;
    ConstraintSet constraintSet;
    Bitmap clickedImage;
    ImageView profilBild;
    TextView profilName;

    //Methode um die Display Auflösung zu erhalten
    private void getDisplayMetrics(){
        DisplayMetrics dm = new DisplayMetrics();
        MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.dpi = getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check for permissions and Display Metrics
        checkPermission();
        getDisplayMetrics();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialisierung des floating Camera Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
            }
        });

        //Initialisierung des Drawer Layout
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Initialisierung des NavigationView + Listener
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Initialisierung des InnerLayout (Linear Layout)
        this.innerLayout = findViewById(R.id.innerLayout);

        this.profilBild = findViewById(R.id.profilBild);
        this.profilBild.setOnClickListener(this);

        this.profilName = findViewById(R.id.profilName);


    }
    //Fügt der Frontpage ein individuelles Post Fragment hinzu
    public void addFragment(Bitmap postBitmap, String titel){

        //Initialisiert den FragmentManager, das PostFragment und das FrameLayout
        FragmentManager fragmentManager = getSupportFragmentManager();
        PostFragment frontPagePost = new PostFragment();
        FrameLayout frameInner = new FrameLayout(this);
        frameInner.setId(View.generateViewId());
        innerLayout.addView(frameInner, 0);

        //add fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(frameInner.getId(), frontPagePost);
        fragmentTransaction.commitNow();
        frontPagePost.addImage(postBitmap, titel);

        //Gib den ImageViews eine generierte ID und fügt einen OnClick Listener hinzu
        ImageView postImage = frontPagePost.postImage;
        postImage.setId(View.generateViewId());
        postImage.setOnClickListener(this);

    }

    //Methode zur Umrechnung der dpi
    private int dp2px(int dp){
        return (int)(dp*dpi);
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

    //Methode zum Skallieren der zu übergebenen Bitmap
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
    public Bitmap scaleBitmap(Bitmap bitmap, int dstWidth, int dstHeight){

        float   srcWidth = bitmap.getWidth(),
                srcHeight = bitmap.getHeight();

        if (dstWidth < 1) {
            dstWidth = (int) (srcWidth / srcHeight * dstHeight);
        }
        Bitmap dst = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);

        return dst;
    }

    //onActivityResult Methode zur Verarbeitung mehrerer Requests
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Verarbeitung der Image Capture Request
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    Bitmap myBitmap = getAndScaleBitmap(this.imageUri, -1, 300);
                    Intent sendToBearbeitung = new Intent (MainActivity.this, BearbeitungsActivity.class);
                    sendToBearbeitung.putExtra("BitmapImage", myBitmap);
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
                    Bitmap myBitmap = getAndScaleBitmap(uri, -1, 300);
                    Intent sendToBearbeitung = new Intent (MainActivity.this, BearbeitungsActivity.class);
                    sendToBearbeitung.putExtra("BitmapImage", myBitmap);
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

                    addFragment(postImage, titel);
                }
            }
            else {
                Log.d(MainActivity.class.getSimpleName(),"no picture selected");
            }
        }
    }
    //Schließt bei einem Backpress das DrawerLayout, falls dies offen ist
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Generiert den Inhalt des DrawerLayout aus der main.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //Verarbeitung der Settings Anfrage (noch passiert nichts)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Methode zur Verarbeitung der Buttons im  Drawer Menü
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startCamera();

        } else if (id == R.id.nav_gallery) {
            Intent intentGallerie = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentGallerie, GALLERY_PICK);

        } else if (id == R.id.nav_slideshow) {

        }
        //Startet das Settingslayout
        else if (id == R.id.nav_manage) {
                Intent intentSetting = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSetting);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        //Shortcut to Logout.
        else if (id == R.id.nav_logout) {
            Intent intentLogout = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentLogout);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Enthält die onClick Methode, für die individuellen Posts
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profilBild) {
            Intent intentProfil = new Intent(MainActivity.this, ProfilActivity.class);
            startActivity(intentProfil);

        }
        else{
        //Baut aus den Daten im Cache eine Bitmap
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        Bitmap parseBit = v.getDrawingCache();

        //Skaliert die Oben gebaute Bitmap auf ein kleineres Format
        Bitmap createBit = scaleBitmap(parseBit,-1,300);

        //Fügt dem Intent für die Vollansicht die Bitmap + einen Titel hinzu
        Intent intentVollansicht = new Intent(MainActivity.this, Main_Image_Clicked.class);
        intentVollansicht.putExtra("BitmapImage", createBit);
        intentVollansicht.putExtra("Titel", v.getContentDescription());
        startActivityForResult(intentVollansicht, IMAGE_CLICKED);}
    }
    //Enthält Methode zur Convertierung einer Bitmap in eine Uri
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}