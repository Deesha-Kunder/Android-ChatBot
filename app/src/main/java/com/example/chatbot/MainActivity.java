package com.example.chatbot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;


public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText message;
    ImageView send;
    Toolbar toolbar;
    List<MessageModel> modelList;
    MessageAdapter adapter;
    String question;
    String APIKEY = BuildConfig.myAPIKey;

    private GenerativeModelFutures model;
    private Executor executor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Initialize generative Model
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", APIKEY);
        model = GenerativeModelFutures.from(gm);
        //use Executor for asynchronous operation
       executor = Executors.newSingleThreadExecutor();

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        //set backArrow and handle back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }


        //Initialization
        recyclerView = findViewById(R.id.recyclerView);
        message = findViewById(R.id.mainEditText);
        send = findViewById(R.id.sendIcon);

        modelList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MessageAdapter(modelList, this);
        recyclerView.setAdapter(adapter);

        //onclickListener to send
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question = message.getText().toString().trim();
                if (question.isEmpty()) {
                    Toast.makeText(MainActivity.this, "write something", Toast.LENGTH_SHORT).show();
                } else {
                    addToChat(question, MessageModel.SENT_BY_ME);
                    message.setText("");
                    addAPICall();

                }
            }
        });
    }

    private void addToChat(String question, String sentByMe) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                modelList.add(new MessageModel(question, sentByMe));
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });
    }


    private void addAPICall() {
        //show typing before response is received
        modelList.add(new MessageModel("Typing...", MessageModel.SENT_BY_BOT));
        adapter.notifyItemInserted(modelList.size() - 1);


        //create content to send
        Content content = new Content.Builder()
                .addText(question)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {

            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();


                runOnUiThread(() -> {
                    modelList.remove(modelList.size() - 1);
                    addToChat(resultText, MessageModel.SENT_BY_BOT);
                });

            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    modelList.remove(modelList.size() - 1);
                    addToChat("Error" + t.getMessage(), MessageModel.SENT_BY_BOT);
                });
                t.printStackTrace();
            }
        }, executor);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
