package in.gems.fuzionai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = pref.edit();

        // Hiding Toolbar
        getSupportActionBar().hide();

        new Handler().postDelayed(() -> {
            boolean isLoggedIn = pref.getBoolean("isLoggedIn", false);
            if (isLoggedIn) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
            }
            finish();
        }, 2000);
    }
}