package de.hshl.softwareprojekt;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseHelperUser userDatabase;
    private Button regButton;
    private EditText userNameField;
    private EditText pwField;
    private EditText emailField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.userDatabase = new DatabaseHelperUser(this);
        this.regButton = findViewById(R.id.regButton);
        this.userNameField = findViewById(R.id.userNameField);
        this.pwField = findViewById(R.id.passwordField);
        this.emailField = findViewById(R.id.emailField);

        this.regButton.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar9);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    public void onClick(View v) {
        String username = this.userNameField.getText().toString();
        String pw = this.pwField.getText().toString();
        String email = this.emailField.getText().toString();

        if(username != null && pw != null && email != null) {
            this.userDatabase.insertData(username, email, pw);
            Intent startLogin = new Intent(SignUpActivity.this, LoginActivity.class);
            startLogin.putExtra("email", email);
            startLogin.putExtra("pw", pw);

            setResult(RESULT_OK, startLogin);
            finish();
        }
        else{
            Log.e("Fields", "emtpy");
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
