package in.gems.fuzionai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {
    private TextInputLayout tilEmail, tilPassword;
    private EditText editTextEmail, editTextPassword;
    private ImageButton btn_sign_in;
    private TextView textView;

    private static final String TAG = "SignIn Activity";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private FirebaseAuth firebaseAuth;
    private boolean allOkay;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        tilEmail = findViewById(R.id.textInputLayoutEmail);
        tilPassword = findViewById(R.id.textInputLayoutPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btn_sign_in = findViewById(R.id.btn_signIn);
        textView = findViewById(R.id.textView2);
        firebaseAuth = FirebaseAuth.getInstance();
        pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = pref.edit();

        // clearing error onTouch of editTexts
        editTextEmail.setOnTouchListener((v, event) -> {
            tilEmail.setError(null);
            return false;
        });
        editTextPassword.setOnTouchListener((v, event) -> {
            tilPassword.setError(null);
            return false;
        });

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allOkay = true;
                String email = editTextEmail.getText().toString().trim().toLowerCase();
                String password = editTextPassword.getText().toString().trim();

                // setting error if any field is empty
                if (!Pattern.matches("(?!.*\\.\\.)[a-zA-Z0-9_-]*[.]*[a-zA-Z0-9_-]+@[a-zA-Z0-9-.]+\\.[a-zA-Z]{2,}", email)) {
                    allOkay = false;
                    tilEmail.setError("Enter a valid email");
                }
                if (email.isEmpty()) {
                    allOkay = false;
                    tilEmail.setError("Enter email");
                }
                if (password.isEmpty()) {
                    allOkay = false;
                    tilPassword.setError("Enter password");
                }
                if (allOkay) {
                    // Creating an instance of the InternetCheckTask class
                    // & passing an implementation of the InternetCheckListener interface to the constructor.
                    // InternetCheckListener interface provides a callback method onInternetCheckDone() that will be called when the task completes.
                    InternetCheckTask internetCheckTask = new InternetCheckTask(new InternetCheckTask.InternetCheckListener() {
                        @Override
                        public void onInternetCheckDone(boolean isOnline) {
                            if (isOnline) {
                                Log.i(TAG, "CONNECTED TO INTERNET: TRUE");
                                signIn(email, password);
                            } else {
                                Log.i(TAG, "CONNECTED TO INTERNET: FALSE");
                                Toast.makeText(SignInActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    internetCheckTask.execute();
                }
            }
        });

        // going to SignInActivity onClick of textView
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignupActivity.class));
            }
        });
    }

    // closing app onBackPress
    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    // Firebase SignIn with Email & Password
    void signIn(String email, String password) {
        Log.d(TAG, "signIn() called with: email = [" + email + "], password = [" + password + "]");

        // Firebase SignIn with Email & Password
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "signInWithEmail:success");
                            Toast.makeText(SignInActivity.this, "Logged In", Toast.LENGTH_SHORT).show();

                            // storing user info in SharedPreference
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            editor.putString("user_email", user.getEmail());
                            editor.putString("user_id", user.getUid());
                            editor.putBoolean("isLoggedIn", true);
                            editor.commit();

                            // going to MainActivity
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        }
                        else {
                            switch (task.getException().getMessage()) {
                                // Connected to wifi with no access rights
                                case "An internal error has occurred. [ java.security.cert.CertPathValidatorException:Trust anchor for certification path not found. ]":
                                    Log.d(TAG, "createUserWithEmail:failure: " + task.getException().getMessage());
                                    Toast.makeText(SignInActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                                    break;

                                // Invalid Email
                                case "The email address is badly formatted.":
                                    Log.d(TAG, "createUserWithEmail:failure: Invalid Email");
                                    Toast.makeText(SignInActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                                    break;

                                // User not registered
                                case "There is no user record corresponding to this identifier. The user may have been deleted.":
                                    Log.d(TAG, "createUserWithEmail:failure: User not registered");
                                    Toast.makeText(SignInActivity.this, "User not registered", Toast.LENGTH_SHORT).show();
                                    break;

                                // Wrong password
                                case "The password is invalid or the user does not have a password.":
                                    Log.d(TAG, "createUserWithEmail:failure: Wrong Password");
                                    Toast.makeText(SignInActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                    break;

                                // Other failure reasons
                                default:
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}