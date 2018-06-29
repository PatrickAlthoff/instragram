package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.PorterDuff;
import android.media.Image;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class ProfilActivity extends AppCompatActivity implements OnClickListener, AsyncResponse {

    private DatabaseHelperPosts dataBasePosts;
    private ImageView profilbild;
    private TextView follower;
    private TextView following;
    private TextView benutzerName;
    private TextView beiträgeField;
    private Button folgen;
    private Button entfolgen;
    private User user;
    private String followerList;
    private String followsList;
    private GridView gridView;
    private ArrayList<Bitmap> imageViewArrayList;
    private ArrayList<String> idList;
    private ProfilAdapter profilAdapter;
    private long id;
    private int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        Intent getIntent = getIntent();

        this.beiträgeField = findViewById(R.id.beiträgeField);
        this.follower = findViewById(R.id.follower);
        this.following = findViewById(R.id.following);
        this.profilbild = findViewById(R.id.profilbild);
        this.benutzerName = findViewById(R.id.benutzerName);
        this.folgen = findViewById(R.id.folgen);
        this.entfolgen = findViewById(R.id.entfolgen);
        this.user= (User) getIntent.getSerializableExtra("User");
        this.code = getIntent.getIntExtra("Code", 1);
        if(this.code == 3){
            id = Long.parseLong(getIntent.getStringExtra("UserKey"));
            getUserData(id);
            updateFollower(id);
            getUserPosts(id);
        }
        else if(this.code == 2){
            this.folgen.setVisibility(View.INVISIBLE);
            this.entfolgen.setVisibility(View.INVISIBLE);
            this.benutzerName.setText(this.user.getUsername());
            profilbild.setImageDrawable(roundImage(ImageHelper.base64ToBitmap(user.getBase64())));
            updateFollower(user.getId());
            getUserPosts(user.getId());
        }

        this.dataBasePosts = new DatabaseHelperPosts(this);
        this.imageViewArrayList = new ArrayList<>();
        this.idList = new ArrayList<>();
        this.gridView = findViewById(R.id.gridViewBilder);
        this.profilAdapter = new ProfilAdapter(this,imageViewArrayList, idList);
        this.gridView.setAdapter(profilAdapter);


        // einzelnes Bild groß anzeigen lassen
        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                String contentDis = ((ViewGroup)v).getChildAt(0).getContentDescription().toString();
                getFullPost(contentDis);
            }
        });


        // Zeigt Liste Following
        following.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, ListeFollowing.class);
                intent.putExtra("FollowsList", followsList);
                intent.putExtra("User", user);
                startActivity(intent);

            }
        });
        //Zeigt Liste Followern
        follower.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, ListeFollower.class);
                intent.putExtra("FollowerList",followerList);
                intent.putExtra("User", user);
                startActivity(intent);

            }
        });

        // Person folgen
        folgen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFollowStatus(user.getId(),id);

            }
        });

        // Person entfolgen
        entfolgen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateunfollowStatus(user.getId(),id);


            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar11);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        // stellt Profilbild rund dar

    }
    public RoundedBitmapDrawable roundImage(Bitmap bitmap){
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;
    }
    private void getFullPost(String id){
        String dstAdress = "http://intranet-secure.de/instragram/getFullPost.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getFullPost(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }
    private void updateFollower(long id){
        String dstAdress = "http://intranet-secure.de/instragram/getFollower.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getUsers(id));
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
    private void getUserData(long id){
        String dstAdress = "http://intranet-secure.de/instragram/getUserData.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getUsers(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }
    private void getUserPosts(long id){
        String dstAdress = "http://intranet-secure.de/instragram/getPostPicture.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getUsers(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    // erstellt Menuleiste
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater MenuInflater = getMenuInflater();
        MenuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // wenn Profil bearbeiten
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent back = new Intent(ProfilActivity.this,Profil_BearbeitungActivity.class);
            back.putExtra("User", user);
            setResult(RESULT_OK, back);
            finish(); // close this activity and return to preview activity (if there is any)
        }else{
            Intent intent = new Intent(ProfilActivity.this,Profil_BearbeitungActivity.class);
            intent.putExtra("User", user);
            startActivityForResult(intent, 200);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Verarbeitung der Image Capture Request
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    user = (User) data.getSerializableExtra("User");
                    benutzerName.setText(user.getUsername());
                    profilbild.setImageDrawable(roundImage(ImageHelper.base64ToBitmap(user.getBase64())));

                }
            } else {

                Log.d(MainActivity.class.getSimpleName(),  "UserUpdated");
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void processFinish(String output) {
        if(output.contains("UserData")){
            String[] userDataSplit = output.split(" : ");
            this.profilbild.setImageDrawable(roundImage(ImageHelper.base64ToBitmap(userDataSplit[2])));
            this.benutzerName.setText(userDataSplit[1]);

        }else if(output.contains("Follower: ")){
            String[] firstSplit = output.split( " : " );
            this.follower.setText(firstSplit[0]);
            String[] secondSplit =  firstSplit[1].split(": ");
            if(firstSplit.length>2){
                this.followerList = firstSplit[2];
            }
            if(secondSplit.length >1){
                this.followsList = secondSplit[1];
                String[] thirdSplit = secondSplit[1].split(":");
                this.following.setText(secondSplit[0] +": "+ (thirdSplit.length));
            }else{
                this.following.setText(secondSplit[0] +": "+ "0");

            }
        }else if(output.contains("Followed")) {
            Toast.makeText(getApplicationContext(), "Du folgst nun dieser Person!", Toast.LENGTH_SHORT).show();
            updateFollower(id);
        }else if(output.contains("PostIDs")){
            String[] firstSplit = output.split(":");
            String postCount = firstSplit[1];
            beiträgeField.setText(beiträgeField.getText().toString() + postCount);
            int i = firstSplit.length-1;
            while(i>1){
                getUserPosts(Long.parseLong(firstSplit[i]));
                i--;
            }
        }else if(output.contains("PostPic")){
            String[] firstSplit = output.split(":");
            String base64 = firstSplit[1];
            Bitmap bitmap = ImageHelper.base64ToBitmap(base64);
            imageViewArrayList.add(bitmap);
            idList.add(firstSplit[2]);
            profilAdapter.notifyDataSetChanged();
        }else if (output.contains("FullPost")){
            String[] pieces = output.split(" : ");
            String id = pieces[2];
            Bitmap image = ImageHelper.base64ToBitmap(pieces[3]);
            String titel = pieces[4];
            String[] hashes = pieces[5].split(":");
            ArrayList<String> hashList = new ArrayList<>();
            int i = 1;
            while(i<hashes.length){
                hashList.add(hashes[i]);
                i++;
            }
            String likes = "Likes: " + pieces[6];
            int like = dataBasePosts.getLikeCount(Long.parseLong(id), user.getUsername());
            boolean checked;
            if(like==2){
                checked = true;

            }else{
                checked = false;
            }
            Intent goToBigImage = new Intent(ProfilActivity.this, Main_Image_Clicked.class);
            goToBigImage.putExtra("BitmapImage", image);
            goToBigImage.putExtra("Titel", titel);
            goToBigImage.putExtra("Likes", likes);
            goToBigImage.putExtra("Checked", checked);
            goToBigImage.putExtra("ID", id);
            goToBigImage.putExtra("User", user);
            goToBigImage.putStringArrayListExtra("Hashtags", hashList);
            startActivity(goToBigImage);
        }
        else if(output.contains("FollowExc")) {
            Toast.makeText(getApplicationContext(), "Du folgst der Person schon!", Toast.LENGTH_SHORT).show();
        }else if(output.contains("UnfollowNotPossible")) {
            Toast.makeText(getApplicationContext(), "Du musst der Person vorher folgen!", Toast.LENGTH_SHORT).show();
        }else if(output.contains("Unfollowed")) {
            Toast.makeText(getApplicationContext(), "Du folgst der Person nun nicht mehr!", Toast.LENGTH_SHORT).show();
            updateFollower(id);
        }
    }
}



