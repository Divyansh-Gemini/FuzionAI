package in.gems.fuzionai.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import in.gems.fuzionai.R;
import in.gems.fuzionai.model.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final String TAG = "MessageAdapter";
    private Handler mainHandler = new Handler();
    private Bitmap bitmap;
    private Context context;
    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rightChatView, leftChatView;
        TextView leftTextView, rightTextView;
        ImageView leftImageView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            rightChatView = itemView.findViewById(R.id.rightChatView);
            leftChatView = itemView.findViewById(R.id.leftChatView);
            rightTextView = itemView.findViewById(R.id.rightMessageBubble);
            leftTextView = itemView.findViewById(R.id.leftMessageBubble);
            leftImageView = itemView.findViewById(R.id.leftImageView);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_bubble, null);
        return new MessageViewHolder(chatView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        holder.rightChatView.setOnClickListener(view -> showPopupMenuForText(holder, holder.rightChatView));
        holder.rightTextView.setOnClickListener(view -> showPopupMenuForText(holder, holder.rightTextView));
        holder.leftTextView.setOnClickListener(view -> showPopupMenuForText(holder, holder.leftTextView));
        holder.leftImageView.setOnClickListener(view -> showPopupMenuForImage(holder, holder.leftTextView));

        if (message.getSender().equals("User")) {
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.rightTextView.setText(message.getMessage());
        }
        else {
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatView.setVisibility(View.VISIBLE);
            switch (message.getMessageType()) {
                case "image":
                    holder.leftTextView.setVisibility(View.GONE);
                    new FetchImage(holder.leftImageView, message.getMessage()).start();
                    break;
                case "text":
                    holder.leftImageView.setVisibility(View.GONE);
                    holder.leftTextView.setText(message.getMessage());
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class FetchImage extends Thread {
        ImageView imageView;
        String imageURL;

        FetchImage (ImageView imageView, String imageURL) {
            this.imageView = imageView;
            this.imageURL = imageURL;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = new URL(imageURL).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            } catch (IOException e) {
                Log.w(TAG, "run: ", e);
                //imageView.setImageDrawable();
            }
        }
    }

    public void showPopupMenuForText(MessageViewHolder holder, View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_for_message, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.item1) {
                    // Gets a handle to clipboard service.
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

                    // Creates a new text clip to put on clipboard
                    ClipData clip = ClipData.newPlainText("FuzionAI", messageList.get(holder.getAdapterPosition()).getMessage());

                    // Set the clipboard's primary clip.
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(context, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                }
                else if (item.getItemId() == R.id.item2){
                    // Sharing text
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, messageList.get(holder.getAdapterPosition()).getMessage());
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    context.startActivity(shareIntent);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public void showPopupMenuForImage(MessageViewHolder holder, View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_for_message, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.item1) {
                    // TODO: Copy image to clipboard

//                    Uri copyUri = Uri.parse("content://com.example.contacts/copy/...");

                    // Creates a new URI clip object.
                    // System uses anonymous getContentResolver() object to get MIME types from provider.
                    // Clip object's label is "URI", and its data is the Uri previously created.
//                    ClipData clip = ClipData.newUri(context.getContentResolver(), "URI", copyUri);
                }
                else if (item.getItemId() == R.id.item2){
                    // TODO: Share image

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    // Example: content://com.google.android.apps.photos.contentProvider/...
                    //shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                    shareIntent.setType("image/jpeg");
                    context.startActivity(Intent.createChooser(shareIntent, null));
                }
                return false;
            }
        });
        popupMenu.show();
    }
}