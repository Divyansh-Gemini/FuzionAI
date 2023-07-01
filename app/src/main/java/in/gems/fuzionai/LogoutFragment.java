package in.gems.fuzionai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class LogoutFragment extends Fragment {
    private final String TAG = "Logout Fragment";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_logout, container, false);
        pref = this.getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = pref.edit();

        editor.clear();
        editor.commit();

        Log.i(TAG, "Logout successful");
        Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(getActivity(), SignInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

        return myView;
    }
}