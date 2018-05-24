package de.hshl.softwareprojekt;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.ActivityChooserView;
import android.util.DisplayMetrics;
import android.util.EventLogTags;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //Globale Variablen zur Request Identifizierung
    private boolean permissionGranted;
    private static final int PERMISSION_REQUEST = 1;
    private int BEARBEITUNG_CODE = 12;
    private int IMAGE_CAPTURE = 4;
    private int GALLERY_PICK = 5;
    private int REQUEST_GETSEND = 12;

    //Globale Variablen zur Beschreibung der Bilder
    private int TITLE = 2;
    private int DESCRIPTION = 3;
    private Uri imageUri;

    //Globale Variablen zur Bildwiedergabe
    private ImageView imageView;
    private Bitmap bitty;


    //Globale Variablen für die Layouts
    float dpi;
    Intent intentC;
    LinearLayout innerLayout;
    ConstraintSet constraintSet;




    //Methode um die Display Auflösung zu erhalten
    private void getDisplayMetrics(){
        DisplayMetrics dm = new DisplayMetrics();
        MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.dpi = getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check for permissions and Display Metrics
        checkPermission();
        getDisplayMetrics();

        setContentView(R.layout.activity_main);
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

        //Initialisierung des Image View + InnerLayout (Linear Layout)
        this.imageView = findViewById(R.id.imageView);
        this.innerLayout = findViewById(R.id.innerLayout);

        //Initialisierung des Generator Button + Listener
        Button generatorBtn = findViewById(R.id.generatorBtn);
        generatorBtn.setOnClickListener(this);


    }

    //Methode zur Umrechnung der dpi
    private int dp2px(int dp){
        return (int)(dp*dpi);
    }

    //Erzeugt neue posts
    public void createNewPost(Bitmap postImage){


        ImageView testImage = new ImageView(this);
        testImage.setId(View.generateViewId());
        testImage.setImageBitmap(postImage);

        innerLayout.addView(testImage);

        //constraintSet.connect(test.getId(), ConstraintSet.TOP, testImage.getId(), constraintSet.TOP, dp2px(100));
    }

    //Öffnet Fenster zur Bestätigung der Zugriffsrechte
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
             intentC = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentC.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intentC, IMAGE_CAPTURE);
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
                    Intent intentG = data;
                    this.bitty = intentG.getParcelableExtra("BitmapImage");
                    createNewPost(this.bitty);
                }
            }
            else {
                int rowsDeleted = getContentResolver().delete(imageUri, null, null);
                Log.d(MainActivity.class.getSimpleName(), rowsDeleted + " rows deleted");
                //startCamera();
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


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
            Intent intentG = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentG, GALLERY_PICK);

        } else if (id == R.id.nav_slideshow) {

        }

        else if (id == R.id.nav_manage) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        //Shortcut to Logout.
        else if (id == R.id.nav_logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //On Click Methode für den Generator Button, Upload Button und Galerie Button
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.generatorBtn: {

                break;
            }

            }
        }








}