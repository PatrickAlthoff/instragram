package de.hshl.softwareprojekt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


public class SearchUserFragment extends Fragment {
    ImageView profilPic;
    TextView profilName;
    CheckBox aboBox;

    public void init(String username, String contentDis) {
        this.profilPic = getView().findViewById(R.id.profilSearchPic);
        this.profilName = getView().findViewById(R.id.profilSearchName);
        this.profilName.setText(username);
        this.profilName.setContentDescription(contentDis);
        this.aboBox = getView().findViewById(R.id.aboBoxUser);
        this.aboBox.setContentDescription(contentDis);
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
        return inflater.inflate(R.layout.fragment_search_user, container, false);
    }
}


