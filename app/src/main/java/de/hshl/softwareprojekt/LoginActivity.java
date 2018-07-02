package de.hshl.softwareprojekt;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity implements AsyncResponse {
    private boolean valid;
    private int REQUEST_REGIST_CODE = 200;
    private String response;
    private Button neuBtn;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox rememberCheck;
    private DatabaseHelperUser database;
    private HttpConnection httpConnection;
    private ArrayList<String> userTableData;
    private ArrayList<String> DUMMY_CREDENTIALS;
    private UserLoginTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        this.mEmailView = findViewById(R.id.email);
        this.rememberCheck = findViewById(R.id.rememberCheck);
        this.mPasswordView = findViewById(R.id.password);
        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sendXML(mEmailView.getText().toString());
                DUMMY_CREDENTIALS = database.getData();

            }
        });

        this.database = new DatabaseHelperUser(this);
        this.DUMMY_CREDENTIALS = new ArrayList<>();
        this.userTableData = new ArrayList<>();
        this.neuBtn = findViewById(R.id.neuBtn);
        this.neuBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //    database.insertData(mEmailView.getText().toString(), mPasswordView.getText().toString());
                Intent toRegist = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivityForResult(toRegist, REQUEST_REGIST_CODE);
            }
        });


        this.mLoginFormView = findViewById(R.id.login_form);
        this.mProgressView = findViewById(R.id.login_progress);
        String remember = this.database.getRemember();
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
        this.mEmailView.setError(null);
        this.mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = this.mEmailView.getText().toString();
        String password = this.mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            this.mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = this.mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            this.mEmailView.setError(getString(R.string.error_field_required));
            focusView = this.mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            this.mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = this.mEmailView;
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
            this.mAuthTask = new UserLoginTask(email, password, remember, this.valid, this.userTableData);
            this.mAuthTask.execute((Void) null);
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

            this.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            this.mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            this.mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            this.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            this.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void processFinish(String output) {
        this.response = output;

        String[] outputUser = output.split(":");
        int index = 1;
        while (index < outputUser.length) {
            this.userTableData.add(outputUser[index]);
            index++;
        }
        if (this.response.contains("UserChecked")) {
            this.valid = true;
        } else if (this.response.contains("UserNotChecked")) {
            this.valid = false;
        }
        if (this.rememberCheck.isChecked()) {
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
        private String username;
        private String email;
        private String pw;
        private User user;
        private DatabaseHelperUser userData;
        private ArrayList<String> userTableData;

        UserLoginTask(String email, String password, Boolean remember, boolean valid, ArrayList<String> userTableData) {
            this.mEmail = email;
            this.mPassword = password;
            this.remember = remember;
            this.valid = valid;
            this.userData = new DatabaseHelperUser(LoginActivity.this);
            this.userTableData = userTableData;
        }

        //Enthält Funktion zum Vergleich der Eingabe mit der in der Datenbank enthaltenene Informationen
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

        //Enthält die Funktionen zum Abarbeiten verschiedener Login Szenarien
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success && valid == true) {
                this.userID = Long.parseLong(this.userTableData.get(0));
                this.username = this.userTableData.get(1);
                this.email = this.userTableData.get(2);
                this.user = new User(this.userID, this.username, this.mEmail);
                this.user.setBase64(this.userData.getUserPic(this.user.getId()));
                this.userData.updateRemember(this.mEmail, this.remember);
                Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
                intentMain.putExtra("User", this.user);
                startActivity(intentMain);
                finish();
            } else if (success && this.valid == false) {
                mPasswordView.setError("Deine Mailadresse ist ungültig!");
                mPasswordView.requestFocus();
                this.userTableData.clear();
            } else if (!success && this.valid == true) {
                this.pw = this.userTableData.get(3);
                if (this.pw.equals(this.mPassword)) {
                    this.userID = Long.parseLong(this.userTableData.get(0));
                    this.username = this.userTableData.get(1);
                    this.email = this.userTableData.get(2);
                    sendXML(this.userID);
                } else {
                    mPasswordView.setError("Das Passwort ist inkorrekt.");
                }


            } else {
                mPasswordView.setError("Um dich einzuloggen, musst du dich vorher registrieren.");
                mPasswordView.requestFocus();
                this.userTableData.clear();
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        //Sendet eine Anfrage an die getUserPic.php um das Profilbild des Users zu erhalten
        private void sendXML(long id) {
            String dstAdress = "http://intranet-secure.de/instragram/getUserPic.php";
            httpConnection = new HttpConnection(dstAdress);
            httpConnection.setMessage(XmlHelper.getUsers(id));
            httpConnection.setMode(HttpConnection.MODE.PUT);
            httpConnection.delegate = this;
            httpConnection.execute();
        }

        //Enthält die ProcessFinish Funktion des AsyncResponse Interface
        @Override
        public void processFinish(String output) {
            if (output.contains("UserNotChecked")) {
                mEmailView.setError("Ungültige Login-Daten");
            } else {
                String[] outputUser = output.split(":");
                this.userData.insertData(this.userID, this.username, this.email, this.pw, outputUser[2], false);
                this.user = new User(this.userID, this.username, this.mEmail);
                this.user.setBase64(outputUser[2]);
                Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
                intentMain.putExtra("User", this.user);
                startActivity(intentMain);
                finish();
            }

        }
    }
}

