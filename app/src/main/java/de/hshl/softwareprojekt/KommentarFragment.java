package de.hshl.softwareprojekt;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class KommentarFragment extends Fragment {
    ImageView profilPicKomm;
    TextView profilNameKomm;
    TextView kommentar;

    public void creatKomment(Bitmap bitmap, String username, String kommentar){
        this.profilPicKomm = getView().findViewById(R.id.profilPicKomm);
        this.profilNameKomm = getView().findViewById(R.id.usernameKomm);
        this.kommentar = getView().findViewById(R.id.kommentarView);

        this.profilPicKomm.setImageBitmap(bitmap);
        this.profilNameKomm.setText(username);
        this.kommentar.setText(kommentar);
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
        return inflater.inflate(R.layout.fragment_kommentar, container, false);
    }
}
