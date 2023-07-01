package in.gems.fuzionai.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import in.gems.fuzionai.R;
import in.gems.fuzionai.model.Setting;

public class SettingsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Setting> settings;
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;

    public SettingsAdapter(Context context, ArrayList<Setting> settings) {
        this.context = context;
        this.settings = settings;
        this.materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
    }

    private class SettingsViewHolder {
        ImageView imageViewIcon;
        TextView textViewTitle, textViewSubtitle;

        public SettingsViewHolder(View view) {
            imageViewIcon = (ImageView) view.findViewById(R.id.icon);
            textViewTitle = (TextView) view.findViewById(R.id.textView1);
            textViewSubtitle = (TextView) view.findViewById(R.id.textView2);
        }
    }

    @Override
    public int getCount() {
        return settings.size();
    }

    @Override
    public Object getItem(int position) {
        return settings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        SettingsViewHolder viewHolder;

        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_setting_item, container, false);
            viewHolder = new SettingsViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SettingsViewHolder) convertView.getTag();
        }

        // get current item to be displayed
        Setting currentSetting = (Setting) getItem(position);

        // sets the data for the item view
        viewHolder.imageViewIcon.setImageResource(currentSetting.getIcon());
        viewHolder.textViewTitle.setText(currentSetting.getSettingTitle());
        viewHolder.textViewSubtitle.setText(currentSetting.getSettingSubtitle());

        // set click listener for the item view
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case 0:
                        // TODO:
//                        materialAlertDialogBuilder
//                                .setTitle("Choose Theme")
////                                .setMessage()
//                                .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
//                        // Respond to neutral button press
//                             }
//                          .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
//                        // Respond to negative button press
//                    }
//                          .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
//                        // Respond to positive button press
//                    }
//                          .show();

                        break;
                }
            }
        });

        return convertView;
    }

}
