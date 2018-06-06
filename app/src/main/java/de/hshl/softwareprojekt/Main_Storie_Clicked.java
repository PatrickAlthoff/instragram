package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class Main_Storie_Clicked extends AppCompatActivity implements View.OnClickListener {
    Intent data;
    ArrayList<Bitmap> bitmapList;
    ArrayList<Uri> uriList;
    ProgressBar progressBar;
    int progressBarCount;
    ImageView storiePic;
    int i;
    int start;
    Button storieStart;
    Button addBtn;
    Button startBtn;
    TextView emptyText;
    ImageButton deleteStorie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__storie__clicked);
        this.emptyText = findViewById(R.id.emptyText);
        this.emptyText.setVisibility(View.INVISIBLE);
        Intent data = getIntent();
        this.bitmapList = new ArrayList<>();
        this.uriList = data.getParcelableArrayListExtra("UriList");
        scaleUp(this.uriList);
        this.addBtn = findViewById(R.id.addBtn);
        this.addBtn.setOnClickListener(this);
        this.deleteStorie = findViewById(R.id.deleteStorie);
        this.deleteStorie.setOnClickListener(this);
        this.startBtn = findViewById(R.id.startStorie);
        this.startBtn.setOnClickListener(this);
        this.start = 0;
        Toolbar toolbar = findViewById(R.id.toolbar5);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    public void addStorie() {
        if (this.uriList.size() > 0) {

            this.emptyText.setVisibility(View.INVISIBLE);

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



        } else {

        }
    }

    public void startBar() {
        if (this.uriList.size() == 0) {
            this.emptyText.setVisibility(View.VISIBLE);
        } else {
            this.progressBarCount = this.bitmapList.size();
            final ImageView storiePic = this.storiePic;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    i = 0;
                    while (i < progressBarCount) {

                        storiePic.setImageBitmap(bitmapList.get(i));
                        i++;
                        progressBar.setProgress((i) * 100 / progressBarCount);
                        SystemClock.sleep(2000);
                    }

                }
            }).start();
        }
    }

    public ArrayList<Bitmap> scaleUp(ArrayList<Uri> uriList) {

        int length = uriList.size();
        int i = 0;
        Bitmap bitmap;
        while (length > i) {
            bitmap = getAndScaleBitmapNormal(uriList.get(i), -1, 330);
            bitmapList.add(i, bitmap);
            i++;

        }
        return bitmapList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBtn:
                Intent sendtoStorieBearbeitung = new Intent(Main_Storie_Clicked.this, StoriesBearbeitungsActivity.class);
                sendtoStorieBearbeitung.putParcelableArrayListExtra("UriList", this.uriList);
                startActivityForResult(sendtoStorieBearbeitung, 101);
                if(this.uriList.size()> 0){
                    deleteProcess();
                }
                break;
            case R.id.deleteStorie:
                deleteProcess();
                break;
            case R.id.startStorie:
                if(start == 0){
                    addStorie();
                    start++;
                    startBar();
                }
                else {
                    startBar();
                }
                break;
        }

    }
    public void deleteProcess(){

            this.uriList.clear();
            this.bitmapList.clear();
            this.storiePic.setVisibility(View.INVISIBLE);
            this.progressBar.setVisibility(View.INVISIBLE);
            this.emptyText.setVisibility(View.VISIBLE);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent sendBackIntent = new Intent(Main_Storie_Clicked.this, MainActivity.class);
            sendBackIntent.putParcelableArrayListExtra("UriList", this.uriList);
            setResult(RESULT_OK, sendBackIntent);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private Bitmap getAndScaleBitmapNormal(Uri uri, int dstWidth, int dstHeight) {
        try {
            Bitmap src = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

            float srcWidth = src.getWidth(),
                    srcHeight = src.getHeight();

            if (dstWidth < 1) {
                dstWidth = (int) (srcWidth / srcHeight * dstHeight);
            }
            Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
            return dst;
        } catch (IOException e) {
            Log.e(MainActivity.class.getSimpleName(), "setBitmap", e);
        }
        return null;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Verarbeitung der Image Capture Request
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Intent intentGet = data;
                    this.uriList = intentGet.getParcelableArrayListExtra("UriList");
                    this.bitmapList.clear();
                    scaleUp(this.uriList);
                    start++;
                    addStorie();
                }
            }

        }
    }
}

