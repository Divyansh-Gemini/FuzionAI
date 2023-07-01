package in.gems.fuzionai;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class HelpAndFeedbackFragment extends Fragment {
    private TextInputLayout tilSubject, tilBody;
    private EditText editTextSubject, editTextBody;
    private MaterialButton btn_send;

    private boolean allOkay;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView =  inflater.inflate(R.layout.fragment_help_and_feedback, container, false);
        tilSubject = myView.findViewById(R.id.textInputLayoutSubject);
        tilBody = myView.findViewById(R.id.textInputLayoutMessage);
        editTextSubject = myView.findViewById(R.id.editTextSubject);
        editTextBody = myView.findViewById(R.id.editTextMessage);
        btn_send = myView.findViewById(R.id.btn_send);

        // clearing error onTouch of editTexts
        editTextSubject.setOnTouchListener((v, event) -> {
            tilSubject.setError(null);
            return false;
        });
        editTextBody.setOnTouchListener((v, event) -> {
            tilBody.setError(null);
            return false;
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allOkay = true;
                String email = "divyanshgemini3232@gmail.com";
                String subject = editTextSubject.getText().toString().trim();
                String body = editTextBody.getText().toString().trim();

                // setting error if any field is empty
                if (subject.isEmpty()) {
                    allOkay = false;
                    tilSubject.setError("Enter Subject");
                }
                if (body.isEmpty()) {
                    allOkay = false;
                    tilBody.setError("Enter Body");
                }
                if (allOkay) {
                    sendEmail(email, subject, body);
                }
            }
        });
        return myView;
    }

    void sendEmail(String email, String subject, String body) {
        String type = "message/rfc822";
        String data = "mailto:" + email + "?subject=" + subject.replace(" ", "%20")
                + "&body=" + body.replace(" ", "%20").replace("\n", "%0a");

        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType(type);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, body);
        i.setData(Uri.parse(data));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // this will make such that when user returns to the app, app is displayed, instead of the email app.
        startActivity(i);
    }
}