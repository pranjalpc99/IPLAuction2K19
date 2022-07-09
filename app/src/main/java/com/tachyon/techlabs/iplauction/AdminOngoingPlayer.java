package com.tachyon.techlabs.iplauction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AdminOngoingPlayer extends AppCompatActivity {

    List<String> list;
    AllPlayerInfo allPlayerInfo = new AllPlayerInfo();
    ArrayAdapter<String> adapter;
    String [] players;
    ListView playerlist;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String useremail,id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_ongoing_player);

        list = new ArrayList<>(Arrays.asList(allPlayerInfo.fullname));

        playerlist = findViewById(R.id.adminlist);

        players = new String[list.size()];
        players = list.toArray(players);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,players);
        playerlist.setAdapter(adapter);

        getId();

    }

    public void getId()
    {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        useremail = Objects.requireNonNull(currentUser).getEmail();

        DocumentReference documentReference = db.collection("Players").document(useremail);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                id = documentSnapshot.getString("roomid");
                setCurrentPlayer();
            }
        });
    }


    public void setCurrentPlayer()
    {
        final DocumentReference documentReference = db.collection(id).document("CurrentPlayer");
        playerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                documentReference.update("curr",position+1);
            }
        });

    }

}
