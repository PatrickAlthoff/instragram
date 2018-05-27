package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Main_Image_Clicked extends AppCompatActivity implements View.OnClickListener {

    private ImageView clickedImage;
    private Bitmap getBitmap;
    private TextView kommentar;
    private EditText editKomm;
    private TextView titel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__image__clicked);
        Intent getImage = getIntent();
        this.kommentar = findViewById(R.id.clickedKomment);
        this.editKomm = findViewById(R.id.editKomment);
        this.titel = findViewById(R.id.titel);
        this.clickedImage = findViewById(R.id.clickedImage);
        this.getBitmap =  getImage.getParcelableExtra("BitmapImage");
        this.clickedImage.setImageBitmap(this.getBitmap);
        this.titel.setText(getImage.getStringExtra("Titel"));
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        this.kommentar.setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onClick(View v) {

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
