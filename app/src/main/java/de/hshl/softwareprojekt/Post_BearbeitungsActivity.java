package de.hshl.softwareprojekt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    private final String uploadUrlString = "http://intranet-secure.de/instragram/Upload.php";
    private Uri imageUri;
    private ProgressDialog uploadDialog;
    private final int PICK_IMAGE_REQ_CODE = 12;
    private final int EXTERNAL_STORAGE_PERMISSION_REQ_CODE = 14;

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

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void imageUriÜbergabe(){
        this.imageUri = getImageUri(this, this.postBitmap);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.postButton:
                Intent sendBackIntent = new Intent(Post_BearbeitungsActivity.this, MainActivity.class);
                String sendTitelPost = this.postTitel.getText().toString();
                sendBackIntent.putExtra("BitmapImage", this.postBitmap);
                sendBackIntent.putExtra("Titel", sendTitelPost);
                sendBackIntent.putStringArrayListExtra("Hashtags",convertIntoStringList(this.editList));
                String date = new SimpleDateFormat("MMM. dd. HH:mm", Locale.getDefault()).format(new Date());
                sendBackIntent.putExtra("Date", date);

                if(imageUri!= null && internetAvailable()) {
                    uploadDialog = new ProgressDialog(Post_BearbeitungsActivity.this);
                    uploadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    uploadDialog.show();
                    new Post_BearbeitungsActivity.UploadAsynk().execute();
                }

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

        if(requestCode == BEARBEITUNG_CODE ){
            if(resultCode == RESULT_OK){
                if(data != null){
                    Intent intentVerarbeitet = data;
                    this.postTitel.setText(intentVerarbeitet.getStringExtra("Titel"));
                    this.postBitmap = intentVerarbeitet.getParcelableExtra("BitmapImage");
                    this.postImage.setImageBitmap(this.postBitmap);
                    imageUriÜbergabe();
                }
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        EditText newText = new EditText(this);
        newText.setBackground(null);
        newText.setTextSize(14);
        newText.setText("#"+v.getText().toString());
        this.hashGrid = (GridLayout) v.getParent();
        this.hashGrid.addView(newText);
        this.editList.add(newText);
        return false;
    }
    public ArrayList<String> convertIntoStringList(ArrayList<EditText> editList){
        ArrayList<String> stringList = new ArrayList<>();
        int i = 0;
        while(i<editList.size()){
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
            Intent sendBackIntent = new Intent (Post_BearbeitungsActivity.this, MainActivity.class);
            setResult(RESULT_CANCELED, sendBackIntent);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void pickImage(){

        Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickImageIntent.setType("image/*");
        startActivityForResult(pickImageIntent, PICK_IMAGE_REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == EXTERNAL_STORAGE_PERMISSION_REQ_CODE && grantResults.length >0 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED){
            pickImage();
        }
    }
    private class UploadAsynk extends AsyncTask {


        String serverResponse;
        @Override
        protected Object doInBackground(Object[] params) {


            String boundary = "---boundary" + System.currentTimeMillis();
            String firstLineBoundary = "--" + boundary + "\r\n";
            String contentDisposition = "Content-Disposition: form-data;name=\"fileupload1\";filename=\"imagefile.jpg\"\r\n";
            String newLine = "\r\n";
            String lastLineBoundary = "--" + boundary + "--\r\n";


            try {
                InputStream imageInputStream = getContentResolver().openInputStream(imageUri);
                int uploadSize = (firstLineBoundary + contentDisposition + newLine + newLine + lastLineBoundary).getBytes().length + imageInputStream.available();
                uploadDialog.setMax(uploadSize);

                URL uploadUrl = new URL(uploadUrlString);
                HttpURLConnection connection = (HttpURLConnection) uploadUrl.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setFixedLengthStreamingMode(uploadSize);

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes(firstLineBoundary);
                dataOutputStream.writeBytes(contentDisposition);
                dataOutputStream.writeBytes(newLine);

                int byteCounter = 0;

                byte[] buffer = new byte[1024];
                int read;
                while ((read = imageInputStream.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, read);
                    byteCounter+=1024;
                    uploadDialog.setProgress(byteCounter);
                }

                dataOutputStream.writeBytes(newLine);
                dataOutputStream.writeBytes(lastLineBoundary);
                dataOutputStream.flush();
                dataOutputStream.close();

                serverResponse = getTextFromInputStream(connection.getInputStream());
                connection.getInputStream().close();
                connection.disconnect();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(Post_BearbeitungsActivity.this, "Fehler aufgetreten!", Toast.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(Post_BearbeitungsActivity.this, "Fehler aufgetreten!", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            uploadDialog.dismiss();
            super.onPostExecute(o);
        }
    }
    public String getTextFromInputStream(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String aktuelleZeile;
        try {
            while ((aktuelleZeile = reader.readLine()) != null) {
                stringBuilder.append(aktuelleZeile);
                stringBuilder.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString().trim();
    }

    private boolean internetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }
}

