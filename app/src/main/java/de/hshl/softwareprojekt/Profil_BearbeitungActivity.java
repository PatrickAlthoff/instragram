package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Profil_BearbeitungActivity extends AppCompatActivity {

    public static final int IMAGE_GALLERY_REQUEST = 20;

    ImageView Profilbild;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil__bearbeitung);

        ImageView Profilbild = findViewById(R.id.Profilbild);

        Button OpenPicture = findViewById(R.id.OpenPicture);

        EditText editBiografie = findViewById(R.id.editBiografie);
        EditText editName = findViewById(R.id.editName);


    }// ImageGalerie öffnen wenn geklickt
    public void onGalleryClicked(View v) {
        Intent photoPicker = new Intent(Intent.ACTION_PICK);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        Uri data = Uri.parse(pictureDirectoryPath);

        photoPicker.setDataAndType(data, "image/*");

        startActivityForResult(photoPicker, IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            if (requestCode == IMAGE_GALLERY_REQUEST) {

                Uri imageUri = data.getData();

                InputStream inputStream;

                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    // Bild dem Nutzer anzeigen
                    Profilbild.setImageBitmap(image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to open Image", Toast.LENGTH_LONG).show();
                }

            }


        }

    }

}
