package in.gems.fuzionai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.gems.fuzionai.adapters.MessageAdapter;
import in.gems.fuzionai.model.Message;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    private ConstraintLayout actionBar;
    private ImageButton btn_mic, btn_send;
    private EditText editText;
    private Drawable bg_message_box;
    private RecyclerView recyclerView;
    private MaterialToolbar toolbar;
    private ImageView imageView;
    private Bitmap bitmap;
    private byte[] byteArray;
    private String chatBotName;

    private Map<String, Object> dataMap = new HashMap<>();
    private List<HashMap> objectList;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    public final String TAG = "ChatActivity";

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.toolbar);
        btn_mic = findViewById(R.id.btn_mic);
        btn_send = findViewById(R.id.btn_send);
        editText = findViewById(R.id.editTextMessage);
        actionBar = findViewById(R.id.layoutChatBox);
        recyclerView = findViewById(R.id.recyclerView);
        imageView = findViewById(R.id.imageView);
        bg_message_box = actionBar.getBackground().mutate();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        messageList = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        int chatBotLogo = b.getInt("chatBotLogo");
        chatBotName = b.getString("chatBotName");
        toolbar.setLogo(chatBotLogo);
        toolbar.setTitle(chatBotName);

        // Hiding Toolbar
        getSupportActionBar().hide();

        // changing colors of some components according to theme
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                // Dark mode is on
                toolbar.setBackgroundColor(Color.parseColor("#2C302A"));
                toolbar.setNavigationIconTint(Color.parseColor("#FFFFFFFF"));
                bg_message_box.setColorFilter(Color.parseColor("#2C302A"), PorterDuff.Mode.ADD);    // C9DAAF | D4DACD
                editText.setHintTextColor(Color.parseColor("#FFFFFFFF"));
                btn_mic.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.md_theme_light_primaryContainer));
                btn_send.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.md_theme_light_primaryContainer));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                // Dark mode is off
                toolbar.setBackgroundColor(Color.parseColor("#D4DACD"));
                bg_message_box.setColorFilter(Color.parseColor("#D4DACD"), PorterDuff.Mode.ADD);    // C9DAAF | D4DACD
                editText.setHintTextColor(Color.parseColor("#131F0D"));
                btn_mic.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.md_theme_light_onSecondaryContainer));
                btn_send.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.md_theme_light_onSecondaryContainer));
                break;
        }

        // setting up recyclerView
        messageAdapter = new MessageAdapter(messageList, this);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);      // because we want to scroll from below to up
        recyclerView.setLayoutManager(linearLayoutManager);

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
                        objectList = (List<HashMap>) dataMap.get(chatBotName);
                        if (objectList != null) {
                            for (HashMap messageMap : objectList) {
                                Message message = new Message(messageMap.get("message").toString(), messageMap.get("sender").toString());
                                messageList.add(message);
                            }
                        }
                        messageAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "No such document");
                        Toast.makeText(ChatActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.w(TAG, "get failed with ", task.getException());
                    Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                messageAdapter.notifyDataSetChanged();
            }
        });

        // onClick on navigationIcon (Back button)
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // onClick on toolbar
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Info of chatBot should appear in different screen
            }
        });

        // Mic Button
        btn_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySpeechRecognizer();
            }
        });

        // Send Button
        btn_send.setOnClickListener(view -> {
            String message = editText.getText().toString().trim();
            if (!message.isEmpty()) {
                addTextToChat(message, "User");
                switch (chatBotName) {
                    case "Aura":
                        createChat(message);
                        break;
                    case "Pixel":
                        createImage(message);
                        break;
                }

                // clearing text
                editText.setText("");
            }
        });
    }

    void createChat(String message) {
        messageList.add(new Message("Typing...", chatBotName));

        JSONObject requestBodyObject = new JSONObject();
        try {
            requestBodyObject.put("model", "gpt-3.5-turbo");
            JSONArray messagesArray = new JSONArray();
            JSONObject messageObject = new JSONObject();
            messageObject.put("role", "user");
            messageObject.put("content", message);
            messagesArray.put(messageObject);
            requestBodyObject.put("messages", messagesArray);
        } catch (JSONException e) {
            Log.w(TAG, "createChat: ", e);
        }

        RequestBody requestBody = RequestBody.create(requestBodyObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(OpenAI.API_POST_CHAT)
                .header("Authorization", "Bearer " + OpenAI.API_KEY)
                .post(requestBody)               .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject responseJsonObject;
                    try {
                        responseJsonObject = new JSONObject(response.body().string());
                        JSONArray choicesArray = responseJsonObject.getJSONArray("choices");
                        String result = choicesArray.getJSONObject(0).getJSONObject("message").getString("content");
                        addTextResponse(result.trim());
                    } catch (JSONException e) {
                        Log.w(TAG, "onResponse: ", e);
                    }
                }
                else {
                    addTextResponse("Failed to load response due to " + response.body().string());
                    Log.d(TAG, "onResponse: " + response);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addTextResponse("Failed to load response due to " + e.getMessage());
                Log.w(TAG, "onFailure: ", e);
            }
        });
    }

    void createImage(String prompt) {
        messageList.add(new Message("Generating...", chatBotName));

        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put("prompt", prompt);
            requestJsonObject.put("size", "1024x1024");
        } catch (JSONException e) {
            Log.w(TAG, "createImage: ", e);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        RequestBody requestBody = RequestBody.create(requestJsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(OpenAI.API_POST_IMAGE)
                .header("Authorization", "Bearer " + OpenAI.API_KEY)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseJSONObject = new JSONObject(response.body().string());
                        Log.i(TAG, "onResponse: " + responseJSONObject);
                        final String[] imageURL = {responseJSONObject.getJSONArray("data").getJSONObject(0).getString("url")};

                        // Getting bitmap from image URL
                        try {
                            InputStream inputStream = new URL(imageURL[0]).openStream();
                            bitmap = BitmapFactory.decodeStream(inputStream);
                        } catch (IOException e) {
                            Log.w(TAG, "run: ", e);
                        }

                        // Converting Bitmap to byte[]
                        if (bitmap != null) {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
                            byteArray = byteArrayOutputStream.toByteArray();
                        }

                        // Uploading bitmap image to Cloud Storage
                        StorageReference ref = storageReference.child("pixel/" + System.currentTimeMillis() + ".jpg ");
                        ref.putBytes(byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageURL[0] = uri.toString();
                                        Log.i(TAG, "onSuccess: Image uploaded to Firebase Cloud Storage");
                                        addTextResponse(imageURL[0].trim());
                                        messageAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "onFailure: ", e);
                            }
                        });
                    }
                    catch (Exception e) {
                        Log.w(TAG, "onResponse: ", e);
                    }
                }
                else {
                    addTextResponse("Failed to load response due to " + response.body().string());
                    Log.d(TAG, "onResponse: " + response);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addTextResponse("Failed to load response due to " + e.getMessage());
                Log.w(TAG, "onFailure: ", e);
            }
        });
    }

    void addTextResponse(String response) {
        messageList.remove(messageList.size() - 1);
        addTextToChat(response, chatBotName);
    }

    // adding message & senderName to messageList
    // notifying recyclerView that dataset has been changed
    // scrolling recyclerView to bottom
    void addTextToChat(String message, String sender) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sender));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());

                // sending messageList to Firebase
                dataMap.put(chatBotName, messageList);
                firestore.collection("users")
                        .document(firebaseUser.getUid()).set(dataMap, SetOptions.merge())
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
                                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // This starts the activity and populates the intent with the speech text.
        startActivityForResult(intent, 103);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where we process the intent & extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 103 && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            editText.setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}