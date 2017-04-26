package id.co.unila.adi.laundryapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import id.co.unila.adi.laundryapp.helpers.SessionManager;

public class SplashActivity extends AppCompatActivity {

    // Session Manager Class
    SessionManager session;
    CountDownTimer ctdn;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        ctdn = new CountDownTimer(5*1000, 1000) {
            int i=0;
            public void onTick(long millisUntilFinished) {
                i++;
                mProgressBar.setProgress(i);

            }
            public void onFinish() {
                i++;
                mProgressBar.setProgress(i);

                if(session.isLoggedIn()){
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                SplashActivity.this.finish();
            }
        }.start();
    }
}
