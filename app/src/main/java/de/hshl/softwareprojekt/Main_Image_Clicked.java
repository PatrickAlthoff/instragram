package de.hshl.softwareprojekt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class Main_Image_Clicked extends AppCompatActivity implements View.OnClickListener, AsyncResponse {
    //Variablen zur Verarbeitung der Inhalte in der Activity
    private String ID;
    private ImageView clickedImage;
    private TextView kommentar;
    private TextView titel;
    private TextView hashTags;
    private EditText editKomm;
    private Bitmap getBitmap;
    private CheckBox checkLike;
    private Button subButton;
    private User user;
    private GridLayout gridLayout;
    private LinearLayout underKomm;
    private ArrayList<String> hashList;
    private ArrayList<TextView> hashTextViewList;
    private DatabaseHelperPosts dataBasePosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__image__clicked);
        Intent getPost = getIntent();
        //Ruft die übergebene Bitmap aus dem Intent auf und speichert sie in einem ImageView
        this.clickedImage = findViewById(R.id.clickedImage);
        this.getBitmap =  getPost.getParcelableExtra("BitmapImage");
        this.clickedImage.setImageBitmap(this.getBitmap);
        //Initialisierung der Inhalte der Activity
        this.kommentar = findViewById(R.id.clickedKomment);
        this.titel = findViewById(R.id.titel);
        this.editKomm = findViewById(R.id.editKomment);
        this.hashTags = findViewById(R.id.hashTags);
        this.gridLayout = findViewById(R.id.gridLayout);
        this.subButton = findViewById(R.id.submitButton);
        this.underKomm = findViewById(R.id.underKomm);
        //Enthält weitere Informationen für den individuellen Post
        this.subButton.setOnClickListener(this);
        this.kommentar.setOnClickListener(this);
        this.hashTags.setOnClickListener(this);
        this.titel.setText(getPost.getStringExtra("Titel"));
        this.user = (User) getPost.getSerializableExtra("User");
        this.checkLike = findViewById(R.id.clickedLike);
        this.checkLike.setText(getPost.getStringExtra("Likes"));
        this.checkLike.setChecked(getPost.getExtras().getBoolean("Checked"));
        this.checkLike.setContentDescription(getPost.getStringExtra("ID"));
        this.checkLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Teilt den Text der CheckBox in 2 Teile, damit der Integerwert hochgezählt werden kann
                boolean checked = ((CheckBox) v).isChecked();
                String getCount = ((CheckBox) v).getText().toString();
                String[] pieces = getCount.split(": ");
                int getInt = Integer.parseInt(pieces[1]);

                if(checked){
                    long id = Long.parseLong(v.getContentDescription().toString());
                    if(dataBasePosts.getLikeCount(id,user.getUsername())==0){
                        dataBasePosts.insertIntoLikeCount(id, user.getUsername(),true);
                    }else if(dataBasePosts.getLikeCount(id,user.getUsername())==1){
                        dataBasePosts.updateLike(id,user.getUsername(), true);
                    }
                    ((CheckBox) v).setText("Likes: " + (getInt + 1));
                    updateLikeStatus(1, id);

                }
                else{
                    long id = Long.parseLong(v.getContentDescription().toString());
                    dataBasePosts.updateLike(id,user.getUsername(), false);
                    ((CheckBox) v).setText("Likes: " + (getInt - 1));
                    updateLikeStatus(-1, id);
                }
            }
        });
        //Enthält die ID der Checkbox, für die korrekte Übermittlung
        this.hashTextViewList = new ArrayList<>();
        this.hashList = getPost.getStringArrayListExtra("Hashtags");
        this.dataBasePosts = new DatabaseHelperPosts(this);
        //Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clickedKomment:
            //Setzt die Sichtbarkeit des EditText auf VISIBLE und leert den Inhalt
                this.gridLayout.setVisibility(View.INVISIBLE);
                this.editKomm.setVisibility(View.VISIBLE);
                this.subButton.setVisibility(View.VISIBLE);
                this.editKomm.requestFocus();
                this.editKomm.setText("");
                int d = 0;
                while(d<this.hashTextViewList.size()){
                    this.hashTextViewList.get(d).setVisibility(View.INVISIBLE);
                    d++;
                }
                long idPost = Long.parseLong(this.checkLike.getContentDescription().toString());
                getKommentar(idPost);
                break;
            case R.id.hashTags:
                this.editKomm.setVisibility(View.INVISIBLE);
                this.subButton.setVisibility(View.INVISIBLE);
                this.gridLayout.setVisibility(View.VISIBLE);
                int i = 0;
                if(this.hashTextViewList.size()>0){
                    while(i<this.hashTextViewList.size()){
                        this.hashTextViewList.get(i).setVisibility(View.VISIBLE);
                        i++;
                    }
                }else{
                    while(i<this.hashList.size()){
                        final TextView textview = new TextView(this);
                        this.hashTextViewList.add(textview);
                        textview.setText(this.hashList.get(i));
                        textview.setTextSize(14);
                        this.gridLayout.addView(textview);
                        textview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent sendToSearchIntent = new Intent(Main_Image_Clicked.this, SearchActivity.class);
                                sendToSearchIntent.putExtra("Search", textview.getText().toString());
                                sendToSearchIntent.putExtra("User", user);
                                startActivity(sendToSearchIntent);
                                finish();
                            }
                        });
                        i++;
                    }
                }

                break;
            case R.id.submitButton:
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if(editKomm.getText().length()>10){
                    long id = Long.parseLong(this.checkLike.getContentDescription().toString());
                    uploadKommentar(id,editKomm.getText().toString());
                    addKommentar(ImageHelper.base64ToBitmap(user.getBase64()), user.getUsername(), editKomm.getText().toString(), System.currentTimeMillis());
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }else{
                    this.editKomm.setError("Kommentare müssen mind. 10 Charakter lang sein.");
                }

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent sendBackIntent = new Intent(Main_Image_Clicked.this, MainActivity.class);
            sendBackIntent.putExtra("ID", this.ID);
            sendBackIntent.putExtra("Likes", this.checkLike.getText().toString());
            sendBackIntent.putExtra("Checked", this.checkLike.isChecked());
            setResult(RESULT_OK,sendBackIntent);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateLikeStatus(int status, long id){
        String dstAdress = "http://intranet-secure.de/instragram/updateLikeStatus.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateStatus(status, id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }
    private void getKommentar(long id){
        String dstAdress = "http://intranet-secure.de/instragram/getKommentare.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getKommentar(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }
    private void uploadKommentar(long id, String kommentar){
        String dstAdress = "http://intranet-secure.de/instragram/uploadKomment.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.uploadKommentar(id,user.getUsername(),user.getBase64(), kommentar, System.currentTimeMillis()));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.execute();
    }

    @Override
    public void processFinish(String output) {
        if(output.contains("Update erfolgreich.")) {
            String returner = output;
        }else if (output.contains("KommentarUploaded")){

        }else if (output.contains("getKommentare")){
            String[] outputPieces = output.split(" : ");
            String[] usernamePieces = outputPieces[1].split(":");
            String[] userPicPieces = outputPieces[2].split(":");
            String[] kommentarPieces = outputPieces[3].split(":_:");
            String[] postTimePieces = outputPieces[4].split(":");
            int i = 0;
            while(usernamePieces.length>i){
                addKommentar(ImageHelper.base64ToBitmap(userPicPieces[i]),usernamePieces[i], kommentarPieces[i], Long.parseLong(postTimePieces[i]));
                i++;
            }
        }
    }
    public void addKommentar(Bitmap profilPic, String username, String kommentar, long kommentTime) {

        //Initialisiert den FragmentManager, das PostFragment und das FrameLayout
        FragmentManager fragmentManagerKommentar = getSupportFragmentManager();
        KommentarFragment kommentarFragment = new KommentarFragment();
        FrameLayout frameInner = new FrameLayout(this);
        frameInner.setId(View.generateViewId());
        underKomm.addView(frameInner);

        final FragmentTransaction fragmentTransactionKommentar = fragmentManagerKommentar.beginTransaction();
        fragmentTransactionKommentar.add(frameInner.getId(), kommentarFragment);

        fragmentTransactionKommentar.commitNow();
        kommentarFragment.creatKomment(profilPic,username,kommentar, kommentTime);
    }
}