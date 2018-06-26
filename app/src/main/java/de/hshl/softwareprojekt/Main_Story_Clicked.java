package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class Main_Story_Clicked extends AppCompatActivity implements View.OnClickListener, AsyncResponse {
    //Variablen zur Verarbeitung der Inhalte in der Activity
    private ArrayList<Bitmap> bitmapList;
    private ArrayList<String> titelList;
    private ProgressBar progressBar;
    private Button addBtn;
    private Button startBtn;
    private ImageButton deleteBtn;
    private TextView emptyText;
    private TextView titelStory;
    private ImageView storiePic;
    private User user;
    private DatabaseHelperPosts database;
    private int progressBarCount;
    private int i;
    private int id = 0;
    private int checkInt;
    private int start;
    private long userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__storie__clicked);
        Intent data = getIntent();

        //Initialisierung der Objekte zur Darstellung der Stories
        this.emptyText = findViewById(R.id.emptyText);
        this.emptyText.setVisibility(View.INVISIBLE);
        this.bitmapList = new ArrayList<>();
        this.titelList = new ArrayList<>();
        this.database = new DatabaseHelperPosts(this);
        this.user = (User) data.getSerializableExtra("User");
        String userID = data.getStringExtra("User_ID");
        this.userid = Long.parseLong(userID);
        this.addBtn = findViewById(R.id.addBtn);
        this.deleteBtn = findViewById(R.id.deleteStorie);
        this.startBtn = findViewById(R.id.startStorie);
        this.addBtn.setOnClickListener(this);
        this.deleteBtn.setOnClickListener(this);
        this.startBtn.setOnClickListener(this);
        this.start = 0;
        this.checkInt = 0;

        //Initialisierung der Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar5);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        emptyText.post(new Runnable() {
            @Override
            public void run() {
                if(user.getId()!=userid){
                    updateStoryboard(userid);
                    addBtn.setVisibility(View.INVISIBLE);
                    deleteBtn.setVisibility(View.INVISIBLE);
                }
                else{
                    ArrayList<String> storyList = database.getStory(user.getId());
                    if (storyList.size() == 0) {
                        updateStoryboard(user.getId());
                    } else {

                        String story = storyList.get(0);
                        String[] pieces = story.split(" : ");
                        String ids = pieces[0];
                        int id = Integer.parseInt(ids);
                        String base64 = pieces[2];
                        String[] base64Strings = base64.split(":");
                        int i = 1;
                        ArrayList<Bitmap> base64Bitmap = new ArrayList<>();
                        while (i < base64Strings.length) {
                            base64Bitmap.add(ImageHelper.base64ToBitmap(base64Strings[i]));
                            i++;
                        }
                        String titel = pieces[3];
                        String[] titleStringsSplit = titel.split(":");
                        int d = 1;
                        ArrayList<String> titleStrings = new ArrayList<>();

                        while (d < titleStringsSplit.length) {
                            titleStrings.add(titleStringsSplit[d]);
                            d++;
                        }
                        addStory(base64Bitmap, titleStrings);
                    }
                }

            }
        });

    }
    //Added das Storyfragment
    public void addStory(ArrayList<Bitmap> bitmapList, ArrayList<String> titelList) {
            this.bitmapList = bitmapList;
            this.titelList = titelList;
            this.checkInt++;
            this.emptyText.setVisibility(View.INVISIBLE);

            //Initialisiert den FragmentManager, das PostFragment und das FrameLayout
            final FragmentManager storieManager = getSupportFragmentManager();
            final StoriesFragment storiesFragment = new StoriesFragment();

            //add fragment
            final FragmentTransaction storieTransaction = storieManager.beginTransaction();
            storieTransaction.add(R.id.storieConstraints, storiesFragment);
            storieTransaction.commitNow();
            storiesFragment.addStr(this.bitmapList.get(0), this.titelList.get(0));

            this.titelStory = storiesFragment.storieTitel;
            this.progressBar = storiesFragment.prBar;
            this.storiePic = storiesFragment.storieImage;

    }
    //Enthält die Methode zur Visualisierung der Progressbar
    public void startBar() {
        this.progressBarCount = this.bitmapList.size();
        final ImageView storiePic = this.storiePic;
        new Thread(new Runnable() {
            @Override
            public void run() {
                i = 0;
                while (i < progressBarCount) {

                    storiePic.post(new Runnable() {

                        @Override
                        public void run() {
                            storiePic.setImageBitmap(bitmapList.get(i));
                            titelStory.setText(titelList.get(i));
                            i++;
                        }
                    });
                    progressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress((i) * 100 / progressBarCount);
                        }
                    });
                    SystemClock.sleep(2000);

                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBtn:
                Intent sendtoStorieBearbeitung = new Intent(Main_Story_Clicked.this, Stories_BearbeitungsActivity.class);
                sendtoStorieBearbeitung.putExtra("User", this.user);
                this.id = database.getID(this.user.getId());
                sendtoStorieBearbeitung.putExtra("id", this.id);
                startActivityForResult(sendtoStorieBearbeitung, 101);
                this.bitmapList.clear();
                this.titelList.clear();
                if(id != 0) {
                    this.storiePic.setVisibility(View.INVISIBLE);
                    this.progressBar.setVisibility(View.INVISIBLE);
                    this.titelStory.setVisibility(View.INVISIBLE);
                    this.emptyText.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.deleteStorie:
                deleteProcess();
                break;
            case R.id.startStorie:


                if(start == 0){
                    start++;
                    startBar();
                }
                else {
                    startBar();
                }
                break;
        }
    }
    //Sorgt beim Delete für eine Leerung der ArrayLists und schaltet Objekte auf INVISIBLE
    public void deleteProcess(){
            /*this.bitmapList.clear();
            this.titelList.clear();
            this.storiePic.setVisibility(View.INVISIBLE);
            this.progressBar.setVisibility(View.INVISIBLE);
            this.titelStory.setVisibility(View.INVISIBLE);
            this.emptyText.setVisibility(View.VISIBLE);
            */
            database.deleteStory(user.getId());
            deleteStory(user.getId());
    }

    //Kümmert sich um den Klick auf den BackButton
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent sendBackIntent = new Intent(Main_Story_Clicked.this, MainActivity.class);
            setResult(RESULT_OK, sendBackIntent);
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
    private void deleteStory(long id){
        String dstAdress = "http://intranet-secure.de/instragram/deleteStory.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress, this);
        httpConnection.setMessage(XmlHelper.getUsers(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }
    private void updateStoryboard(long id){
        String dstAdress = "http://intranet-secure.de/instragram/getStory.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress, this);
        httpConnection.setMessage(XmlHelper.getUsers(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Verarbeitung der Image Capture Request
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Intent intentGet = data;
                    this.id = intentGet.getIntExtra("id",0);
                    this.bitmapList.clear();
                    start++;

                    ArrayList<String> storyList = database.getStory(user.getId()) ;
                    String story = storyList.get(0);
                    String[] pieces = story.split(" : ");
                    String ids = pieces[0];
                    int id = Integer.parseInt(ids);
                    String base64 =  pieces[2];
                    String[] base64Strings = base64.split(":");
                    int i = 1;
                    ArrayList<Bitmap> base64Bitmap = new ArrayList<>();
                    while(i < base64Strings.length) {
                        base64Bitmap.add(ImageHelper.base64ToBitmap(base64Strings[i]));
                        i++;
                    }
                    String titel = pieces[3];
                    String[] titleStringsSplit = titel.split(":");
                    int d = 1;
                    ArrayList<String> titleStrings = new ArrayList<>();

                    while(d < titleStringsSplit.length){
                        titleStrings.add(titleStringsSplit[d]);
                        d++;
                    }
                    addStory(base64Bitmap, titleStrings);
                    }
                }
            }
        }

    @Override
    public void processFinish(String output) {
        if(output.contains("StoryReturn")){
            ArrayList<String> titelList = new ArrayList<>();
            ArrayList<Bitmap> bitmapList = new ArrayList<>();
            String[] storyPieces = output.split(" : ");
            String[] titelsPieces = storyPieces[2].split(":");
            String[] base64Pieces = storyPieces[3].split(":");
            int index = 1;
            while(index<titelsPieces.length){
                titelList.add(titelsPieces[index]);
                index++;
            }
            index = 1;
            while(index<base64Pieces.length){
                bitmapList.add(ImageHelper.base64ToBitmap(base64Pieces[index]));
                index++;
            }
            addStory(bitmapList,titelList);
            int id = Integer.valueOf(storyPieces[1]);
            if(database.getID(user.getId()) == 0 && user.getId() == userid){
                database.insertStory(id, this.user.getUsername(), storyPieces[3], storyPieces[2], "", "", true, this.user.getId());
            }else if(database.getID(user.getId()) != 0 && user.getId() == userid){
                database.updateStory(user.getId(),storyPieces[3],storyPieces[2]);
            }

        }else{
            emptyText.setText("Die Story ist leer.");
            emptyText.setVisibility(View.VISIBLE);
        }

    }
}