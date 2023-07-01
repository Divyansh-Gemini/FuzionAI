package in.gems.fuzionai;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.gems.fuzionai.adapters.ChatAdapter;
import in.gems.fuzionai.model.Message;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private Integer[] logos = {R.drawable.img_logo_aura,
            R.drawable.img_logo_pixel};
    private String[] chatNames = {"Aura", "Pixel"};
    private String[] lastMessages = {"Get started with Aura", "Get started with Pixel"};

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    private String TAG = "HomeFragment";
    private Map<String, Object> dataMap = new HashMap<>();
    private List<HashMap> auraObjectList, pixelObjectList;
    private List<Message> messageList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = myView.findViewById(R.id.recyclerView);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        // Getting chat from Firestore
        DocumentReference docRef = firestore.collection("users").document(firebaseUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        dataMap = document.getData();
                        Log.i(TAG, dataMap.toString());
                        auraObjectList = (List<HashMap>) dataMap.get("Aura");
                        pixelObjectList = (List<HashMap>) dataMap.get("Pixel");

                        // setting last messages to the array
                        if (auraObjectList != null)
                            lastMessages[0] =  auraObjectList.get(auraObjectList.size() - 2).get("message").toString();
                        if (pixelObjectList != null)
                            lastMessages[1] =  pixelObjectList.get(pixelObjectList.size() - 2).get("message").toString();

                        // setting data to the adapter, and adapter to the RecyclerView
                        ChatAdapter adapter = new ChatAdapter(getActivity(), logos, chatNames, lastMessages);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(adapter);

                    } else {
                        Log.d(TAG, "No such document");
                        Toast.makeText(getActivity(), "Data not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.w(TAG, "get failed with ", task.getException());
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return myView;
    }
}