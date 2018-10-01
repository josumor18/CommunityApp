package elcarmen.project.community.Business;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import elcarmen.project.community.R;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Chat chat = (Chat) intent.getSerializableExtra("chat");
        //getSupportActionBar().setSubtitle(chat.getCommunity_name());


    }

    public void send_message(View view){

    }
}
