package de.hshl.softwareprojekt;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements AsyncResponse {
    //Variablen zur Verarbeitung der Inhalte in der Activity
    private Button applyChanges;
    private User user;
    private EditText userChanger;
    private EditText emailChanger;
    private DatabaseHelperUser databaseHelperUser;
    private DatabaseHelperPosts databasePosts;
    private String oldName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent = getIntent();
        this.user = (User) intent.getSerializableExtra("User");
        this.databaseHelperUser = new DatabaseHelperUser(this);
        this.databasePosts = new DatabaseHelperPosts(this);
        this.oldName = user.getUsername();
        this.userChanger = findViewById(R.id.editUser);
        this.emailChanger = findViewById(R.id.editEmail);
        this.applyChanges = findViewById(R.id.applyChanges);
        this.emailChanger.setText(user.getEmail());
        this.userChanger.setText(user.getUsername());
        this.applyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userChanger.length() > 2) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Änderungen bestätigen!");
                    builder.setMessage("Sie sind dabei ihre Nutzerdaten zu ändern, sind sie sicher, dass sie fortfahren möchten?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            user.setUsername(userChanger.getText().toString());
                            databaseHelperUser.updateUser(user.getId(), userChanger.getText().toString());
                            databasePosts.updateUserPosts(user.getId(), userChanger.getText().toString());
                            updateData(user.getId());
                            getAllPosts(user.getId());
                            Toast.makeText(getApplicationContext(), "Dein Nutzerdaten wurden erfolgreich geändert!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            userChanger.setText(user.getUsername());
                            Toast.makeText(getApplicationContext(), "Dein ursprünglicher Nutzerdaten wurden wiederhergestellt!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.show();
                } else {
                    userChanger.setError("Benutzernamen müssen mind. 3 Zeichen haben!");
                }

            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar6);
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
            Intent sendBackIntent = new Intent(SettingsActivity.this, MainActivity.class);
            sendBackIntent.putExtra("User", this.user);
            setResult(RESULT_OK, sendBackIntent);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateData(long id) {
        String dstAdress = "http://intranet-secure.de/instragram/updateUserData.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateData(id, userChanger.getText().toString(), emailChanger.getText().toString()));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.execute();
    }

    private void getAllPosts(long id) {
        String dstAdress = "http://intranet-secure.de/instragram/getAllKommPosts.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.getUsers(id));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    private void updateAllKomms(long id) {
        String dstAdress = "http://intranet-secure.de/instragram/updateKommentData.php";
        HttpConnection httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.updateAllKomms(id, user.getUsername(), user.getBase64(), oldName, user.getBase64()));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    @Override
    public void processFinish(String output) {
        if (output.contains("getPostIDs")) {
            String[] postIDs = output.split(" : ");

            int i = 1;
            while (i < postIDs.length) {
                updateAllKomms(Long.parseLong(postIDs[i]));
                i++;
            }
        }
    }
}
