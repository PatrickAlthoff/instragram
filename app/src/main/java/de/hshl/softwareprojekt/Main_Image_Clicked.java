package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Main_Image_Clicked extends AppCompatActivity implements View.OnClickListener {
    private int ID;
    private ImageView clickedImage;
    private TextView kommentar;
    private TextView titel;
    private TextView disUser;
    private EditText editKomm;
    private Bitmap getBitmap;
    private CheckBox checkLike;
    private User user;

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
        this.disUser = findViewById(R.id.displayUser);

        this.kommentar.setOnClickListener(this);
        this.titel.setText(getImage.getStringExtra("Titel"));

        this.user = (User) getImage.getSerializableExtra("User");
        this.disUser.setText(user.getUsername());

        this.checkLike = findViewById(R.id.clickedLike);
        this.checkLike.setText(getImage.getStringExtra("Likes"));
        this.checkLike.setChecked(getImage.getExtras().getBoolean("Checked"));
        this.checkLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                String getCount = ((CheckBox) v).getText().toString();
                String[] pieces = getCount.split(": ");
                int getInt = Integer.parseInt(pieces[1]);

                if(checked){

                    ((CheckBox) v).setText("Likes: " + (getInt + 1));

                }
                else{
                    ((CheckBox) v).setText("Likes: " + (getInt - 1));
                }
            }
        });

        this.ID = getImage.getExtras().getInt("ID");
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
            Intent sendBackIntent = new Intent(Main_Image_Clicked.this, MainActivity.class);

            sendBackIntent.putExtra("ID", this.ID);
            sendBackIntent.putExtra("Likes", this.checkLike.getText().toString());
            sendBackIntent.putExtra("Checked", this.checkLike.isChecked());
            setResult(RESULT_OK,sendBackIntent);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}