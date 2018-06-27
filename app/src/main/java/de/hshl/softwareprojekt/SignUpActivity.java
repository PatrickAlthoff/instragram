package de.hshl.softwareprojekt;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse {

    private boolean permissionGranted;
    private static final int PERMISSION_REQUEST = 1;
    private int TITLE = 2;
    private int DESCRIPTION = 3;
    private int IMAGE_CAPTURE = 4;
    private int GALLERY_PICK = 5;
    private Uri imageUri;
    private Intent intentCaptureImage;
    private DatabaseHelperUser userDatabase;
    private Button regButton;
    private EditText userNameField;
    private EditText pwField;
    private EditText emailField;
    private ImageView profilPic;
    private Bitmap profilBitmap;
    private String response;
    private String base64;
    public HttpConnection httpConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        checkPermission();
        this.userDatabase = new DatabaseHelperUser(this);
        this.regButton = findViewById(R.id.regButton);
        this.userNameField = findViewById(R.id.userNameField);
        this.pwField = findViewById(R.id.passwordField);
        this.emailField = findViewById(R.id.emailField);
        this.profilPic = findViewById(R.id.imgView_logo);

        this.regButton.setOnClickListener(this);
        this.profilPic.setOnClickListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar9);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.regButton:
                String username = this.userNameField.getText().toString();
                String pw = this.pwField.getText().toString();
                String email = this.emailField.getText().toString();

                if(username != null && pw != null && email != null && this.profilBitmap != null) {
                    this.base64 = ImageHelper.bitmapToBase64(this.profilBitmap);
                    sendXML(username,email,pw,base64);
                }
                else{
                    Log.e("Fields", "emtpy");
                }
                break;
            case R.id.imgView_logo:
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Profilbild wählen!");
                builder.setMessage("Möchten sie ein neues Bild aufnehmen oder eines aus der Gallerie wählen?");
                builder.setCancelable(true);
                builder.setPositiveButton("Foto aufnehmen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startCamera();
                        Toast.makeText(getApplicationContext(), "Profilbild aktualisiert", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Gallerie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentGallerie = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intentGallerie, GALLERY_PICK);
                        Toast.makeText(getApplicationContext(), "Profilbild aktualisiert", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                break;
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendXML(String username, String email, String password, String base64){
        String dstAdress = "http://intranet-secure.de/instragram/Upload_User.php";
        httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.uploadUser( username,  email,  password, base64));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    @Override
    public void processFinish(String output) {
        this.response = output;
        String username = this.userNameField.getText().toString();
        String pw = this.pwField.getText().toString();
        String email = this.emailField.getText().toString();
        Intent startLogin = new Intent(SignUpActivity.this, LoginActivity.class);
        startLogin.putExtra("email", email);
        startLogin.putExtra("pw", pw);
        if(this.response.contains("Not_ok") ){
            this.emailField.setError("Email schon vergeben!");
        }
        else if(this.response.contains("Is_Ok")){

            String[] idPiece = this.response.split(":");
            String idPiec = idPiece[0];
            long id = Long.parseLong(idPiec);
            this.userDatabase.insertData(id,username, email, pw, base64,false);
            setResult(RESULT_OK, startLogin);
            finish();
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
                }
                else  {
                    this.permissionGranted = false;
                }
                return;
        }
    }

    //onActivityResult Methode zur Verarbeitung mehrerer Requests
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Verarbeitung der Image Capture Request
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    this.profilBitmap = getAndScaleBitmap(this.imageUri, 80, 80);
                    this.profilPic.setImageBitmap(this.profilBitmap);
                }
            } else {
                int rowsDeleted = getContentResolver().delete(imageUri, null, null);
                Log.d(MainActivity.class.getSimpleName(), rowsDeleted + " rows deleted");
                //startCamera();
            }
        }

        //Verarbeitung der Gallerie Request
        if (requestCode == GALLERY_PICK) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    this.profilBitmap = getAndScaleBitmap(uri, 80, 80);
                    this.profilPic.setImageBitmap(this.profilBitmap);
            }
            } else {
                Log.d(MainActivity.class.getSimpleName(), "no picture selected");
            }
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
    }
