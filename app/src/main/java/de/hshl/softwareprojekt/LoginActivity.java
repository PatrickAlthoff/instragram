package de.hshl.softwareprojekt;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity implements AsyncResponse {

    private ArrayList<String> DUMMY_CREDENTIALS = new ArrayList<>();
    private UserLoginTask mAuthTask = null;

    // UI references.
    private int REQUEST_REGIST_CODE = 200;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button neuBtn;
    private Button getDaten;
    private Button deleteBtn;
    private ArrayList<String> userList;
    private DatabaseHelperUser database;
    private LinearLayout email_login_form;
    private CheckBox rememberCheck;
    private ArrayList<String> userTableData;
    private HttpConnection httpConnection;
    private String response;
    private boolean valid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        rememberCheck = findViewById(R.id.rememberCheck);

        mPasswordView = findViewById(R.id.password);


        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sendXML(mEmailView.getText().toString());
                DUMMY_CREDENTIALS = database.getData();

            }
        });

        database = new DatabaseHelperUser(this);
        email_login_form = findViewById(R.id.email_login_form);

        this.userTableData = new ArrayList<>();
        this.userList = new ArrayList<>();
        this.neuBtn = findViewById(R.id.neuBtn);
        this.neuBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //    database.insertData(mEmailView.getText().toString(), mPasswordView.getText().toString());
                Intent toRegist = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivityForResult(toRegist, REQUEST_REGIST_CODE);
            }
        });

        this.getDaten = findViewById(R.id.getDaten);
        this.getDaten.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                userList = database.getData();
                int i = 0;
                while (i < userList.size()) {
                    TextView newText = new TextView(LoginActivity.this);
                    newText.setText(userList.get(i));
                    email_login_form.addView(newText, 6);
                    i++;
                }
            }
        });

        this.deleteBtn = findViewById(R.id.deleteBtn);
        this.deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                database.deleteData();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        String remember = database.getRemember();
        if (remember.contains(":")) {
            String[] pieces = remember.split(":");
            mEmailView.setText(pieces[0]);
            mPasswordView.setText(pieces[1]);
            rememberCheck.setChecked(true);
        }
    }

    private void sendXML(String email) {

        String dstAdress = "http://intranet-secure.de/instragram/CheckForUser.php";

        httpConnection = new HttpConnection(dstAdress);
        httpConnection.setMessage(XmlHelper.checkEmail(email));
        httpConnection.setMode(HttpConnection.MODE.PUT);
        httpConnection.delegate = this;
        httpConnection.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_REGIST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Intent intentVerarbeitet = data;
                    this.mEmailView.setText(intentVerarbeitet.getStringExtra("email"));
                    this.mPasswordView.setText(intentVerarbeitet.getStringExtra("pw"));
                }
            }
        }
    }

    private void attemptLogin(Boolean remember) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            mAuthTask = new UserLoginTask(email, password, remember, valid, userTableData);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 3;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void processFinish(String output) {
        this.response = output;

        String[] outputUser = output.split(":");
        int index = 1;
        while (index < outputUser.length) {
            userTableData.add(outputUser[index]);
            index++;
        }
        if (this.response.contains("UserChecked")) {
            valid = true;
        } else if (this.response.contains("UserNotChecked")) {
            valid = false;
        }
        if (rememberCheck.isChecked()) {
            attemptLogin(true);
        } else {
            attemptLogin(false);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> implements AsyncResponse {

        private final String mEmail;
        private final String mPassword;
        private long userID;
        private boolean remember;
        private boolean valid;
        private ArrayList<String> userTableData;
        private String username;
        private String email;
        private String pw;
        private User user;
        private DatabaseHelperUser userData;

        UserLoginTask(String email, String password, Boolean remember, boolean valid, ArrayList<String> userTableData) {
            this.mEmail = email;
            this.mPassword = password;
            this.remember = remember;
            this.valid = valid;
            this.userData = new DatabaseHelperUser(LoginActivity.this);
            this.userTableData = userTableData;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");

                if (pieces[0].equals(mEmail)) {
                    return pieces[1].equals(mPassword);
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success && valid == true) {
                userID = Long.parseLong(userTableData.get(0));
                username = userTableData.get(1);
                email = userTableData.get(2);
                this.user = new User(userID, username, mEmail);
                this.user.setBase64(userData.getUserPic(user.getId()));
                this.userData.updateRemember(mEmail, remember);
                Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
                intentMain.putExtra("User", this.user);
                startActivity(intentMain);
                finish();
            } else if (success && valid == false) {
                mPasswordView.setError("Deine Mailadresse ist ungültig!");
                mPasswordView.requestFocus();
                userTableData.clear();
            } else if (!success && valid == true) {
                pw = userTableData.get(3);
                if(pw == mPassword){
                    userID = Long.parseLong(userTableData.get(0));
                    username = userTableData.get(1);
                    email = userTableData.get(2);
                    sendXML(userID);
                }else{
                    mPasswordView.setError("Das Passwort ist inkorrekt.");
                }



            } else {
                mPasswordView.setError("Um dich einzuloggen, musst du dich vorher registrieren.");
                mPasswordView.requestFocus();
                userTableData.clear();
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        private void sendXML(long id) {
            String dstAdress = "http://intranet-secure.de/instragram/getUserPic.php";
            httpConnection = new HttpConnection(dstAdress);
            httpConnection.setMessage(XmlHelper.getUsers(id));
            httpConnection.setMode(HttpConnection.MODE.PUT);
            httpConnection.delegate = this;
            httpConnection.execute();

        }

        @Override
        public void processFinish(String output) {
            if (output.contains("UserNotChecked")) {
                mEmailView.setError("Ungültige Login-Daten");
            } else {
                String[] outputUser = output.split(":");
                userData.insertData(userID, username, email, pw, outputUser[2], false);
                this.user = new User(userID, username, mEmail);
                this.user.setBase64(outputUser[2]);
                Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
                intentMain.putExtra("User", this.user);
                startActivity(intentMain);
                finish();
            }

        }
    }
}

