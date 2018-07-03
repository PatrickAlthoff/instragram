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
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AsyncResponse {

    private static final int PERMISSION_REQUEST = 1;
    //Variablen zur Verarbeitung der Inhalte in der Activity
    private boolean permissionGranted;
    private int TITLE = 2;
    private int DESCRIPTION = 3;
    private int IMAGE_CAPTURE = 4;
    private int GALLERY_PICK = 5;
    private int BEARBEITUNG_CODE = 12;
    private int REQUEST_GETSEND = 12;
    private int IMAGE_CLICKED = 13;
    private long[] splitIDs;
    private int index = 0;
    private int fragmentIndex = 0;
    private int rememberIndex = 0;
    private long newestPost;
    private float dpi;
    private String FollowsList;
    private String FollowListWithoutSelf = "";
    private Uri imageUri;
    private User user;
    private Intent intentCaptureImage;
    private ImageView profilBild;
    private ImageView followerBild;
    private TextView profilName;
    private TextView followerCount;
    private TextView followsCount;
    private SearchView searchView;
    private ScrollView scrollView;
    private LinearLayout innerLayout;
    private LinearLayout horiInner;
    private ArrayList<ImageView> followerList;
    private ArrayList<TextView> textViewList;
    private ArrayList<String> hashTagList;
    private ArrayList<String> postList;
    private ArrayList<PostFragment> postFragmentArrayList;
    private DatabaseHelperPosts dataBasePosts;

    //Methode um die Display Auflösung zu erhalten
    private void getDisplayMetrics() {
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
        this.followerCount = findViewById(R.id.followerTextView);
        this.followsCount = findViewById(R.id.followsTextView);
        this.scrollView = findViewById(R.id.scrollView);

        this.followerBild.setOnClickListener(this);
        this.profilBild.setOnClickListener(this);
        this.postFragmentArrayList = new ArrayList<>();
        this.followerList = new ArrayList<>();
        this.textViewList = new ArrayList<>();
        this.hashTagList = new ArrayList<>();

        this.followerBild.setImageDrawable(roundImage(ImageHelper.base64ToBitmap(user.getBase64())));
        this.profilName.setText(this.user.getUsername());
        this.dataBasePosts = new DatabaseHelperPosts(this);
        this.postList = this.dataBasePosts.getData();
        updateFollower(user.getId());
        this.profilBild.setImageDrawable(roundImage(ImageHelper.base64ToBitmap(user.getBase64())));
        //Sendet nach 2000 Millsec. eine Anfrage an den Server um alle Posts zu erhalten
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                getAllPostIDs(FollowsList);
            }
        }).start();

        //Enthält Funktion zum Nachladen der Posts, wenn der User am Ende des Scrolls angekommnen ist
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);

                scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (scrollView != null && splitIDs.length > 4) {
                            if (scrollView.getChildAt(0).getBottom() == (scrollView.getHeight() + scrollView.getScrollY())) {
                                int precheck = index - 3;
                                int d = 3;
                                if (precheck >= 0) {
                                    fragmentIndex = rememberIndex;
                                    while (d >= 0) {
                                        getPostForID(Long.toString(splitIDs[index]));
                                        index--;
                                        d--;
                                    }
                                } else {

                                    while (index >= 0) {
                                        getPostForID(Long.toString(splitIDs[index]));
                                        index--;
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }).start();

    }

    //Fügt der Frontpage ein individuelles Post Fragment hinzu
    public void addPostFragment(Bitmap postBitmap, final String username, String titel, ArrayList<String> hashlist, String date, long id, int liked, Bitmap userPic, String userKey, String shares, String shareName) {

        //Initialisiert den FragmentManager, das PostFragment und das FrameLayout
        final FragmentManager fragmentManagerPost = getSupportFragmentManager();
        final PostFragment frontPagePost = new PostFragment();
        FrameLayout frameInner = new FrameLayout(this);
        frameInner.setId(View.generateViewId());
        innerLayout.addView(frameInner, fragmentIndex);

        //add fragment
        String i = String.valueOf(id);

        final FragmentTransaction fragmentTransactionPost = fragmentManagerPost.beginTransaction();
        fragmentTransactionPost.add(frameInner.getId(), frontPagePost, i);

        fragmentTransactionPost.commitNow();
        frontPagePost.addPost(postBitmap, titel);
        final ArrayList<String> hashList = hashlist;
        TextView datefield = frontPagePost.timeStampView;
        datefield.setText(date);
        //Gib den ImageViews eine generierte ID und fügt einen OnClick Listener hinzu

        ImageView userImage = frontPagePost.profilPicPost;
        userImage.setImageDrawable(roundImage(userPic));
        userImage.setContentDescription(userKey);
        //Verarbeitet das OnClick Event für den Klick auf das Profilbild
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToProfil = new Intent(MainActivity.this, ProfilActivity.class);
                goToProfil.putExtra("UserKey", v.getContentDescription().toString());
                goToProfil.putExtra("Code", 3);
                goToProfil.putExtra("User", user);
                goToProfil.putExtra("FollowList", FollowListWithoutSelf);
                startActivity(goToProfil);
            }
        });
        ImageView postImage = frontPagePost.postImage;
        postImage.setId(View.generateViewId());
        postImage.setContentDescription(i);
        //Verarbeitet das OnClick Event für den Klick auf das Postbild
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View nextChild = ((ViewGroup) v.getParent()).getChildAt(4);
                View titelView = ((ViewGroup) v.getParent()).getChildAt(3);
                Boolean checked = ((CheckBox) nextChild).isChecked();
                String titel = ((TextView) titelView).getText().toString();
                String likes = ((CheckBox) nextChild).getText().toString();
                int ID = nextChild.getId();
                //Baut aus den Daten im Cache eine Bitmap
                v.setDrawingCacheEnabled(true);
                v.buildDrawingCache();
                Bitmap parseBit = v.getDrawingCache();

                //Skaliert die Oben gebaute Bitmap auf ein kleineres Format
                Bitmap createBit = scaleBitmap(parseBit, -1, 300);

                //Fügt dem Intent für die Vollansicht die Bitmap + einen Titel hinzu
                Intent intentVollansicht = new Intent(MainActivity.this, Main_Image_Clicked.class);
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
        if (like == 2) {
            likeCheck.setChecked(true);

        } else {
            likeCheck.setChecked(false);
        }
        String ID = String.valueOf(id);
        likeCheck.setText("Likes: " + (liked));
        likeCheck.setId(Integer.parseInt(i));
        likeCheck.setContentDescription(ID);
        //Verarbeitet OnClick Event beim Check der Likebox
        likeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                String getCount = ((CheckBox) v).getText().toString();
                String[] pieces = getCount.split(": ");
                int getInt = Integer.parseInt(pieces[1]);
                if (checked) {
                    String idString = v.getContentDescription().toString();
                    long id = Long.parseLong(idString);
                    if (dataBasePosts.getLikeCount(id, user.getUsername()) == 0) {
                        dataBasePosts.insertIntoLikeCount(id, user.getUsername(), true);
                    } else if (dataBasePosts.getLikeCount(id, user.getUsername()) == 1) {
                        dataBasePosts.updateLike(id, user.getUsername(), true);
                    }
                    ((CheckBox) v).setText("Likes: " + (getInt + 1));
                    updateLikeStatus(1, id);

                } else {
                    String idString = v.getContentDescription().toString();
                    long id = Long.parseLong(idString);
                    dataBasePosts.updateLike(id, user.getUsername(), false);
                    ((CheckBox) v).setText("Likes: " + (getInt - 1));
                    updateLikeStatus(-1, id);
                }
            }
        });
        postFragmentArrayList.add(frontPagePost);
        fragmentIndex++;
        rememberIndex++;
        if (Long.parseLong(userKey) == user.getId() || shareName.equals(user.getUsername())) {
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.INVISIBLE);
        }
        deleteButton.setContentDescription(i);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment(frontPagePost);
                updateDelete(Long.parseLong(v.getContentDescription().toString()));
            }
        });

        final Bitmap postBitty = postBitmap;
        final Bitmap userPicF = userPic;
        final String titelF = titel;
        final String dateF = date;
        final long userKeyF = Long.parseLong(userKey);
        int index = 0;
        String hashes = "";
        while (index < hashlist.size()) {

            hashes = hashes + ":" + hashlist.get(index);
            index++;

        }
        TextView shareNAME = frontPagePost.shareName;
        if (shareName.equals("")) {
            shareNAME.setVisibility(View.INVISIBLE);
        } else {
            shareNAME.setText("Shared By: " + shareName);
        }

        TextView shareCount = frontPagePost.shareCount;
        shareCount.setText("Shares: " + shares);
        final String hashesF = hashes;
        ImageButton shareButton = frontPagePost.share;
        if (shareName.equals("")) {
            shareButton.setContentDescription(i);
            //Verarbeitet OnClick Event des Share-Buttons
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    updateShare(Long.parseLong(v.getContentDescription().toString()));
                    fragmentIndex = 0;
                    long d = System.currentTimeMillis() / 1000;
                    String y = String.valueOf(d);
                    int c = Integer.parseInt(y);
                    uploadShare(c, username, ImageHelper.bitmapToBase64(postBitty), titelF, hashesF, dateF, false, userKeyF, ImageHelper.bitmapToBase64(userPicF), user.getUsername());

                }
            });
        } else {
            shareButton.setVisibility(View.INVISIBLE);
            shareCount.setVisibility(View.INVISIBLE);
        }
    }

    //Enthält Funktion zum "Runden" einer Bitmap
    public RoundedBitmapDrawable roundImage(Bitmap bitmap) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;
    }

    //Entfernt das Fragment
    public void removeFragment(PostFragment pf) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(pf);
        fragmentTransaction.commitNow();

    }

    //Methode zur Umrechnung der dpi
    private int dp2px(int dp) {
        return (int) (dp * dpi);
    }

    //Öffnet Fenster zur Bestätigung der Zugriffsrechte / Prüft ob dies schon geschehen ist
    protected void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, this.PERMISSION_REQUEST);
            }
        } else {
            this.permissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.permissionGranted = true;

                } else {
                    this.permissionGranted = false;
                }
                return;
        }
    }

    //Methode zum Start der Camera
    private void startCamera() {
        if (this.permissionGranted) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.TITLE, TITLE);
            contentValues.put(MediaStore.Images.Media.DESCRIPTION, DESCRIPTION);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intentCaptureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intentCaptureImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intentCaptureImage, IMAGE_CAPTURE);
        }
    }

    //Methode zum Skallieren der zu übergebenen Bitmap
    private Bitmap getAndScaleBitmap(Uri uri, int dstWidth, int dstHeight) {
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

    //Skaliert eine übergebene Bitmap
    public Bitmap scaleBitmap(Bitmap bitmap, int dstWidth, int dstHeight) {
        float srcWidth = bitmap.getWidth(),
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
                    Intent sendToBearbeitung = new Intent(MainActivity.this, Post_BearbeitungsActivity.class);
                    sendToBearbeitung.putExtra("BitmapImage", myBitmap);
                    sendToBearbeitung.putExtra("User", this.user);
                    startActivityForResult(sendToBearbeitung, BEARBEITUNG_CODE);
                }
            } else {
                int rowsDeleted = getContentResolver().delete(imageUri, null, null);
                Log.d(MainActivity.class.getSimpleName(), rowsDeleted + " rows deleted");
            }
        }

        //Verarbeitung der Gallerie Request
        if (requestCode == GALLERY_PICK) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();

                    Bitmap myBitmap = getAndScaleBitmap(uri, -1, 300);
                    Intent sendToBearbeitung = new Intent(MainActivity.this, Post_BearbeitungsActivity.class);
                    sendToBearbeitung.putExtra("BitmapImage", myBitmap);
                    sendToBearbeitung.putExtra("User", this.user);
                    startActivityForResult(sendToBearbeitung, BEARBEITUNG_CODE);
                }
            } else {
                Log.d(MainActivity.class.getSimpleName(), "no picture selected");
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
                    long d = System.currentTimeMillis() / 1000;
                    String y = String.valueOf(d);
                    int c = Integer.parseInt(y);
                    fragmentIndex = 0;
                    newestPost = d;
                    addPostFragment(postImage, user.getUsername(), titel, this.hashTagList, date, d, 0, ImageHelper.base64ToBitmap(user.getBase64()), String.valueOf(user.getId()), "0", "");
                    int i = 0;
                    String hashes = "";
                    while (i < this.hashTagList.size()) {

                        hashes = hashes + ":" + hashTagList.get(i);
                        i++;
                    }
                    if (hashes == null) {
                        hashes = "#NoHashtags";
                    }
                    String base64Code = ImageHelper.bitmapToBase64(postImage);
                    uploadPost(c, this.user.getUsername(), base64Code, titel, hashes, date, false, this.user.getId(), user.getBase64());

                }
            } else {
                Log.d(MainActivity.class.getSimpleName(), "no picture selected");
            }
        }
        //Enthält das User Objekt aus der Settings Acitivty
        if (requestCode == 111) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Intent intentStorie = data;
                    this.user = (User) intentStorie.getSerializableExtra("User");
                    this.profilName.setText(this.user.getUsername());
                    int i = 0;
                    while (i < textViewList.size()) {
                        textViewList.get(i).setText(user.getUsername());
                        i++;
                    }
                }
            }
        }
        //Enthält Daten aus der Vollbildansicht
        if (requestCode == IMAGE_CLICKED) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Intent intentFromImage = data;
                    int ID = Integer.parseInt(intentFromImage.getStringExtra("ID"));
                    View v = findViewById(ID);
                    ((CheckBox) v).setText(intentFromImage.getStringExtra("Likes"));
                    ((CheckBox) v).setChecked(intentFromImage.getExtras().getBoolean("Checked"));
                }
            }
        }
        //Enthält die Daten, die aus dem Profil kommen
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    user = (User) data.getSerializableExtra("User");
                    profilName.setText(user.getUsername());
                    profilBild.setImageDrawable(roundImage(ImageHelper.base64ToBitmap(user.getBase64())));
                    FollowListWithoutSelf = data.getStringExtra("FollowList");
                    int i = 0;
                    while (i < followerList.size()) {
                        followerList.get(i).setVisibility(View.GONE);
                        i++;
                    }
                    updateFollower(user.getId());
                }
            }
        }
        //aktualisiert die Follower Liste nach der Suche
        if (requestCode == 350) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    int i = 0;
                    while (i < followerList.size()) {
                        followerList.get(i).setVisibility(View.GONE);
                        i++;
                    }
                    updateFollower(user.getId());
                }
            }
        }
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
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        return true;
    }

    //Verarbeitung der Settings Anfrage (noch passiert nichts)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == R.id.action_search) {
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setQueryHint("Search for something...");
            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    // This is your adapter that will be filtered
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    // **Here you can get the value "query" which is entered in the search box.**
                    Intent startSearchIntent = new Intent(MainActivity.this, SearchActivity.class);
                    startSearchIntent.putExtra("User", user);
                    startSearchIntent.putExtra("Search", query);
                    startSearchIntent.putExtra("FollowList", FollowListWithoutSelf);
                    startActivityForResult(startSearchIntent, 350);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    searchView.setQuery("", false);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        } else if (item.getItemId() == R.id.action_refresh) {

            if (rememberIndex == fragmentIndex) {
                rememberIndex = fragmentIndex;
            }

            updateTimeline(FollowsList, newestPost);

        }


        return super.onOptionsItemSelected(item);
    }

    //Methode zur Verarbeitung der Buttons im  Drawer Menü
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //Startet die Kamera
        if (id == R.id.nav_camera) {
            startCamera();

        }
        //Startet die Galerie Ansicht
        else if (id == R.id.nav_gallery) {
            Intent intentGallerie = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentGallerie, GALLERY_PICK);

        }
        //Startet die Story Ansicht
        else if (id == R.id.nav_slideshow) {
            Intent intentStories = new Intent(MainActivity.this, Main_Story_Clicked.class);
            intentStories.putExtra("User", this.user);
            String userID = String.valueOf(this.user.getId());
            intentStories.putExtra("User_ID", userID);
            startActivityForResult(intentStories, 101);
        }
        //Startet das Settingslayout
        else if (id == R.id.nav_manage) {
            Intent intentSetting = new Intent(MainActivity.this, SettingsActivity.class);
            intentSetting.putExtra("User", this.user);
            startActivityForResult(intentSetting, 111);

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
        if (v.getId() == R.id.followerNr1) {
            Intent intentStorie = new Intent(MainActivity.this, Main_Story_Clicked.class);
            intentStorie.putExtra("User", this.user);
            String userID = String.valueOf(this.user.getId());
            intentStorie.putExtra("User_ID", userID);
            startActivityForResult(intentStorie, 101);
        } else if (v.getId() == R.id.profilBild) {
            Intent intentProfil = new Intent(MainActivity.this, ProfilActivity.class);
            intentProfil.putExtra("User", user);
            intentProfil.putExtra("Code", 2);
            intentProfil.putExtra("Follower", this.followerCount.getText().toString());
            intentProfil.putExtra("Follows", this.followsCount.getText().toString());
            intentProfil.putExtra("FollowList", this.FollowListWithoutSelf);
            startActivityForResult(intentProfil, 200);
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //Enthält die Anfrage, einen Post zu teilen
    private void uploadShare(int id, String name, String path, String titel, String hashtags, String date, boolean liked, long userKey, String userPic, String shareName) {
        String dstAdress = "http://intranet-secure.de/instragram/uploadShare.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.uploadShare(id, name, path, titel, hashtags, date, liked, userKey, userPic, shareName));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    //Enthält die Anfrage, den Share Counter hochzuzählen
    private void updateShare(long id) {
        String dstAdress = "http://intranet-secure.de/instragram/updateShare.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateShare(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    //Enthält die Anfrage, einen Post zu löschen
    private void updateDelete(long id) {
        String dstAdress = "http://intranet-secure.de/instragram/updateDelete.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateDelete(id, user.getUsername()));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    //Enthält die Anfrage einen Post hochzuladen
    private void uploadPost(long id, String name, String path, String titel, String hashtags, String date, boolean liked, long userKey, String userPic) {
        String dstAdress = "http://intranet-secure.de/instragram/Upload.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.uploadPost(id, name, path, titel, hashtags, date, liked, userKey, userPic));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.execute();
    }

    //Enthält die Anfrage ein Profilbild zu erhalten
    private void getUserPic(long id) {
        String dstAdress = "http://intranet-secure.de/instragram/getUserPic.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getUsers(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    //Enthält die Abfrage, um die Followerlisten zu aktualisieren
    private void updateFollower(long id) {
        String dstAdress = "http://intranet-secure.de/instragram/getFollower.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getUsers(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    //Enthält die Anfrage, den Like Status eines Posts upzudaten
    private void updateLikeStatus(int status, long id) {
        String dstAdress = "http://intranet-secure.de/instragram/updateLikeStatus.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateStatus(status, id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    //Enthält die Anfrage, alle IDs der Posts zu erhalten (Eigene und Usern denen man folgt)
    private void getAllPostIDs(String ids) {
        String dstAdress = "http://intranet-secure.de/instragram/getAllPostIDs.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getFullPost(ids));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    //Enthält die Anfrage, einen Post zu erhalten
    private void getPostForID(String id) {
        String dstAdress = "http://intranet-secure.de/instragram/getPosts.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getFullPost(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    //Enthält die Abfrage, um neuere Posts in die Timeline zu laden
    private void updateTimeline(String follows, long highestPost) {
        String dstAdress = "http://intranet-secure.de/instragram/updateTimeline.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateTimeline(follows, highestPost));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    //Enthält die ProcessFinish Funktion des AsyncResponse Interface
    @Override
    public void processFinish(String output) {
        if (output.contains("UserPic")) {
            String[] input = output.split(":");
            long inputLong = Long.valueOf(input[1]);
            Bitmap bitmap = ImageHelper.base64ToBitmap(input[2]);
            createFollower(inputLong, bitmap);
        }
        //Verarbeitet den String, welcher alle Post IDs enthält
        else if (output.contains("AllPostIDs")) {
            String[] split = output.split(":");
            this.splitIDs = new long[split.length - 1];
            for (int i = 0; i < split.length - 1; i++) {
                this.splitIDs[i] = Long.parseLong(split[i + 1]);
            }
            Arrays.sort(this.splitIDs);
            this.index = this.splitIDs.length - 1;
            if (this.splitIDs.length >= 6) {
                this.newestPost = this.splitIDs[this.splitIDs.length - 1];
                for (int i = 2; i >= 0; i--) {
                    getPostForID(Long.toString(this.splitIDs[this.index]));
                    this.index--;

                }
            } else {
                if (this.splitIDs.length >= 1) {
                    this.newestPost = this.splitIDs[this.splitIDs.length - 1];
                    this.index = splitIDs.length - 1;
                    while (this.index >= 1) {
                        getPostForID(Long.toString(this.splitIDs[this.index]));
                        this.index--;
                    }
                }

            }

        } else if (output.contains("FullPost")) {
            String[] response = output.split(" : ");
            this.postList = new ArrayList<>();
            if (response.length > 1) {

                String ids = response[1];
                String username = response[2];
                long id = Long.parseLong(ids);
                String base64 = response[3];
                Bitmap bitmap = ImageHelper.base64ToBitmap(base64);
                String titel = response[4];
                ArrayList<String> hashlist = new ArrayList<>();
                String[] hashes = response[5].split(":");
                String date = response[6];
                String bool = response[7];
                String userKeyString = response[8];
                String userPic = response[9];
                String shares = response[10];
                String shareName = "";
                if (response.length == 12) {
                    shareName = response[11];
                }
                int like = Integer.valueOf(bool);
                int c = 0;
                while (c < hashes.length) {
                    hashlist.add(hashes[c]);
                    c++;
                }

                addPostFragment(bitmap, username, titel, hashlist, date, id, like, ImageHelper.base64ToBitmap(userPic), userKeyString, shares, shareName);
                this.postList.clear();
            }


        } else if (output.contains("Follower")) {

            String[] firstSplit = output.split(" : ");
            this.followerCount.setText(firstSplit[0]);
            String[] secondSplit = firstSplit[1].split(": ");
            this.FollowsList = String.valueOf(this.user.getId());
            if (secondSplit.length > 1) {
                this.FollowsList = secondSplit[1] + this.user.getId();
                this.FollowListWithoutSelf = secondSplit[1];
                String[] thirdSplit = secondSplit[1].split(":");
                this.followsCount.setText("Follows: " + (thirdSplit.length));
                int i = 0;
                while (i < thirdSplit.length) {
                    long followerID = Long.parseLong(thirdSplit[i]);
                    getUserPic(followerID);
                    i++;
                }
            } else {
                this.followsCount.setText("Follows: " + "0");
            }
        } else if (output.contains("UpdatePostIDs")) {

            String[] splitNewPost = output.split(":");
            if (splitNewPost.length > 1) {
                int i = splitNewPost.length - 1;
                this.fragmentIndex = 0;
                while (i >= 1) {
                    getPostForID(splitNewPost[i]);
                    i--;
                }
                this.newestPost = Long.parseLong(splitNewPost[splitNewPost.length - 1]);
            }

        } else if (output.contains("NoNewPosts")) {
            Toast.makeText(getApplicationContext(), "Es gibt keine neuen Posts!", Toast.LENGTH_SHORT).show();
        } else if (output.contains("PostDeleted")) {
            Toast.makeText(getApplicationContext(), "Dein Post wurde erfolgreich gelöscht.", Toast.LENGTH_SHORT).show();
        } else if (output.contains("SharedSuccessfully")) {
            Toast.makeText(getApplicationContext(), "Post wurde geteilt!", Toast.LENGTH_SHORT).show();
        }
    }

    //Enthält die Funktion um einen Follower zu erzeugen (Story Picture)
    public void createFollower(long id, Bitmap bitmap) {
        ImageView followerPic = new ImageView(this);
        this.horiInner.addView(followerPic);
        followerPic.getLayoutParams().height = 155;
        followerPic.getLayoutParams().width = 155;
        String descriLong = String.valueOf(id);
        followerPic.setImageDrawable(roundImage(bitmap));
        followerPic.setId(View.generateViewId());
        followerPic.setContentDescription(descriLong);
        this.followerList.add(followerPic);
        followerPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStorie = new Intent(MainActivity.this, Main_Story_Clicked.class);
                intentStorie.putExtra("User", user);
                intentStorie.putExtra("User_ID", v.getContentDescription());
                startActivityForResult(intentStorie, 101);
            }
        });
    }
}