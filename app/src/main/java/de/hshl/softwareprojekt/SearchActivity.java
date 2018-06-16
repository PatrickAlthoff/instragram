package de.hshl.softwareprojekt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private int IMAGE_CLICKED = 13;
    private DatabaseHelperPosts dataBasePosts;
    private ImageButton searchButton;
    private EditText searchText;
    private LinearLayout searchLayout;
    private ArrayList<String> postList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent getSeachIntent = getIntent();

        this.dataBasePosts = new DatabaseHelperPosts(this);
        this.searchText = findViewById(R.id.searchText);
        this.searchButton = findViewById(R.id.searchButton);
        this.searchLayout = findViewById(R.id.searchInnerLayout);
        if(getSeachIntent != null) {
            this.searchText.setText(getSeachIntent.getStringExtra("Hashtag"));
        }
        this.searchButton.setOnClickListener(this);
    }

    //Fügt der Frontpage ein individuelles Post Fragment hinzu
    public void addPostFragment(Bitmap postBitmap, final String username, String titel, ArrayList<String> hashlist, String date, int id, boolean liked){

        //Initialisiert den FragmentManager, das PostFragment und das FrameLayout
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final PostFragment frontPagePost = new PostFragment();
        FrameLayout frameInner = new FrameLayout(this);
        frameInner.setId(View.generateViewId());
        searchLayout.addView(frameInner, 0);

        //add fragment
        String i = String.valueOf(id);

        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(frameInner.getId(), frontPagePost, i);

        fragmentTransaction.commitNow();
        frontPagePost.addPost(postBitmap, titel);
        final ArrayList<String> hashList = hashlist;
        TextView datefield = frontPagePost.timeStampView;
        datefield.setText(date);
        //Gib den ImageViews eine generierte ID und fügt einen OnClick Listener hinzu
        ImageView postImage = frontPagePost.postImage;
        postImage.setId(View.generateViewId());
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View nextChild = ((ViewGroup)v.getParent()).getChildAt(2);
                Boolean checked = ((CheckBox)nextChild).isChecked();
                String likes = ((CheckBox) nextChild).getText().toString();
                int ID = nextChild.getId();
                //Baut aus den Daten im Cache eine Bitmap
                v.setDrawingCacheEnabled(true);
                v.buildDrawingCache();
                Bitmap parseBit = v.getDrawingCache();


                //Skaliert die Oben gebaute Bitmap auf ein kleineres Format
                Bitmap createBit = scaleBitmap(parseBit,-1,300);

                //Fügt dem Intent für die Vollansicht die Bitmap + einen Titel hinzu
                Intent intentVollansicht = new Intent(SearchActivity.this, Main_Image_Clicked.class);
                intentVollansicht.putExtra("BitmapImage", createBit);
                intentVollansicht.putExtra("Titel", v.getContentDescription());
                intentVollansicht.putExtra("Username", username);
                intentVollansicht.putExtra("Likes", likes);
                intentVollansicht.putExtra("Checked", checked);
                intentVollansicht.putExtra("ID", ID);
                intentVollansicht.putStringArrayListExtra("Hashtags", hashList);
                startActivityForResult(intentVollansicht, IMAGE_CLICKED);
            }
        });

        ImageButton deleteButton = frontPagePost.delete;
        TextView profilName = frontPagePost.postProfilName;
        profilName.setText(username);

        CheckBox likeCheck = frontPagePost.likeChecker;
        likeCheck.setChecked(liked);
        if(likeCheck.isChecked() == true){
            String getCount = likeCheck.getText().toString();
            String[] pieces = getCount.split(": ");
            int getInt = Integer.parseInt(pieces[1]);
            likeCheck.setText("Likes: " + (getInt + 1));
        }
        likeCheck.setId(View.generateViewId());
        likeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                String getCount = ((CheckBox) v).getText().toString();
                String[] pieces = getCount.split(": ");
                int getInt = Integer.parseInt(pieces[1]);
                View deleteButtonId = ((ViewGroup)v.getParent()).getChildAt(7);

                if(checked){
                    dataBasePosts.updateLike(deleteButtonId.getId(), true);
                    ((CheckBox) v).setText("Likes: " + (getInt + 1));

                }
                else{
                    dataBasePosts.updateLike(deleteButtonId.getId(), false);
                    ((CheckBox) v).setText("Likes: " + (getInt - 1));
                }
            }
        });

        deleteButton.setVisibility(View.INVISIBLE);
    }

    //Methode zum Skallieren der zu übergebenen Bitmap
    private Bitmap getAndScaleBitmap(Uri uri, int dstWidth, int dstHeight){
        try {
            Bitmap src = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

            float   srcWidth = src.getWidth(),
                    srcHeight = src.getHeight();

            if (dstWidth < 1) {
                dstWidth = (int) (srcWidth / srcHeight * dstHeight);
            }
            Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
            return dst;
        }
        catch (IOException e) {
            Log.e(MainActivity.class.getSimpleName(), "setBitmap", e);
        }
        return null;
    }

    //Skaliert eine übergebene Bitmap
    public Bitmap scaleBitmap(Bitmap bitmap, int dstWidth, int dstHeight){

        float   srcWidth = bitmap.getWidth(),
                srcHeight = bitmap.getHeight();

        if (dstWidth < 1) {
            dstWidth = (int) (srcWidth / srcHeight * dstHeight);
        }
        Bitmap dst = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);

        return dst;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.searchButton:
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                this.postList =  this.dataBasePosts.getHashtags(this.searchText.getText().toString());
                if(this.postList.size()>0){
                    int i = postList.size()-1;
                        while(i>=0){
                        String[] pieces = postList.get(i).split(" : ");
                        String ids = pieces[0];
                        String username = pieces[1];
                        int id = Integer.parseInt(ids);
                        Uri uri =  Uri.parse(pieces[2]);
                        Bitmap bitmap = getAndScaleBitmap(uri ,-1,330);
                        String titel = pieces[3];
                        ArrayList<String> hashlist = new ArrayList<>();
                        String[] hashes = pieces[4].split(":");
                        String date = pieces[5];
                        String bool = pieces[6];
                        int like = Integer.valueOf(bool);
                        Boolean liked = (like != 0);
                        int c = 0;
                        while(c<hashes.length){
                            hashlist.add(hashes[c]);
                            c++;
                        }

                        addPostFragment(bitmap, username,  titel, hashlist, date, id, liked);

                        i--;

                    }
                    postList.clear();
                    break;

                }
            }
        }
    }
