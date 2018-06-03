package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class Main_Storie_Clicked extends AppCompatActivity implements View.OnClickListener {
    Intent data;
    ArrayList<Bitmap> bitmapList;
    ProgressBar progressBar;
    int progressBarCount;
    ImageView storiePic;
    int i;
    Button storieStart;
    Button getBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__storie__clicked);

        Intent data = getIntent();
        Bundle bundle = data.getExtras();
        String s = bundle.getString("String");
        this.bitmapList = data.getParcelableArrayListExtra("BitmapList");
        this.getBtn = findViewById(R.id.getBtn);
        this.getBtn.setOnClickListener(this);


        Toolbar toolbar = findViewById(R.id.toolbar5);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

    }
    public void addStorie() {

        //Initialisiert den FragmentManager, das PostFragment und das FrameLayout
        final FragmentManager storieManager = getSupportFragmentManager();
        final StoriesFragment storiesFragment = new StoriesFragment();


        //add fragment

        final FragmentTransaction storieTransaction = storieManager.beginTransaction();
        storieTransaction.add(R.id.storieConstraints, storiesFragment);
        storieTransaction.commitNow();
        storiesFragment.addStr(this.bitmapList.get(0));



        this.progressBar = storiesFragment.prBar;
        this.storiePic = storiesFragment.storieImage;
        this.storieStart = storiesFragment.strStart;
        this.storieStart.setOnClickListener(new View.OnClickListener() {
            @Override
          public void onClick(View v) {
              startBar();
           }
        });

    }

    public void startBar(){
        this.progressBarCount = this.bitmapList.size();
        final ImageView storiePic = this.storiePic;
        new Thread(new Runnable() {
            @Override
            public void run() {
                i = 0;

                while(i<progressBarCount){

                    storiePic.setImageBitmap(bitmapList.get(i));
                    i++;
                    progressBar.setProgress((i)*100/progressBarCount);
                    SystemClock.sleep(2000);
                }

            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    this.data = data;
                    String s = this.data.getParcelableExtra("String");

                    //this.bitmapList = this.data.getParcelableArrayListExtra("BitmapList");
                    //addStorie();
                }
            }

        }
    }

    @Override
    public void onClick(View v)  {
        addStorie();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent sendBackIntent = new Intent (Main_Storie_Clicked.this, MainActivity.class);
            setResult(RESULT_CANCELED, sendBackIntent);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
