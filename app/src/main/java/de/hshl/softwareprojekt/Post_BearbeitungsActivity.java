package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Post_BearbeitungsActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private int BEARBEITUNG_CODE = 12;
    private Button customizeButton;
    private Button postBtn;
    private ImageView postImage;
    private TextView postTitel;
    private EditText postHashtag;
    private Bitmap postBitmap;
    private GridLayout hashGrid;
    private ArrayList<EditText> editList;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__bearbeitungs);
        Intent getIntent = getIntent();
        this.customizeButton = findViewById(R.id.customizeButton);
        this.postBtn = findViewById(R.id.postButton);
        this.postImage = findViewById(R.id.postImage);
        this.postTitel = findViewById(R.id.postTitel);
        this.postHashtag = findViewById(R.id.hashtagField);
        this.hashGrid = findViewById(R.id.gridLayout);
        this.postBitmap = getIntent.getParcelableExtra("BitmapImage");
        this.postImage.setImageBitmap(this.postBitmap);
        this.postTitel.setText(getIntent.getStringExtra("Titel"));
        this.customizeButton.setOnClickListener(this);
        this.postBtn.setOnClickListener(this);
        this.postHashtag.setOnEditorActionListener(this);
        this.postHashtag.selectAll();
        this.user = (User) getIntent.getSerializableExtra("User");
        this.editList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar7);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.postButton:
                Intent sendBackIntent = new Intent(Post_BearbeitungsActivity.this, MainActivity.class);
                String sendTitelPost = this.postTitel.getText().toString();
                sendBackIntent.putExtra("BitmapImage", this.postBitmap);
                sendBackIntent.putExtra("Titel", sendTitelPost);
                sendBackIntent.putStringArrayListExtra("Hashtags", convertIntoStringList(this.editList));
                String date = new SimpleDateFormat("MMM. dd. HH:mm", Locale.getDefault()).format(new Date());
                sendBackIntent.putExtra("Date", date);
                setResult(RESULT_OK, sendBackIntent);
                finish();
                break;
            case R.id.customizeButton:
                Intent sendToBearbeitung = new Intent(Post_BearbeitungsActivity.this, BearbeitungsActivity.class);
                sendToBearbeitung.putExtra("BitmapImage", this.postBitmap);
                sendToBearbeitung.putExtra("Code", 1);
                sendToBearbeitung.putExtra("User", this.user);
                startActivityForResult(sendToBearbeitung, BEARBEITUNG_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BEARBEITUNG_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Intent intentVerarbeitet = data;
                    this.postTitel.setText(intentVerarbeitet.getStringExtra("Titel"));
                    this.postBitmap = intentVerarbeitet.getParcelableExtra("BitmapImage");
                    this.postImage.setImageBitmap(this.postBitmap);
                }
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        EditText newText = new EditText(this);
        newText.setBackground(null);
        newText.setTextSize(14);
        newText.setText("#" + v.getText().toString());
        this.hashGrid = (GridLayout) v.getParent();
        this.hashGrid.addView(newText);
        this.editList.add(newText);
        return false;
    }

    public ArrayList<String> convertIntoStringList(ArrayList<EditText> editList) {
        ArrayList<String> stringList = new ArrayList<>();
        int i = 0;
        while (i < editList.size()) {
            String getText = editList.get(i).getText().toString();
            stringList.add(getText);
            i++;
        }

        return stringList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent sendBackIntent = new Intent(Post_BearbeitungsActivity.this, MainActivity.class);
            setResult(RESULT_CANCELED, sendBackIntent);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}

