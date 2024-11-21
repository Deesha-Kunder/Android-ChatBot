package com.example.chatbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.noties.markwon.Markwon;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    List<MessageModel> modelList;
    Context context;
    Markwon markwon;

    public MessageAdapter(List<MessageModel>modelList,Context context){
        this.modelList=modelList;
        this.context=context;
        this.markwon = Markwon.create(context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_message,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageModel model = modelList.get(position);

        if(model.getSentBy().equals(MessageModel.SENT_BY_ME)){
            holder.leftBotMsg.setVisibility(View.GONE);
            holder.rightUsersMsg.setVisibility(View.VISIBLE);
            holder.userMsgTv.setText(model.getMessage());
        }else{
            holder.rightUsersMsg.setVisibility(View.GONE);
            holder.leftBotMsg.setVisibility(View.VISIBLE);
            markwon.setMarkdown(holder.botMsgTv,model.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout leftBotMsg,rightUsersMsg;
        TextView botMsgTv,userMsgTv;

        public ViewHolder(View item){
            super(item);
            leftBotMsg=item.findViewById(R.id.leftBotsMsg);
            rightUsersMsg = item.findViewById(R.id.rightUserMsg);
            botMsgTv = item.findViewById(R.id.botMsgTV);
            userMsgTv = item.findViewById(R.id.usersMsgTV);
        }

    }
}
