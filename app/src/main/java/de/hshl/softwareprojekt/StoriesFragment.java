package de.hshl.softwareprojekt;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class StoriesFragment extends Fragment {
    ImageView storieImage;
    ProgressBar storiesBar;




    public void addStorie(Bitmap bitmap, String titel){
        this.storieImage = getView().findViewById(R.id.storieView);
        this.storieImage.setImageBitmap(bitmap);

        //Der Titel wird hier einem TextView übergeben und
        //in die ContentDescription des ImageView geschrieben





    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.storiesBar = getView().findViewById(R.id.storiesBar);
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stories, container, false);
    }


}