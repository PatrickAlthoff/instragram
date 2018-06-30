package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ListeFollowing extends AppCompatActivity implements AsyncResponse {
    private String FollowListWithoutSelf;
    private String followsList;
    private LinearLayout userCache;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        Intent getIntent = getIntent();
        this.userCache = findViewById(R.id.userCache);
        this.user = (User) getIntent.getSerializableExtra("User");

        this.FollowListWithoutSelf = getIntent.getStringExtra("FollowList");
        this.followsList = getIntent.getStringExtra("FollowsList");
        String[] followerPieces = this.followsList.split(":");
        int i = 0;
        while (i < followerPieces.length) {
            getUserData(Long.parseLong(followerPieces[i]));
            i++;
        }
        Toolbar toolbar = findViewById(R.id.toolbar10);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent gotBack = new Intent(ListeFollowing.this, ProfilActivity.class);
            gotBack.putExtra("FollowList", FollowListWithoutSelf);
            setResult(RESULT_OK, gotBack);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public RoundedBitmapDrawable roundImage(Bitmap bitmap) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;
    }

    private void getUserData(long query) {
        String dstAdress = "http://intranet-secure.de/instragram/getUserData.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getUsers(query));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    private void updateFollowStatus(long userkey, long FID) {
        String dstAdress = "http://intranet-secure.de/instragram/updateFollows.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateFollows(userkey, FID));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    private void updateunfollowStatus(long userkey, long FID) {
        String dstAdress = "http://intranet-secure.de/instragram/updateUnfollow.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateFollows(userkey, FID));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    public void addSearchUser(Bitmap postBitmap, String username, String contentDis) {

        //Initialisiert den FragmentManager, das PostFragment und das FrameLayout
        FragmentManager fragmentManagerSearchUser = getSupportFragmentManager();
        SearchUserFragment followerFragment = new SearchUserFragment();
        FrameLayout frameInner = new FrameLayout(this);
        frameInner.setId(View.generateViewId());
        userCache.addView(frameInner, 0);

        final FragmentTransaction fragmentTransactionSearchUser = fragmentManagerSearchUser.beginTransaction();
        fragmentTransactionSearchUser.add(frameInner.getId(), followerFragment);

        fragmentTransactionSearchUser.commitNow();
        followerFragment.init(username, contentDis);
        ImageView profilBild = followerFragment.profilPic;
        profilBild.setImageDrawable(roundImage(postBitmap));
        profilBild.setContentDescription(contentDis);
        profilBild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToProfil = new Intent(ListeFollowing.this, ProfilActivity.class);
                goToProfil.putExtra("UserKey", v.getContentDescription().toString());
                goToProfil.putExtra("Code", 3);
                goToProfil.putExtra("User", user);
                goToProfil.putExtra("FollowList", FollowListWithoutSelf);
                startActivity(goToProfil);
            }
        });
        CheckBox checkAbo = followerFragment.aboBox;

        if (FollowListWithoutSelf.contains(contentDis)) {
            checkAbo.setChecked(true);
        }

        checkAbo.setOnClickListener(new View.OnClickListener() {
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

    }

    @Override
    public void processFinish(String output) {
        if (output.contains("UserData")) {
            String[] userDataSplit = output.split(" : ");
            addSearchUser(ImageHelper.base64ToBitmap(userDataSplit[2]), userDataSplit[1], userDataSplit[3]);

        } else if (output.contains("Followed")) {
            Toast.makeText(getApplicationContext(), "Du folgst nun dieser Person!", Toast.LENGTH_SHORT).show();

        } else if (output.contains("FollowExc")) {
            Toast.makeText(getApplicationContext(), "Du folgst der Person schon!", Toast.LENGTH_SHORT).show();
        } else if (output.contains("UnfollowNotPossible")) {
            Toast.makeText(getApplicationContext(), "Du musst der Person vorher folgen!", Toast.LENGTH_SHORT).show();
        } else if (output.contains("Unfollowed")) {
            Toast.makeText(getApplicationContext(), "Du folgst der Person nun nicht mehr!", Toast.LENGTH_SHORT).show();

        }
    }
}
