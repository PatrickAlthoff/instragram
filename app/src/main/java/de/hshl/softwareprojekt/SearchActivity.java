package de.hshl.softwareprojekt;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener,AsyncResponse {
    private int IMAGE_CLICKED = 13;
    private DatabaseHelperPosts dataBasePosts;
    private LinearLayout searchLayout;
    private ArrayList<String> postList;
    private ArrayList<PostFragment> postFragmentList;
    private Intent getSeachIntent;
    private User user;

    private Bitmap postbitmap;
    private String titel;
    private ArrayList<String> hashL;
    private String date;
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        this.getSeachIntent = getIntent();
        this.postFragmentList = new ArrayList<>();
        this.dataBasePosts = new DatabaseHelperPosts(this);
        this.searchLayout = findViewById(R.id.searchInnerLayout);
        this.user = (User) getSeachIntent.getSerializableExtra("User");

        Toolbar toolbar = findViewById(R.id.toolbar8);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

    }

    //Fügt der Frontpage ein individuelles Post Fragment hinzu
    public void addPostFragment(Bitmap postBitmap, final String username, String titel, ArrayList<String> hashlist, String date, long id, int liked, Bitmap userPic){

        this.postbitmap = postbitmap;
        this.titel = titel;
        this.hashL = hashlist;
        this.date = date;
        this.id = id;

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

        ImageView userImage = frontPagePost.profilPicPost;
        userImage.setImageBitmap(userPic);

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
        int like = dataBasePosts.getLikeCount(id, user.getUsername());
        if(like==2){
            likeCheck.setChecked(true);

        }else{
            likeCheck.setChecked(false);
        }
        likeCheck.setText("Likes: " + (liked));
        likeCheck.setId(View.generateViewId());
        String ID = String.valueOf(id);
        likeCheck.setContentDescription(ID);
        likeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                String getCount = ((CheckBox) v).getText().toString();
                String[] pieces = getCount.split(": ");
                int getInt = Integer.parseInt(pieces[1]);
                View deleteButtonId = ((ViewGroup)v.getParent()).getChildAt(7);

                if(checked){
                    String idString = v.getContentDescription().toString();
                    long id = Long.parseLong(idString);
                    if(dataBasePosts.getLikeCount(v.getId(),user.getUsername())==0){
                        dataBasePosts.insertIntoLikeCount(id, user.getUsername(),true);
                    }else if(dataBasePosts.getLikeCount(v.getId(),user.getUsername())==1){
                        dataBasePosts.updateLike(v.getId(), true);
                    }
                    ((CheckBox) v).setText("Likes: " + (getInt + 1));
                    updateLikeStatus(1, id);

                }
                else{
                    String idString = v.getContentDescription().toString();
                    long id = Long.parseLong(idString);
                    dataBasePosts.updateLike(deleteButtonId.getId(), false);
                    ((CheckBox) v).setText("Likes: " + (getInt - 1));
                    updateLikeStatus(-1, id);
                }
            }
        });
        postFragmentList.add(frontPagePost);
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


        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
    //Generiert den Inhalt des DrawerLayout aus der main.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search for something...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                sendXML(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setQuery(this.getSeachIntent.getStringExtra("Search"), true);
        searchView.setQuery(this.getSeachIntent.getStringExtra("Search"), false);
        searchView.clearFocus();
        return true;
    }
    //Entfernt das Fragment
    public void removeFragment(PostFragment pf) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(pf);
        fragmentTransaction.commitNow();

    }

    private void updateLikeStatus(int status, long id){
        String dstAdress = "http://intranet-secure.de/instragram/updateLikeStatus.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress, this);
        httpConnection.setMessage(XmlHelper.buildXMLUpdateStatus(status, id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    private void sendXML(String query){

        String dstAdress = "http://intranet-secure.de/instragram/search.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress, this);
        httpConnection.setMessage(XmlHelper.buildXmlSearch(query));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }
    private void getUserPic(long query){

        String dstAdress = "http://intranet-secure.de/instragram/getUsers.php";

        HttpConnection httpConnection = new HttpConnection(dstAdress, this);

        httpConnection.setMessage(XmlHelper.getUsers(query));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }
    @Override
    public void processFinish(String output) {
        String[] response = output.split(" : ");
        if(response[0].equals("UserReturn")){
            int i = 1;
            while(i<response.length){
            TextView searchResult = new TextView(this);
            searchResult.setText(response[i]);
            searchLayout.addView(searchResult);
            i++;
            }
        }else if(output.contains("Update erfolgreich.")) {
            String returner = output;
        }else{
           /* int index = 1;
            while (index < response.length) {
                removeFragment(postFragmentList.get(index));
                index++;
            }
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }*/

            postList = new ArrayList<>();
            if (response.length > 1) {
                int i = 1;
                while (i < response.length-1) {

                    String ids = response[i];
                    String username = response[i+1];
                    long id = Long.parseLong(ids);
                    String base64 = response[i+2];
                    Bitmap bitmap = ImageHelper.base64ToBitmap(base64);
                    String titel = response[i+3];
                    ArrayList<String> hashlist = new ArrayList<>();
                    String[] hashes = response[i+4].split(":");
                    String date = response[i+5];
                    String bool = response[i+6];
                    String userKeyString = response[i+7];
                    String userPic = response[i+8];
                    long userKey = Long.parseLong(userKeyString);
                    int like = Integer.valueOf(bool);
                    int c = 0;
                    while (c < hashes.length) {
                        hashlist.add(hashes[c]);
                        c++;
                    }

                    addPostFragment(bitmap, username, titel, hashlist, date, id, like, ImageHelper.base64ToBitmap(userPic));

                    i +=9;

                }
                postList.clear();
            }

        }



    }
}

