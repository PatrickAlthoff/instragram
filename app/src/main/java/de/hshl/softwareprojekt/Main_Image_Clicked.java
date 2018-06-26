package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Main_Image_Clicked extends AppCompatActivity implements View.OnClickListener, AsyncResponse {
    //Variablen zur Verarbeitung der Inhalte in der Activity
    private String ID;
    private ImageView clickedImage;
    private TextView kommentar;
    private TextView titel;
    private TextView disUser;
    private TextView hashTags;
    private EditText editKomm;
    private Bitmap getBitmap;
    private CheckBox checkLike;
    private User user;
    private GridLayout gridLayout;
    private ArrayList<String> hashList;
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
        this.disUser = findViewById(R.id.displayUser);
        this.hashTags = findViewById(R.id.hashTags);
        this.gridLayout = findViewById(R.id.gridLayout);
        //Enthält weitere Informationen für den individuellen Post
        this.kommentar.setOnClickListener(this);
        this.hashTags.setOnClickListener(this);
        this.titel.setText(getPost.getStringExtra("Titel"));
        this.user = (User) getPost.getSerializableExtra("User");
        String username = getPost.getStringExtra("Username");
        this.disUser.setText(username);
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
                this.editKomm.requestFocus();
                this.editKomm.setText("");
                break;
            case R.id.hashTags:
                this.editKomm.setVisibility(View.INVISIBLE);
                this.gridLayout.setVisibility(View.VISIBLE);
                int i = 0;
                while(i<this.hashList.size()){
                    final TextView textview = new TextView(this);
                    textview.setText(this.hashList.get(i));
                    textview.setTextSize(12);
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
        HttpConnection httpConnection = new HttpConnection(dstAdress, this);
        httpConnection.setMessage(XmlHelper.buildXMLUpdateStatus(status, id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    @Override
    public void processFinish(String output) {
        if(output.contains("Update erfolgreich.")) {
            String returner = output;
        }
    }
}