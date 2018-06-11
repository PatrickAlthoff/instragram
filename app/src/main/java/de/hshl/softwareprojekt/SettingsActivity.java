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
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    //Variablen zur Verarbeitung der Inhalte in der Activity
    private Button change;
    private User user;
    private EditText userChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("User");

        userChanger = findViewById(R.id.editUser);
        change = findViewById(R.id.doStuffBtn);

        userChanger.setText(user.getUsername());
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Namen bestätigen!");
                builder.setMessage("Sie sind dabei ihren Namen zu ändern, sind sie sicher, dass sie fortfahren möchten?");
                builder.setCancelable(false);
                builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.setUsername(userChanger.getText().toString());
                        Toast.makeText(getApplicationContext(), "Dein Name wurde erfolgreich geändert!", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userChanger.setText(user.getUsername());
                        Toast.makeText(getApplicationContext(), "Dein ursprünglicher Name wurde wiederhergestellt!", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();


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
            Intent sendBackIntent = new Intent (SettingsActivity.this, MainActivity.class);
            sendBackIntent.putExtra("User", user);
            setResult(RESULT_OK, sendBackIntent);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}
