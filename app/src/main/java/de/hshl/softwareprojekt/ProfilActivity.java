package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        Button folgen = findViewById(R.id.folgen);
        folgen.setOnClickListener(this);

        TextView biografie = findViewById(R.id.biografie);

        Button weiterleitung = findViewById(R.id.weiterleitung);
        weiterleitung.setOnClickListener(this);


        // soll Profilbild rund darstellen
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.futurecity);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        profilbild.setImageDrawable(roundedBitmapDrawable);
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
            case R.id.weiterleitung:
                Intent intent = new Intent(ProfilActivity.this,
                        Profil_BearbeitungActivity.class);
                startActivity(intent);
                break;

            default:
                break;


        }
    }
}

