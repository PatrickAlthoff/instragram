package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfilActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        ImageView profilbild = findViewById(R.id.profilbild);

        TextView follower = findViewById(R.id.follower);
        TextView following = findViewById(R.id.following);

        TextView AnzahlFollower = findViewById(R.id.anzFollower);
        TextView AnzahlFollowing = findViewById(R.id.anzFollowing);

        Button folgen = findViewById(R.id.folgen);
        folgen.setOnClickListener(this);

        TextView biografie = findViewById(R.id.biografie);

        View divider = findViewById(R.id.divider);


        // soll Profilbild rund darstellen
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.futurecity);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        profilbild.setImageDrawable(roundedBitmapDrawable);
    }
    // erstellt Menuleiste
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater MenuInflater = getMenuInflater();
        MenuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // wenn Profil bearbeiten
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // wenn Button folgen gedrückt wird
            case R.id.folgen:
                Button folgen = findViewById(R.id.folgen);
                folgen.setBackgroundColor(Color.GREEN);
                folgen.setText("Entfolgen");
                break;
            // Weiterleitung auf Profil_BearbeitungActivity
            case R.id.action_settings:
                Intent intent = new Intent(ProfilActivity.this,
                        Profil_BearbeitungActivity.class);
                startActivity(intent);
                break;

            default:
                break;


        }
    }
}

