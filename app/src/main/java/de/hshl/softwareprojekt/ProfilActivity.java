package de.hshl.softwareprojekt;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.provider.ContactsContract;
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
import android.widget.ListAdapter;
import android.widget.TextView;

import com.amitshekhar.utils.DatabaseHelper;

import java.util.ArrayList;


public class ProfilActivity extends AppCompatActivity implements OnClickListener {

    private TextView anzahlFollower, anzahlFollowing, biografie, folgen, entfolgen;
    private static final String TAG = "ProfilActivity";
    private static final int NUM_COL_GRID = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        ImageView profilbild = findViewById(R.id.profilbild);

        TextView follower = findViewById(R.id.follower);
        TextView following = findViewById(R.id.following);

        TextView anzahlFollower = findViewById(R.id.anzFollower);
        TextView anzahlFollowing = findViewById(R.id.anzFollowing);

        TextView benutzerName = findViewById(R.id.benutzerName);

        Button folgen = findViewById(R.id.folgen);
        Button entfolgen = findViewById(R.id.entfolgen);

        View divider = findViewById(R.id.divider);

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

//


        // Zeigt Liste Following
        following.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, ListeFollowing.class);
                startActivity(intent);

            }
        });
        //Zeigt Liste Followern
        follower.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, ListeFollower.class);
                startActivity(intent);

            }
        });

        // Person folgen
        folgen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Folgt Nutzer");
                //Intent intent = new Intent();
                //startActivity(intent);
                dbHelper.setIsFollowing();
                setUnfollowing();

            }
        });

        // Person entfolgen
        entfolgen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Folgt Nutzer nicht");

                dbHelper.setIsFollowedBy();
                setFollowing();


            }
        });

        // stellt Profilbild rund dar
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hannah);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        profilbild.setScaleType(ImageView.ScaleType.CENTER_CROP);
        profilbild.setImageDrawable(roundedBitmapDrawable);
    }

    //überprüft ob man dem Nutzer folgt
    private void isFollowing() {
        Log.d(TAG, "Überprüft ob dem Nutzer gefolgt wird");

    }

    // zeigt an, dass man dem Nutzer folgt
    private void setFollowing() {
        folgen.setVisibility(View.GONE);
        entfolgen.setVisibility(View.VISIBLE);

    }

    // zeigt an, dass man dem Nutzer nicht folgt
    private void setUnfollowing() {
        folgen.setVisibility(View.VISIBLE);
        entfolgen.setVisibility(View.GONE);
        folgen.setBackgroundColor(Color.GREEN);
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


    /** @Override public void onClick(View v) {
    switch (v.getId()) {

    // Weiterleitung auf Profil_BearbeitungActivity
    case R.id.action_settings:
    Intent intent = new Intent(ProfilActivity.this,
    Profil_BearbeitungActivity.class);
    startActivity(intent);
    break;

    default:
    break;

     **/
}



