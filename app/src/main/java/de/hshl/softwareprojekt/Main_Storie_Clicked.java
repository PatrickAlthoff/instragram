package de.hshl.softwareprojekt;

import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class Main_Storie_Clicked extends AppCompatActivity {
    ConstraintLayout storieCons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__storie__clicked);
        this.storieCons = findViewById(R.id.storieCons);
    }
    public void addStorieFragment(Bitmap postBitmap, String titel){

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final StoriesFragment frontPageStorie = new StoriesFragment();
        FrameLayout frameInner = new FrameLayout(this);
        frameInner.setId(View.generateViewId());
        storieCons.addView(frameInner,0);

        String i = Long.toString(System.currentTimeMillis()/1000);
        int c = Integer.parseInt(i);
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(frameInner.getId(), frontPageStorie, i);

        fragmentTransaction.commitNow();
        frontPageStorie.addStorie(postBitmap,titel);

        ImageView storieImage = frontPageStorie.storieImage;
        storieImage.setId(View.generateViewId());
    }

}
