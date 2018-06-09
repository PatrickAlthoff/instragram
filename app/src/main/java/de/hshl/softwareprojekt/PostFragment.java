package de.hshl.softwareprojekt;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class PostFragment extends Fragment {
    ImageView postImage;
    ImageButton delete;
    TextView textViewTitel;
    TextView timeStampView;
    TextView postProfilName;

    //Enthält die Methode mit der einem ImageView eine Bitmap und ein titel übergeben wird
    public void addPost(Bitmap bitmap, String titel){
        this.postImage = getView().findViewById(R.id.postView);
        this.postImage.setImageBitmap(bitmap);

        //Der Titel wird hier einem TextView übergeben und
        //in die ContentDescription des ImageView geschrieben

        this.postProfilName = getView().findViewById(R.id.profilNamePost);

        this.textViewTitel = getView().findViewById(R.id.textViewTitel);
        this.textViewTitel.setText(titel);
        this.postImage.setContentDescription(this.textViewTitel.getText());

        String date = new SimpleDateFormat("MMM. dd. HH:mm", Locale.getDefault()).format(new Date());
        this.timeStampView = getView().findViewById(R.id.timeStamp);
        this.timeStampView.setText(date);

        this.delete = getView().findViewById(R.id.deleteButton);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false);

    }
}