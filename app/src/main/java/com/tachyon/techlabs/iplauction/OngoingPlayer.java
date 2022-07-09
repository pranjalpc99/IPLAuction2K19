package com.tachyon.techlabs.iplauction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

import javax.annotation.Nullable;

public class OngoingPlayer extends AppCompatActivity {

    TextView name1text,name2text,pointtext,matchtext,runtext,wickettext,basetext;
    AllPlayerInfo allPlayerInfo = new AllPlayerInfo();
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String useremail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id;
    int current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_player);

        name1text = findViewById(R.id.name1);
        name2text = findViewById(R.id.name2);
        pointtext = findViewById(R.id.pointtext);
        matchtext = findViewById(R.id.matchtext);
        runtext = findViewById(R.id.runstext);
        wickettext = findViewById(R.id.wicketstext);
        basetext =findViewById(R.id.basepricetext);

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
                getCurrent();
            }
        });
    }

    public void getCurrent()
    {
        DocumentReference documentReference = db.collection(id).document("CurrentPlayer");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                current = Objects.requireNonNull(Objects.requireNonNull(documentSnapshot).getLong("curr")).intValue();
                //Toast.makeText(OngoingPlayer.this, current+"", Toast.LENGTH_SHORT).show();
                setText();
            }
        });
    }

    public void setText()
    {
        String pt = allPlayerInfo.points[current]+"";
        String match = allPlayerInfo.match[current]+"";
        String run = allPlayerInfo.run[current]+"";
        String wicket = allPlayerInfo.wicket[current]+"";
        String base = allPlayerInfo.basecost[current]+"";
        name1text.setText(allPlayerInfo.fname[current]);
        name2text.setText(allPlayerInfo.sname[current]);
        pointtext.setText(pt);
        matchtext.setText(match);
        runtext.setText(run);
        wickettext.setText(wicket);
        basetext.setText(base);
    }
}
