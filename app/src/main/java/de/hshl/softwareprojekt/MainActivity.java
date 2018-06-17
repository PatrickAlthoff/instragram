package de.hshl.softwareprojekt;

import android.Manifest;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //Variablen zur Verarbeitung der Inhalte in der Activity
    private boolean permissionGranted;
    private static final int PERMISSION_REQUEST = 1;
    private int STORIE_PICK = 1;
    private int TITLE = 2;
    private int DESCRIPTION = 3;
    private int IMAGE_CAPTURE = 4;
    private int GALLERY_PICK = 5;
    private int BEARBEITUNG_CODE = 12;
    private int REQUEST_GETSEND = 12;
    private int IMAGE_CLICKED = 13;
    private float dpi;
    private String date;
    private Uri imageUri;
    private Intent intentCaptureImage;
    private User user;
    private ArrayList<Uri> uriList;
    private ArrayList<String> titelList;
    private ImageView profilBild;
    private ImageView followerBild;
    private ImageButton refreshBtn;
    private Button testBtn;
    private TextView profilName;
    private LinearLayout innerLayout;
    private LinearLayout horiInner;
    private ArrayList<TextView> textViewList;
    private ArrayList<String> hashTagList;
    private ArrayList<String> postList;
    private DatabaseHelperPosts dataBasePosts;

    //Methode um die Display Auflösung zu erhalten
    private void getDisplayMetrics(){
        DisplayMetrics dm = new DisplayMetrics();
        MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.dpi = getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        getDisplayMetrics();
        Intent logInIntent = getIntent();
        this.user = (User) logInIntent.getSerializableExtra("User");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.innerLayout = findViewById(R.id.innerLayout);
        this.horiInner = findViewById(R.id.horiInner);
        this.profilBild = findViewById(R.id.profilBild);
        this.followerBild = findViewById(R.id.followerNr1);
        this.profilName = findViewById(R.id.profilName);
        this.refreshBtn = findViewById(R.id.refreshBtn);
        this.testBtn = findViewById(R.id.testBtn);

        this.testBtn.setOnClickListener(this);
        this.refreshBtn.setOnClickListener(this);
        this.followerBild.setOnClickListener(this);
        this.profilBild.setOnClickListener(this);

        this.uriList = new ArrayList<>();
        this.titelList = new ArrayList<>();
        this.textViewList = new ArrayList<>();
        this.hashTagList = new ArrayList<>();

        this.profilName.setText(this.user.getUsername());
        this.dataBasePosts = new DatabaseHelperPosts(this);

        this.postList = this.dataBasePosts.getData();

    }

    //Fügt der Frontpage ein individuelles Post Fragment hinzu
    public void addPostFragment(Bitmap postBitmap, String titel, ArrayList<String> hashlist, String date, int id, boolean liked){

        //Initialisiert den FragmentManager, das PostFragment und das FrameLayout
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final PostFragment frontPagePost = new PostFragment();
        FrameLayout frameInner = new FrameLayout(this);
        frameInner.setId(View.generateViewId());
        innerLayout.addView(frameInner, 0);

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
                Intent intentVollansicht = new Intent(MainActivity.this, Main_Image_Clicked.class);
                intentVollansicht.putExtra("BitmapImage", createBit);
                intentVollansicht.putExtra("Titel", v.getContentDescription());
                intentVollansicht.putExtra("User", user);
                intentVollansicht.putExtra("Likes", likes);
                intentVollansicht.putExtra("Checked", checked);
                intentVollansicht.putExtra("ID", ID);
                intentVollansicht.putStringArrayListExtra("Hashtags", hashList);
                startActivityForResult(intentVollansicht, IMAGE_CLICKED);
            }
        });

        ImageButton deleteButton = frontPagePost.delete;
        TextView profilName = frontPagePost.postProfilName;
        profilName.setText(user.getUsername());

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

        this.textViewList.add(profilName);

        deleteButton.setId(id);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment(frontPagePost);
                dataBasePosts.deletePost(v.getId());
            }
        });

    }
    //Entfernt das Fragment
    public void removeFragment(PostFragment pf) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(pf);
        fragmentTransaction.commitNow();

   }
    //Methode zur Umrechnung der dpi
    private int dp2px(int dp){
        return (int)(dp*dpi);
    }

    //Öffnet Fenster zur Bestätigung der Zugriffsrechte / Prüft ob dies schon geschehen ist
    protected void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{   Manifest.permission.WRITE_EXTERNAL_STORAGE}, this.PERMISSION_REQUEST);
            }
        }
        else {
            this.permissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){

        switch (requestCode) {
            case PERMISSION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.permissionGranted = true;
                startCamera();
                }
                else  {
                    this.permissionGranted = false;
                }
                return;
        }
    }

    //Methode zum Start der Camera
    private void startCamera(){
        if (this.permissionGranted){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.TITLE, TITLE);
            contentValues.put(MediaStore.Images.Media.DESCRIPTION, DESCRIPTION );
            contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
             imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
             intentCaptureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentCaptureImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intentCaptureImage, IMAGE_CAPTURE);
        }
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

    //onActivityResult Methode zur Verarbeitung mehrerer Requests
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Verarbeitung der Image Capture Request
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    Bitmap myBitmap = getAndScaleBitmap(this.imageUri, -1, 300);
                    Intent sendToBearbeitung = new Intent (MainActivity.this, Post_BearbeitungsActivity.class);
                    sendToBearbeitung.putExtra("BitmapImage", myBitmap);
                    sendToBearbeitung.putExtra("User", this.user);
                    startActivityForResult(sendToBearbeitung, BEARBEITUNG_CODE);
                }
            }
            else {
                int rowsDeleted = getContentResolver().delete(imageUri, null, null);
                Log.d(MainActivity.class.getSimpleName(), rowsDeleted + " rows deleted");
                //startCamera();
            }
        }

        //Verarbeitung der Gallerie Request
        if(requestCode == GALLERY_PICK){
            if(resultCode == RESULT_OK){
                if(data != null) {
                    Uri uri = data.getData();

                    Bitmap myBitmap = getAndScaleBitmap(uri, -1, 300);
                    Intent sendToBearbeitung = new Intent (MainActivity.this, Post_BearbeitungsActivity.class);
                    sendToBearbeitung.putExtra("BitmapImage", myBitmap);
                    sendToBearbeitung.putExtra("User", this.user);
                    startActivityForResult(sendToBearbeitung, BEARBEITUNG_CODE);
                }
            }
        else{
            Log.d(MainActivity.class.getSimpleName(),"no picture selected");
            }
        }

        //Verarbeitung der Bearbeitungs Request
        if (requestCode == REQUEST_GETSEND) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Intent intentVerarbeitet = data;
                    Bitmap postImage;
                    String titel;
                    titel = intentVerarbeitet.getStringExtra("Titel");
                    postImage = intentVerarbeitet.getParcelableExtra("BitmapImage");
                    this.hashTagList = intentVerarbeitet.getStringArrayListExtra("Hashtags");
                    String date = intentVerarbeitet.getStringExtra("Date");
                    String d = Long.toString(System.currentTimeMillis()/1000);
                    int c = Integer.parseInt(d);
                    addPostFragment(postImage, titel, this.hashTagList, date, c, false);
                    int i = 0;
                    String hashes = "";
                    while(i<this.hashTagList.size()){

                        hashes = hashes + ":"+ hashTagList.get(i);
                        i++;
                    }
                    if(hashes == null){
                        hashes = "#NoHashtags";
                    }
                    String path = getImageUri(this,postImage).toString();
                    dataBasePosts.insertData(c,this.user.getUsername(), path, titel, hashes, date, false, this.user.getId());
                    ArrayList<String> postList = dataBasePosts.getData();
                    postList.size();
                }
            }


            else {
                Log.d(MainActivity.class.getSimpleName(),"no picture selected");
            }
        }
        //Verarbeitet Informationen aus einem Intent, aus der Story Activity
        if(requestCode == STORIE_PICK){
            if(resultCode == RESULT_OK){
                if(data != null) {
                    Intent intentStory = data;
                    this.uriList = intentStory.getParcelableArrayListExtra("UriList");
                    this.titelList = intentStory.getStringArrayListExtra("TitelList");
                }
            }
            else{
                Log.d(MainActivity.class.getSimpleName(),"no picture selected");
            }
        }
        if(requestCode == 101){
            if(resultCode == RESULT_OK){
                if(data != null){
                    Intent intentStory = data;
                    this.uriList = intentStory.getParcelableArrayListExtra("UriList");
                    this.titelList = intentStory.getStringArrayListExtra("TitelList");
                }
            }
        }
        //Enthält das User Objekt aus der Settings Acitivty
        if(requestCode == 111){
            if(resultCode == RESULT_OK){
                if(data != null){
                    Intent intentStorie = data;
                    this.user = (User) intentStorie.getSerializableExtra("User");
                    this.profilName.setText(this.user.getUsername());
                    int i=0;
                    while(i < textViewList.size()){
                        textViewList.get(i).setText(user.getUsername());
                        i++;
                    }
                }
            }
        }
        //Enthält Daten aus der Vollbildansicht
        if(requestCode == IMAGE_CLICKED) {
            if(resultCode == RESULT_OK){
                if(data != null){
                    Intent intentFromImage = data;
                    int ID = intentFromImage.getExtras().getInt("ID");
                    View v = findViewById(ID);
                    ((CheckBox)v).setText(intentFromImage.getStringExtra("Likes"));
                    ((CheckBox)v).setChecked(intentFromImage.getExtras().getBoolean("Checked"));
                }
            }
        }
    }

    public void createFollower(){
        ImageView followerPic = new ImageView(this);
        this.horiInner.addView(followerPic);
        followerPic.setImageResource(R.drawable.ic_menu_gallery);
        followerPic.setId(this.horiInner.getId());
        followerPic.setOnClickListener(this);
    }

    //Schließt bei einem Backpress das DrawerLayout, falls dies offen ist
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Generiert den Inhalt des DrawerLayout aus der main.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search for #Hashtags...");

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // This is your adapter that will be filtered

                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                // **Here you can get the value "query" which is entered in the search box.**

                Intent startSearchIntent = new Intent(MainActivity.this, SearchActivity.class);
                if(query.contains("#")) {
                    startSearchIntent.putExtra("Hashtag", query);
                    startActivity(startSearchIntent);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
                else{
                    query = "#" + query;
                    startSearchIntent.putExtra("Hashtag", query);
                    startActivity(startSearchIntent);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return true;
    }

    //Verarbeitung der Settings Anfrage (noch passiert nichts)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Methode zur Verarbeitung der Buttons im  Drawer Menü
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startCamera();

        } else if (id == R.id.nav_gallery) {
            Intent intentGallerie = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentGallerie, GALLERY_PICK);

        } else if (id == R.id.nav_slideshow) {
            Intent intentStories = new Intent(MainActivity.this, Main_Story_Clicked.class);
            intentStories.putParcelableArrayListExtra("UriList", this.uriList);
            intentStories.putStringArrayListExtra("TitelList", this.titelList);
            startActivityForResult(intentStories, 101);
        }
        //Startet das Settingslayout
        else if (id == R.id.nav_manage) {
            Intent intentSetting = new Intent(MainActivity.this, SettingsActivity.class);
            intentSetting.putExtra("User", this.user);
            startActivityForResult(intentSetting, 111);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        //Shortcut to Logout.
        else if (id == R.id.nav_logout) {
            Intent intentLogout = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentLogout);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //Enthält die onClick Methode, für die individuellen Posts
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.followerNr1){
            Intent intentStorie = new Intent(MainActivity.this, Main_Story_Clicked.class);
            intentStorie.putParcelableArrayListExtra("UriList", this.uriList);
            intentStorie.putStringArrayListExtra("TitelList", this.titelList);
            startActivityForResult(intentStorie, 101);
        }else if (v.getId() == R.id.profilBild) {
            Intent intentProfil = new Intent(MainActivity.this, ProfilActivity.class);
            startActivity(intentProfil);
        }else if (v.getId() == R.id.refreshBtn) {
            int i = postList.size()-1;
            while(i>=0){
                this.hashTagList.clear();
                String[] pieces = postList.get(i).split(" : ");
                if(pieces[1].equals(user.getUsername())){
                    String ids = pieces[0];
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

                    addPostFragment(bitmap, titel, hashlist, date, id, liked);

                }

                i--;

            }
            postList.clear();
        }else if(v.getId() == R.id.testBtn){
            String dstAddress = "http://intranet-secure.de/instragram/upload/1529073.jpeg";
            HttpConnection httpConnection = new HttpConnection(dstAddress, this);
            httpConnection.execute();
        }
    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}