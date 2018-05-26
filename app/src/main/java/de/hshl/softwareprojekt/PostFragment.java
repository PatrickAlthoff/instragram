package de.hshl.softwareprojekt;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class PostFragment extends Fragment {
    ImageView postImage;

    public void addImage(Bitmap bitmap){
        this.postImage = getView().findViewById(R.id.postView);
        this.postImage.setId(View.generateViewId());
        this.postImage.setImageBitmap(bitmap);
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