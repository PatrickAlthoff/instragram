package de.hshl.softwareprojekt;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener,AsyncResponse {
    private int IMAGE_CLICKED = 13;
    private ArrayList<String> response;
    private DatabaseHelperPosts dataBasePosts;
    private LinearLayout searchLayout;
    private ArrayList<String> postList;
    private ArrayList<PostFragment> postFragmentList;
    private ArrayList<SearchUserFragment> searchFragmentList;
    private Intent getSeachIntent;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        this.getSeachIntent = getIntent();
        this.postFragmentList = new ArrayList<>();
        this.searchFragmentList = new ArrayList<>();
        this.response = new ArrayList<>();
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

        //Initialisiert den FragmentManager, das PostFragment und das FrameLayout
        final FragmentManager fragmentManagerSearchPost = getSupportFragmentManager();
        final PostFragment frontPagePost = new PostFragment();
        FrameLayout frameInner = new FrameLayout(this);
        frameInner.setId(View.generateViewId());
        searchLayout.addView(frameInner, 0);

        //add fragment
        String i = String.valueOf(id);

        final FragmentTransaction fragmentTransactionSearchPost = fragmentManagerSearchPost.beginTransaction();
        fragmentTransactionSearchPost.add(frameInner.getId(), frontPagePost, i);

        fragmentTransactionSearchPost.commitNow();
        frontPagePost.addPost(postBitmap, titel);
        final ArrayList<String> hashList = hashlist;
        TextView datefield = frontPagePost.timeStampView;
        datefield.setText(date);
        //Gib den ImageViews eine generierte ID und fügt einen OnClick Listener hinzu

        ImageView userImage = frontPagePost.profilPicPost;
        userImage.setImageDrawable(roundImage(userPic));
        ImageView postImage = frontPagePost.postImage;
        postImage.setId(View.generateViewId());
        postImage.setContentDescription(i);
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View nextChild = ((ViewGroup)v.getParent()).getChildAt(2);
                View titelView = ((ViewGroup)v.getParent()).getChildAt(3);
                Boolean checked = ((CheckBox)nextChild).isChecked();
                String titel = ((TextView)titelView).getText().toString();
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
                intentVollansicht.putExtra("Titel", titel);
                intentVollansicht.putExtra("Likes", likes);
                intentVollansicht.putExtra("Checked", checked);
                intentVollansicht.putExtra("ID", v.getContentDescription());
                intentVollansicht.putExtra("User", user);
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
                if(checked){
                    String idString = v.getContentDescription().toString();
                    long id = Long.parseLong(idString);
                    if(dataBasePosts.getLikeCount(id,user.getUsername())==0){
                        dataBasePosts.insertIntoLikeCount(id, user.getUsername(),true);
                    }else if(dataBasePosts.getLikeCount(id,user.getUsername())==1){
                        dataBasePosts.updateLike(id,user.getUsername(), true);
                    }
                    ((CheckBox) v).setText("Likes: " + (getInt + 1));
                    updateLikeStatus(1, id);

                }
                else{
                    String idString = v.getContentDescription().toString();
                    long id = Long.parseLong(idString);
                    dataBasePosts.updateLike(id,user.getUsername(), false);
                    ((CheckBox) v).setText("Likes: " + (getInt - 1));
                    updateLikeStatus(-1, id);
                }
            }
        });
        postFragmentList.add(frontPagePost);
        deleteButton.setVisibility(View.INVISIBLE);
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

    public RoundedBitmapDrawable roundImage(Bitmap bitmap){
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;
    }


    //Generiert den Inhalt des DrawerLayout aus der main.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search for something...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                sendXML(query);
                response.clear();
                int i = 0;
                while(i<postFragmentList.size()){
                    removeFragment(postFragmentList.get(i));
                    i++;
                }
                int d = 0;
                while(d<searchFragmentList.size()){
                    removeSearchFragment(searchFragmentList.get(d));
                    d++;
                }
                searchView.setQuery("",false);
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
    public void removeSearchFragment(SearchUserFragment pf) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(pf);
        fragmentTransaction.commitNow();
    }

    private void updateLikeStatus(int status, long id){
        String dstAdress = "http://intranet-secure.de/instragram/updateLikeStatus.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateStatus(status, id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    private void sendXML(String query){
        String dstAdress = "http://intranet-secure.de/instragram/search.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.sendSearchRequest(query));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }
    private void updateFollowStatus(long userkey, long FID){
        String dstAdress = "http://intranet-secure.de/instragram/updateFollows.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateFollows(userkey,FID));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }
    private void updateunfollowStatus(long userkey, long FID){
        String dstAdress = "http://intranet-secure.de/instragram/updateUnfollow.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateFollows(userkey,FID));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }
    private void getUserPic(long query){
        String dstAdress = "http://intranet-secure.de/instragram/getUserPic.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getUsers(query));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }
    public void addSearchUser(Bitmap postBitmap, String username, String contentDis) {

        //Initialisiert den FragmentManager, das PostFragment und das FrameLayout
        FragmentManager fragmentManagerSearchUser = getSupportFragmentManager();
        SearchUserFragment searchFragment = new SearchUserFragment();
        FrameLayout frameInner = new FrameLayout(this);
        frameInner.setId(View.generateViewId());
        searchLayout.addView(frameInner, 0);

        final FragmentTransaction fragmentTransactionSearchUser = fragmentManagerSearchUser.beginTransaction();
        fragmentTransactionSearchUser.add(frameInner.getId(), searchFragment);

        fragmentTransactionSearchUser.commitNow();
        searchFragment.init(username, contentDis);
        ImageView profilBild = searchFragment.profilPic;
        profilBild.setImageDrawable(roundImage(postBitmap));
        profilBild.setContentDescription(contentDis);
        profilBild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToProfil = new Intent(SearchActivity.this,ProfilActivity.class);
                goToProfil.putExtra("UserKey",v.getContentDescription().toString());
                goToProfil.putExtra("Code",3);
                goToProfil.putExtra("User", user);
                startActivity(goToProfil);
            }
        });
        Button followBtn = searchFragment.followBtn;
        Button unfollowBtn = searchFragment.unfollowBtn;
        this.searchFragmentList.add(searchFragment);
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = Long.parseLong(v.getContentDescription().toString());
                updateFollowStatus(user.getId(), id);
            }
        });
        unfollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = Long.parseLong(v.getContentDescription().toString());
                updateunfollowStatus(user.getId(), id);
            }
        });

    }
    @Override
    public void processFinish(String output) {
        String[] response = output.split(" : ");
        if(response[0].equals("UserReturn")){
            int i = 1;
            int d = 1;
            while(i<response.length){
                this.response.add(response[i]);

            i++;
            }
            while(d<response.length){
                getUserPic(Long.parseLong(response[d]));
                d+=2;
            }
        }else if(output.contains("UserPic")) {
            String[] bitmapCodes = output.split(":");
            int i = 0;
            while(i<this.response.size()){
                if(bitmapCodes[1].equals(this.response.get(i))){
                    addSearchUser(ImageHelper.base64ToBitmap(bitmapCodes[2]), this.response.get(i+1),this.response.get(i));
                }
                i+=2;
            }

        }else if(output.contains("Followed")) {
            Toast.makeText(getApplicationContext(), "Du folgst nun dieser Person!", Toast.LENGTH_SHORT).show();
        }else if(output.contains("FollowExc")) {
            Toast.makeText(getApplicationContext(), "Du folgst der Person schon!", Toast.LENGTH_SHORT).show();
        }else if(output.contains("UnfollowNotPossible")) {
            Toast.makeText(getApplicationContext(), "Du musst der Person vorher folgen!", Toast.LENGTH_SHORT).show();
        }else if(output.contains("Unfollowed")) {
            Toast.makeText(getApplicationContext(), "Du folgst der Person nun nicht mehr!", Toast.LENGTH_SHORT).show();
        }else if(output.contains("HashInput")) {
            String[] hashtags = output.split(" : ");
            int i = 1;
            while (hashtags.length > i) {
                sendXML(hashtags[i]);
                i++;
            }
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

