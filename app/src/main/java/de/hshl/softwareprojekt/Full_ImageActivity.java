package de.hshl.softwareprojekt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class Full_ImageActivity extends ProfilActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);
        Intent i = getIntent();

        // Selected image id
        int position = i.getExtras().getInt("id");
        GridAdapter imageAdapter = new GridAdapter(this);

        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
        imageView.setImageResource(imageAdapter.images[position]);
    }
}//