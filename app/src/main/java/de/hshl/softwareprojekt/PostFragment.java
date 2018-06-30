package de.hshl.softwareprojekt;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PostFragment extends Fragment {
    //Variablen zur Verarbeitung der Inhalte in der Activity
    ImageView postImage;
    ImageView profilPicPost;
    ImageButton delete;
    ImageButton share;
    TextView textViewTitel;
    TextView timeStampView;
    TextView postProfilName;
    TextView shareName;
    TextView shareCount;
    CheckBox likeChecker;

    //Initialisierung die notwendigen Daten für das Postfragment
    public void addPost(Bitmap bitmap, String titel) {
        this.postImage = getView().findViewById(R.id.postView);
        this.postImage.setImageBitmap(bitmap);

        this.profilPicPost = getView().findViewById(R.id.profilPicPost);
        this.share = getView().findViewById(R.id.shareButton);
        this.shareName = getView().findViewById(R.id.shareName);
        this.postProfilName = getView().findViewById(R.id.profilNamePost);
        this.likeChecker = getView().findViewById(R.id.iLike);
        this.shareCount = getView().findViewById(R.id.shareCount);
        this.textViewTitel = getView().findViewById(R.id.textViewTitel);
        this.textViewTitel.setText(titel);

        this.timeStampView = getView().findViewById(R.id.timeStamp);

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