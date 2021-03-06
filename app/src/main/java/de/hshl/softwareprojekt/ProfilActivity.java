package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ProfilActivity extends AppCompatActivity implements AsyncResponse {

    private long id;
    private int code;
    private String followerList;
    private String followsList;
    private String FollowListWithoutSelf;
    private String rememberQuery;
    private User user;
    private ImageView profilbild;
    private TextView follower;
    private TextView following;
    private TextView benutzerName;
    private TextView beiträgeField;
    private TextView beschreibungView;
    private GridView gridView;
    private CheckBox aboBox;
    private ProfilAdapter profilAdapter;
    private DatabaseHelperPosts dataBasePosts;
    private ArrayList<Bitmap> imageViewArrayList;
    private ArrayList<String> idList;

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
        this.beschreibungView = findViewById(R.id.beschreibungView);
        this.aboBox = findViewById(R.id.aboBox);
        this.user = (User) getIntent.getSerializableExtra("User");
        this.code = getIntent.getIntExtra("Code", 1);
        this.FollowListWithoutSelf = getIntent.getStringExtra("FollowList");
        this.rememberQuery = getIntent.getStringExtra("RQuery");
        if (this.code == 3) {
            this.id = Long.parseLong(getIntent.getStringExtra("UserKey"));
            String userKey = getIntent.getStringExtra("UserKey");
            if (this.id == this.user.getId()) {
                this.aboBox.setVisibility(View.INVISIBLE);
            }
            if (FollowListWithoutSelf.contains(userKey)) {
                aboBox.setChecked(true);
            }
            getUserData(id);
            updateFollower(id);
            getUserPosts(id, user.getUsername());

        } else if (this.code == 2) {
            this.aboBox.setVisibility(View.INVISIBLE);
            this.benutzerName.setText(this.user.getUsername());
            this.profilbild.setImageDrawable(roundImage(ImageHelper.base64ToBitmap(this.user.getBase64())));
            updateFollower(user.getId());
            getUserPosts(this.user.getId(), this.user.getUsername());
            getBio(this.user.getId());
        }
        this.beiträgeField.setText("Beiträge: 0");
        this.dataBasePosts = new DatabaseHelperPosts(this);
        this.imageViewArrayList = new ArrayList<>();
        this.idList = new ArrayList<>();
        this.gridView = findViewById(R.id.gridViewBilder);
        this.profilAdapter = new ProfilAdapter(this, this.imageViewArrayList, this.idList);
        this.gridView.setAdapter(this.profilAdapter);


        // einzelnes Bild groß anzeigen lassen
        this.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                String contentDis = ((ViewGroup) v).getChildAt(0).getContentDescription().toString();
                getFullPost(contentDis);
            }
        });


        // Zeigt Liste Following
        this.following.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, ListeFollowing.class);
                intent.putExtra("User", user);
                intent.putExtra("FollowsList", followsList);
                intent.putExtra("FollowList", FollowListWithoutSelf);
                startActivityForResult(intent, 250);

            }
        });
        //Zeigt Liste Followern
        this.follower.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, ListeFollower.class);
                intent.putExtra("FollowerList", followerList);
                intent.putExtra("User", user);
                intent.putExtra("FollowList", FollowListWithoutSelf);
                startActivityForResult(intent, 250);

            }
        });
        this.aboBox.setContentDescription(getIntent.getStringExtra("UserKey"));
        this.aboBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    FollowListWithoutSelf = FollowListWithoutSelf + v.getContentDescription().toString() + ":";
                    updateFollowStatus(user.getId(), Long.parseLong(v.getContentDescription().toString()));

                } else {
                    String target = v.getContentDescription().toString() + ":";
                    FollowListWithoutSelf = FollowListWithoutSelf.replace(target, "");
                    updateunfollowStatus(user.getId(), Long.parseLong(v.getContentDescription().toString()));
                }
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

    //Enthält Funktion zum "Runden" einer Bitmap
    public RoundedBitmapDrawable roundImage(Bitmap bitmap) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;
    }

    //Sendet eine Anfrage an die getFullPost.php um zu einem bestimmten Post zu gehen
    private void getFullPost(String id) {
        String dstAdress = "http://intranet-secure.de/instragram/getFullPost.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getFullPost(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    //Sendet eine Anfrage an die getFollower.php um die Followeranzeige korrekt darzustellen
    private void updateFollower(long id) {
        String dstAdress = "http://intranet-secure.de/instragram/getFollower.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getUsers(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    //Sendet eine Anfrage an die updateFollows.php um die Abo follow Anfrage zu bearbeiten
    private void updateFollowStatus(long userkey, long FID) {
        String dstAdress = "http://intranet-secure.de/instragram/updateFollows.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateFollows(userkey, FID));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    //Sendet eine Anfrage an die updateUnfollow.php um die entfollow Anfrage zu bearbeiten
    private void updateunfollowStatus(long userkey, long FID) {
        String dstAdress = "http://intranet-secure.de/instragram/updateUnfollow.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateFollows(userkey, FID));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    //Sendet eine Anfrage an die getUserData.php um die Profildaten des Nutzers zu erhalten
    private void getUserData(long id) {
        String dstAdress = "http://intranet-secure.de/instragram/getUserData.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getUsers(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    //Sendet eine Anfrage an die getPostPicture.php alle Posts des Users abzufragen und darzustellen
    private void getUserPosts(long id, String username) {
        String dstAdress = "http://intranet-secure.de/instragram/getPostPicture.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getUserPosts(id, username));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    //Sendet eine Anfrage an die getBio.php um die Biografie des Users darzustellen
    private void getBio(long id) {
        String dstAdress = "http://intranet-secure.de/instragram/getBio.php";
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

    //Enthält die Backbutton Funktio und die Profil-Bearbeiten Funktion
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent back = new Intent(ProfilActivity.this, MainActivity.class);
            back.putExtra("User", user);
            back.putExtra("RQuery", rememberQuery);
            back.putExtra("FollowList", FollowListWithoutSelf);
            setResult(RESULT_OK, back);
            finish(); // close this activity and return to preview activity (if there is any)
        } else {
            Intent intent = new Intent(ProfilActivity.this, Profil_BearbeitungActivity.class);
            intent.putExtra("User", this.user);
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
                    this.user = (User) data.getSerializableExtra("User");
                    this.benutzerName.setText(this.user.getUsername());
                    this.profilbild.setImageDrawable(roundImage(ImageHelper.base64ToBitmap(this.user.getBase64())));

                }
            } else {

                Log.d(MainActivity.class.getSimpleName(), "UserUpdated");
            }
        }
        if (requestCode == 250) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    this.FollowListWithoutSelf = data.getStringExtra("FollowList");
                    String[] thirdSplit = this.FollowListWithoutSelf.split(":");
                    if (this.FollowListWithoutSelf.equals("")) {
                        this.following.setText("Follows" + ": " + "0");
                    } else if (code == 2) {
                        this.following.setText("Follows" + ": " + (thirdSplit.length));
                    } else {

                    }
                }
            }
        }
    }

    //Enthält die ProcessFinish Funktion des AsyncResponse Interface
    @Override
    public void processFinish(String output) {
        if (output.contains("UserData")) {
            String[] userDataSplit = output.split(" : ");
            this.benutzerName.setText(userDataSplit[2]);
            this.beschreibungView.setText(this.beschreibungView.getText().toString() + userDataSplit[3]);
            this.profilbild.setImageDrawable(roundImage(ImageHelper.base64ToBitmap(userDataSplit[4])));

        } else if (output.contains("Follower: ")) {
            String[] firstSplit = output.split(" : ");
            this.follower.setText(firstSplit[0]);
            String[] secondSplit = firstSplit[1].split(": ");
            if (firstSplit.length > 2) {
                this.followerList = firstSplit[2];
            }
            if (secondSplit.length > 1) {
                this.followsList = secondSplit[1];
                String[] thirdSplit = secondSplit[1].split(":");
                this.following.setText(secondSplit[0] + ": " + (thirdSplit.length));
            } else {
                this.following.setText(secondSplit[0] + ": " + "0");

            }
        } else if (output.contains("Followed")) {
            Toast.makeText(getApplicationContext(), "Du folgst nun dieser Person!", Toast.LENGTH_SHORT).show();
            updateFollower(this.id);
        } else if (output.contains("PostIDs")) {
            String[] firstSplit = output.split(":");
            String postCount = firstSplit[1];
            this.beiträgeField.setText("Beiträge: " + postCount);
            int i = firstSplit.length - 1;
            while (i > 1) {
                getUserPosts(Long.parseLong(firstSplit[i]), this.benutzerName.getText().toString());
                i--;
            }
        } else if (output.contains("PostPic")) {
            String[] firstSplit = output.split(":");
            String base64 = firstSplit[1];
            Bitmap bitmap = ImageHelper.base64ToBitmap(base64);
            this.imageViewArrayList.add(bitmap);
            this.idList.add(firstSplit[2]);
            this.profilAdapter.notifyDataSetChanged();
        } else if (output.contains("FullPost")) {
            String[] pieces = output.split(" : ");
            String id = pieces[1];
            Bitmap image = ImageHelper.base64ToBitmap(pieces[2]);
            String titel = pieces[3];
            String[] hashes = pieces[4].split(":");
            ArrayList<String> hashList = new ArrayList<>();
            int i = 1;
            while (i < hashes.length) {
                hashList.add(hashes[i]);
                i++;
            }
            String likes = "Likes: " + pieces[5];
            int like = this.dataBasePosts.getLikeCount(Long.parseLong(id), this.user.getUsername());
            boolean checked;
            if (like == 2) {
                checked = true;

            } else {
                checked = false;
            }
            Intent goToBigImage = new Intent(ProfilActivity.this, Main_Image_Clicked.class);
            goToBigImage.putExtra("BitmapImage", image);
            goToBigImage.putExtra("Titel", titel);
            goToBigImage.putExtra("Likes", likes);
            goToBigImage.putExtra("Checked", checked);
            goToBigImage.putExtra("ID", id);
            goToBigImage.putExtra("User", this.user);
            goToBigImage.putStringArrayListExtra("Hashtags", hashList);
            startActivity(goToBigImage);
        } else if (output.contains("FollowExc")) {
            Toast.makeText(getApplicationContext(), "Du folgst der Person schon!", Toast.LENGTH_SHORT).show();
        } else if (output.contains("UnfollowNotPossible")) {
            Toast.makeText(getApplicationContext(), "Du musst der Person vorher folgen!", Toast.LENGTH_SHORT).show();
        } else if (output.contains("Unfollowed")) {
            Toast.makeText(getApplicationContext(), "Du folgst der Person nun nicht mehr!", Toast.LENGTH_SHORT).show();
            updateFollower(id);
        } else if (output.contains("Beschreibung")) {
            String[] splitBeschreibung = output.split(":_:");
            if (splitBeschreibung.length == 2) {
                this.beschreibungView.setText(splitBeschreibung[1]);
            }

        }
    }
}



