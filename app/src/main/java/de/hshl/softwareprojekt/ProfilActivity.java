package de.hshl.softwareprojekt;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.nfc.Tag;
import android.provider.ContactsContract;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class ProfilActivity extends AppCompatActivity implements OnClickListener {

    private TextView anzahlFollower, anzahlFollowing, biografie, folgen, entfolgen;
    private static final String TAG = "ProfilActivity";
    int personID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        ImageView profilbild = findViewById(R.id.profilbild);

        TextView follower = findViewById(R.id.follower);
        TextView following = findViewById(R.id.following);

        TextView anzahlFollower = findViewById(R.id.anzFollower);
        TextView anzahlFollowing = findViewById(R.id.anzFollowing);

        final DatabaseHelper dbHelper = new DatabaseHelper(this);
       // folgen.setOnClickListener(this);

        TextView biografie = findViewById(R.id.biografie);

        Button folgen = findViewById(R.id.folgen);
        Button entfolgen = findViewById(R.id.entfolgen);

        View divider = findViewById(R.id.divider);
        //isFollowing();

        folgen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "onClick: Folgt Nutzer");
                Intent intent = new Intent();
                startActivity(intent);
                setFollowing();
            }
        });
        /**dbHelper = new DatabaseHelper(this);

        final Cursor cursor = dbHelper.getFollow(int personID);
        String [] columns = new String[] {
                DatabaseHelper.COL_01,
                DatabaseHelper.COL_02
        };**/

        entfolgen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "onClick: Folgt Nutzer nicht");
                if(personID > 0){
                    Cursor rs = dbHelper.getPerson(personID);
                    rs.moveToFirst();
                    String personId = rs.getString(rs.getColumnIndex(DatabaseHelper.COL_01));
                    String personIsFollowing = rs.getString(rs.getColumnIndex(DatabaseHelper.COL_02));
                    String personFollowedBy = rs.getString(rs.getColumnIndex(DatabaseHelper.COL_03));

                    setUnfollowing();
                }

            }
        });

        // soll Profilbild rund darstellen
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.futurecity);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        profilbild.setImageDrawable(roundedBitmapDrawable);
    }

    // zeigt an, dass man dem Nutzer folgt
    private void setFollowing(){
        folgen.setVisibility(View.GONE);
        entfolgen.setVisibility(View.VISIBLE);

    }
    // zeigt an, dass man dem Nutzer nicht folgt
    private void setUnfollowing(){
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

    /** @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // wenn Button folgen gedrückt wird   müsste jetzt unnötig sein
            case R.id.folgen:
               // Button folgen = findViewById(R.id.folgen);
                folgen.setBackgroundColor(Color.GREEN);
                folgen.setText("Entfolgen");
                break;
            // Weiterleitung auf Profil_BearbeitungActivity
            case R.id.action_settings:
                Intent intent = new Intent(ProfilActivity.this,
                        Profil_BearbeitungActivity.class);
                startActivity(intent);
                break;

            default:
                break;


    } //private void setupImageGrid(ArrayList<String> imgURLs){
      //  GridView gridView_Bilder = (GridView) findViewById(R.id.gridView_Bilder);
        // gridView_Bilder.setColumnWidth(imageWidth);
        //int gridWidth = getRessources().getDisplayMetrics().widthPixels;
        // int imageWidth = gridWith/3;
**/
    }
// }