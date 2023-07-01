package in.gems.fuzionai.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import in.gems.fuzionai.ChatActivity;
import in.gems.fuzionai.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private Context context;
    private Integer[] logos;
    private String[] chatNames, lastMessages;

    public ChatAdapter(Context context, Integer[] logos, String[] chatNames, String[] lastMessages) {
        this.context = context;
        this.logos = logos;
        this.chatNames = chatNames;
        this.lastMessages = lastMessages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_chat_card, parent, false);
        ChatViewHolder chatViewHolder = new ChatViewHolder(view);
        return chatViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.imageView.setImageResource(logos[position]);
        holder.textViewChatName.setText(chatNames[position]);
        holder.textViewLastName.setText(lastMessages[position]);

        holder.chatCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("chatBotLogo", logos[holder.getAdapterPosition()]);
                intent.putExtra("chatBotName", chatNames[holder.getAdapterPosition()]);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return logos.length;
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewChatName, textViewLastName;
        MaterialCardView chatCard;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewChatName = itemView.findViewById(R.id.textViewChatName);
            textViewLastName = itemView.findViewById(R.id.textViewLastMessage);
            chatCard = itemView.findViewById(R.id.chatCard);
        }
    }
}