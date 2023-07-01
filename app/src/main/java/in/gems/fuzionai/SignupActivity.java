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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private TextInputLayout tilName, tilEmail, tilPassword;
    private EditText editTextName, editTextEmail, editTextPassword;
    private ImageButton btn_signup;
    private TextView textView;

    private final String TAG = "Signup Activity";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private boolean allOkay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        tilName = findViewById(R.id.textInputLayoutName);
        tilEmail = findViewById(R.id.textInputLayoutEmail);
        tilPassword = findViewById(R.id.textInputLayoutPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textView = findViewById(R.id.textView2);
        btn_signup = findViewById(R.id.btn_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = pref.edit();

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View view) {
                allOkay = true;
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim().toLowerCase();
                String password = editTextPassword.getText().toString().trim();

                // clearing error onTouch of editTexts
                editTextName.setOnTouchListener((v, event) -> {
                    tilName.setError(null);
                    return false;
                });
                editTextEmail.setOnTouchListener((v, event) -> {
                    tilEmail.setError(null);
                    return false;
                });
                editTextPassword.setOnTouchListener((v, event) -> {
                    tilPassword.setError(null);
                    return false;
                });

                // setting error if any field is empty
                if (!Pattern.matches("[a-zA-Z .]+", name)) {
                    allOkay = false;
                    tilName.setError("Name should contain only alphabets");
                }
                if (name.isEmpty()) {
                    allOkay = false;
                    tilName.setError("Enter name");
                }
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
                                signup(name, email, password);
                            } else {
                                Log.i(TAG, "CONNECTED TO INTERNET: FALSE");
                                Toast.makeText(SignupActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(SignupActivity.this, SignInActivity.class));
            }
        });
    }

    // closing app onBackPress
    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

    // Firebase Signup with Email & Password
    void signup(String name, String email, String password) {
        Log.d(TAG, "signup() called with: name = [" + name + "], email = [" + email + "], password = [" + password + "]");

        // Firebase SignUp with Email & Password
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "createUserWithEmail:success");
                            Toast.makeText(SignupActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                            // creating an user object to send it to Firestore
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            user.put("password", password);

                            // Sending user object to Firestore
                            firestore.collection("users")
                                    .document(firebaseUser.getUid()).set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "DocumentSnapshot added");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                            Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            signIn(email, password);
                        }
                        else {
                            switch (task.getException().getMessage()) {
                                // Connected to wifi with no access rights
                                case "An internal error has occurred. [ java.security.cert.CertPathValidatorException:Trust anchor for certification path not found. ]":
                                    Log.d(TAG, "createUserWithEmail:failure: " + task.getException().getMessage());
                                    Toast.makeText(SignupActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                                    break;

                                // Invalid Email
                                case "The email address is badly formatted.":
                                    Log.d(TAG, "createUserWithEmail:failure: Invalid Email");
                                    Toast.makeText(SignupActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                                    break;

                                // User already registered
                                case "The email address is already in use by another account.":
                                    Log.d(TAG, "createUserWithEmail:failure: User already registered");
                                    Toast.makeText(SignupActivity.this, "User already registered", Toast.LENGTH_SHORT).show();
                                    break;

                                // Password is short
                                case "The given password is invalid. [ Password should be at least 6 characters ]":
                                    Log.d(TAG, "createUserWithEmail:failure: Password should be at least 6 characters");
                                    Toast.makeText(SignupActivity.this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
                                    break;

                                // Other failure reasons
                                default:
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
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

                            // storing user info in SharedPreference
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            editor.putString("user_email", user.getEmail());
                            editor.putString("user_id", user.getUid());
                            editor.putBoolean("isLoggedIn", true);
                            editor.commit();

                            // going to MainActivity
                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        }
                        else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}