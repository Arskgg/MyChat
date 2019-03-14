package com.arskgg.mychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.arskgg.mychat.Model.ChatMessage;
import com.arskgg.mychat.ViewHolder.MessageViewHolder;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 77;

    RelativeLayout mainLayout;
    ImageButton sendBtn;
    EditText messageEdt;

    RecyclerView recyclerChat;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter adapter;

    FirebaseDatabase database;
    DatabaseReference messageTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.mainLayout);
        sendBtn = findViewById(R.id.sendBtn);
        messageEdt = findViewById(R.id.messageEdt);
        recyclerChat = findViewById(R.id.recyclerChat);

        //Firebase
        database = FirebaseDatabase.getInstance();
        messageTable = database.getReference("Messages");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


        //Check if not sign in
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        }
        else
        {
            Snackbar.make(mainLayout, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
        }

//        recyclerChat.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerChat.setLayoutManager(layoutManager);



        //Load content
        displayChatMessages();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menuSignOut)
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                }
            });

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE && resultCode == RESULT_OK)
        {
            Snackbar.make(mainLayout, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
            displayChatMessages();
        }
        else{
            Snackbar.make(mainLayout, "LogIn failed, please try again", Snackbar.LENGTH_SHORT).show();
            finish();
        }

    }

    private void sendMessage() {

        if(!messageEdt.getText().toString().trim().isEmpty())
        {
            messageTable.push().setValue(new ChatMessage(messageEdt.getText().toString().trim(), FirebaseAuth.getInstance().getCurrentUser().getEmail()));
            messageEdt.setText("");
        }

    }


    private void displayChatMessages() {

        FirebaseRecyclerOptions<ChatMessage> option = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(messageTable, ChatMessage.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(option) {
            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View v = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.message_item, viewGroup, false);

                return new MessageViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull ChatMessage model) {


                holder.userName.setText(model.getUser());
                holder.message.setText(model.getMessage());
                holder.time.setText(DateFormat.format("dd-MM-yyyy (HH:mm)", model.getTime()));

            }
        };

        adapter.startListening();
        recyclerChat.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
