package de.hshl.softwareprojekt;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Profil_BearbeitungActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse {

    private int TITLE = 2;
    private int DESCRIPTION = 3;
    private int IMAGE_CAPTURE = 4;
    private int GALLERY_PICK = 5;
    private Uri imageUri;
    private ImageView profilbild;
    private Button save;
    private EditText benutzerName;
    private EditText Beschreibung;
    private DatabaseHelperPosts databaseHelperPosts;
    private DatabaseHelperUser databaseHelperUser;
    private User user;
    private String base64;
    private String oldbase;
    private String oldName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil__bearbeitung);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("User");
        getBio(user.getId());
        base64 = user.getBase64();
        oldbase = user.getBase64();
        oldName = user.getUsername();
        this.databaseHelperPosts = new DatabaseHelperPosts(this);
        this.databaseHelperUser = new DatabaseHelperUser(this);

        this.profilbild = findViewById(R.id.Profilbild);
        this.save = findViewById(R.id.speichern);
        this.benutzerName = findViewById(R.id.editName);
        this.Beschreibung = findViewById(R.id.editBiografie);
        this.profilbild.setImageDrawable(roundImage(ImageHelper.base64ToBitmap(user.getBase64())));
        this.benutzerName.setText(user.getUsername());

        this.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Änderungen bestätigen!");
                builder.setMessage("Sie sind dabei ihre Nutzerdaten zu ändern, sind sie sicher, dass sie fortfahren möchten?");
                builder.setCancelable(false);
                builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.setUsername(benutzerName.getText().toString());
                        user.setBase64(base64);
                        databaseHelperUser.updateUserPic(user.getId(),base64);
                        databaseHelperUser.updateUser(user.getId(), benutzerName.getText().toString());
                        databaseHelperPosts.updateUserPosts(user.getId(), benutzerName.getText().toString());
                        updateData(user.getId());
                        getAllPosts(user.getId());
                        Toast.makeText(getApplicationContext(), "Dein Nutzerdaten wurden erfolgreich geändert!", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        benutzerName.setText(user.getUsername());
                        Toast.makeText(getApplicationContext(), "Dein ursprünglicher Nutzerdaten wurden wiederhergestellt!", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();


            }
        });

        this.profilbild.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar12);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    }
    public RoundedBitmapDrawable roundImage(Bitmap bitmap){
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    //Methode zum Start der Camera
    private void startCamera(){

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.TITLE, TITLE);
            contentValues.put(MediaStore.Images.Media.DESCRIPTION, DESCRIPTION );
            contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            Intent intentCaptureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentCaptureImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intentCaptureImage, IMAGE_CAPTURE);
        }

    //onActivityResult Methode zur Verarbeitung mehrerer Requests
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Verarbeitung der Image Capture Request
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    Bitmap profilBitmap = getAndScaleBitmap(this.imageUri, 80, 80);
                    base64 = ImageHelper.bitmapToBase64(profilBitmap);
                    this.profilbild.setImageDrawable(roundImage(profilBitmap));
                }
            } else {
                int rowsDeleted = getContentResolver().delete(imageUri, null, null);
                Log.d(MainActivity.class.getSimpleName(), rowsDeleted + " rows deleted");
            }
        }

        //Verarbeitung der Gallerie Request
        if (requestCode == GALLERY_PICK) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    Bitmap profilBitmap = getAndScaleBitmap(uri, 80, 80);
                    base64 = ImageHelper.bitmapToBase64(profilBitmap);
                    this.profilbild.setImageDrawable(roundImage(profilBitmap));
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

    private void updateData(long id){
        String dstAdress = "http://intranet-secure.de/instragram/sendProfilData.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateProfilData(id, benutzerName.getText().toString(), Beschreibung.getText().toString() , base64));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.execute();
    }
    private void getAllPosts(long id){
        String dstAdress = "http://intranet-secure.de/instragram/getAllKommPosts.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getUsers(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }
    private void updateAllKomms(long id){
        String dstAdress = "http://intranet-secure.de/instragram/updateKommentData.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateAllKomms(id, user.getUsername(), user.getBase64(), oldName, oldbase));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }
    private void getBio(long id){
        String dstAdress = "http://intranet-secure.de/instragram/getBio.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getUsers(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Profilbild:
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

    @Override
    public void processFinish(String output) {
        if(output.contains("getPostIDs")){
            String[] postIDs = output.split(" : ");

            int i = 1;
            while(i<postIDs.length){
                updateAllKomms(Long.parseLong(postIDs[i]));
                i++;
            }
        }else if(output.contains("Beschreibung")){
            String[] splitBeschreibung = output.split(":_:");
            Beschreibung.setText(splitBeschreibung[1]);
        }
    }
}
