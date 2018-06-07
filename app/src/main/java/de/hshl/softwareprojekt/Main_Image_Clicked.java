package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Main_Image_Clicked extends AppCompatActivity implements View.OnClickListener {

    private ImageView clickedImage;
    private TextView kommentar;
    private TextView titel;
    private EditText editKomm;
    private Bitmap getBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__image__clicked);
        Intent getImage = getIntent();

        this.clickedImage = findViewById(R.id.clickedImage);
        this.getBitmap =  getImage.getParcelableExtra("BitmapImage");
        this.clickedImage.setImageBitmap(this.getBitmap);

        this.kommentar = findViewById(R.id.clickedKomment);
        this.titel = findViewById(R.id.titel);
        this.editKomm = findViewById(R.id.editKomment);

        this.kommentar.setOnClickListener(this);
        this.titel.setText(getImage.getStringExtra("Titel"));

        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onClick(View v) {
            //Setzt die Sichtbarkeit des EditText auf VISIBLE und leert den Inhalt
            this.editKomm.setVisibility(View.VISIBLE);
            this.editKomm.requestFocus();
            this.editKomm.setText("");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}