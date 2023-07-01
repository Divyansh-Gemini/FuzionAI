package in.gems.fuzionai;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import in.gems.fuzionai.adapters.SettingsAdapter;
import in.gems.fuzionai.model.Setting;

public class SettingsFragment extends Fragment {
    private ListView listView;
    private SettingsAdapter adapter;
    private ArrayList<Setting> settingsArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_settings, container, false);
        listView = myView.findViewById(R.id.listView);

        settingsArrayList = new ArrayList<>();
        settingsArrayList.add(new Setting(R.drawable.icon_theme, "Theme", "System Default"));

        adapter = new SettingsAdapter(getActivity(), settingsArrayList);

        listView.setAdapter(adapter);

        return myView;
    }
}