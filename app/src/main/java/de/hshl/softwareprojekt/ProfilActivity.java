package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.Bitmap;

import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ProfilActivity extends AppCompatActivity implements OnClickListener, AsyncResponse {

    private static final String TAG = "ProfilActivity";
    private ImageView profilbild;
    private TextView follower;
    private TextView following;
    private TextView benutzerName;
    private Button folgen;
    private Button entfolgen;
    private User user;
    private String followerList;
    private String followsList;
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        Intent getIntent = getIntent();

        this.follower = findViewById(R.id.follower);
        this.following = findViewById(R.id.following);
        this.profilbild = findViewById(R.id.profilbild);
        this.benutzerName = findViewById(R.id.benutzerName);
        this.folgen = findViewById(R.id.folgen);
        this.entfolgen = findViewById(R.id.entfolgen);
        this.user= (User) getIntent.getSerializableExtra("User");
        if(getIntent.getIntExtra("Code", 1)== 2){
            this.folgen.setVisibility(View.INVISIBLE);
            this.entfolgen.setVisibility(View.INVISIBLE);
            this.benutzerName.setText(this.user.getUsername());
            profilbild.setImageDrawable(roundImage(ImageHelper.base64ToBitmap(user.getBase64())));
            updateFollower(user.getId());
        }
        else{
            id = Long.parseLong(getIntent.getStringExtra("UserKey"));
            getUserData(id);
            updateFollower(id);
        }
        final DatabaseHelperFollow dbHelper = new DatabaseHelperFollow(this);
        // folgen.setOnClickListener(this);
        // isFollowing();
        GridView gridView = (GridView) findViewById(R.id.gridViewBilder);
        gridView.setAdapter(new GridAdapter(this));

        // einzelnes Bild groß anzeigen lassen
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {


                Intent i = new Intent(getApplicationContext(), Full_ImageActivity.class);

                i.putExtra("id", position);
                startActivity(i);
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

        // stellt Profilbild rund dar

    }
    public RoundedBitmapDrawable roundImage(Bitmap bitmap){
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;
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
    private void getUserData(long query){
        String dstAdress = "http://intranet-secure.de/instragram/getUserData.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getUsers(query));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }
    //überprüft ob man dem Nutzer folgt
    private void isFollowing() {
        Log.d(TAG, "Überprüft ob dem Nutzer gefolgt wird");

    }

    // zeigt an, dass man dem Nutzer folgt
    private void setFollowing() {


    }

    // zeigt an, dass man dem Nutzer nicht folgt
    private void setUnfollowing() {

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
        return super.onOptionsItemSelected(item);
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
        }else if(output.contains("FollowExc")) {
            Toast.makeText(getApplicationContext(), "Du folgst der Person schon!", Toast.LENGTH_SHORT).show();
        }else if(output.contains("UnfollowNotPossible")) {
            Toast.makeText(getApplicationContext(), "Du musst der Person vorher folgen!", Toast.LENGTH_SHORT).show();
        }else if(output.contains("Unfollowed")) {
            Toast.makeText(getApplicationContext(), "Du folgst der Person nun nicht mehr!", Toast.LENGTH_SHORT).show();
            updateFollower(id);
        }
    }
}



