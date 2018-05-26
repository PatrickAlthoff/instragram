package de.hshl.softwareprojekt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfilActivity extends AppCompatActivity implements View.OnClickListener {

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

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.futurecity);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        profilbild.setImageDrawable(roundedBitmapDrawable);
    }

    @Override
    public void onClick(View v) {

    }
}

