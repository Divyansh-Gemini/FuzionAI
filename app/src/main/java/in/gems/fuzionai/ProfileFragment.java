package in.gems.fuzionai;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private ImageView imageViewProfilePhoto;
    private EditText editTextName, editTextEmail, editTextPassword;
    private Spinner spinner;
    private RadioGroup radioGroup;
    private RadioButton radioMale, radioFemale;
    private Button btn_save;

    private final String TAG = "ProfileFragment";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;

    private String name = "";
    private String email = "";
    private String password = "";
    private String gender = "";
    private String state = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_profile, container, false);
        imageViewProfilePhoto = myView.findViewById(R.id.imageViewProfilePhoto);
        editTextName = myView.findViewById(R.id.editTextName);
        editTextEmail = myView.findViewById(R.id.editTextEmail);
        editTextPassword = myView.findViewById(R.id.editTextPassword);
        spinner = myView.findViewById(R.id.spinner);
        radioGroup = myView.findViewById(R.id.radioGroupGender);
        radioMale = myView.findViewById(R.id.radioButtonMale);
        radioFemale = myView.findViewById(R.id.radioButtonFemale);
        btn_save = myView.findViewById(R.id.btn_save);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // setting up spinner
        String[] states = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, states);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = states[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {   }
        });

        // getting data from Firestore
        DocumentReference docRef = firestore.collection("users").document(firebaseUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        Map<String, Object> user = document.getData();
                        name = user.get("name").toString();
                        email = user.get("email").toString();
                        password = user.get("password").toString();
                        if (user.get("gender") != null)
                            gender = user.get("gender").toString();
                        if (user.get("state") != null)
                            state = user.get("state").toString();

                        // setting data to components
                        editTextName.setText(name);
                        editTextEmail.setText(email);
                        editTextPassword.setText(password);

                        if (gender != null && !gender.isEmpty()) {
                            if (gender.equals("Male"))
                                radioMale.setChecked(true);
                            else if (gender.equals("Female"))
                                radioFemale.setChecked(true);
                        }

                        if (state != null && !state.isEmpty())
                            spinner.setSelection(Arrays.asList(states).indexOf(state));

                        Log.d(TAG, "Name: " + name);
                        Log.d(TAG, "Email: " + email);
                        Log.d(TAG, "Password: " + password);
                        Log.d(TAG, "Gender: " + gender);
                        Log.d(TAG, "State: " + state);
                    } else {
                        Log.d(TAG, "No such document");
                        Toast.makeText(getActivity(), "Data not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // change profile photo
        imageViewProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: USER CAN UPLOAD PHOTO
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch(checkedId){
                    case 2131362183:
                        gender = "Male";
                        break;
                    case 2131362182:
                        gender = "Female";
                        break;
                    default:
                        gender = "";
                }
            }
        });

        // Saving data
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();

                // creating an user object to send it to Firestore
                Map<String, Object> user = new HashMap<>();
                user.put("name", name);
                user.put("email", email);
                user.put("password", password);
                user.put("gender", gender);
                user.put("state", state);

                // Updating user data to Firestore
                firestore.collection("users")
                        .document(firebaseUser.getUid()).set(user, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Data saved", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "DocumentSnapshot added");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return myView;
    }
}