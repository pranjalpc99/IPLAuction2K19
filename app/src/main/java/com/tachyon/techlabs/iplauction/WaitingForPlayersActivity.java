package com.tachyon.techlabs.iplauction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class WaitingForPlayersActivity extends AppCompatActivity {

    Toolbar toolbarRoomWait;
    TextView textViewRoomName;
    String member,roomid,key,boss_namee;
    String boss,roomtext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    TextView bossTextView,joinCodeDisplay,boss_name;
    ListView members_joined;
    List<String> list;
    ArrayAdapter<String> adapter;
    String [] players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_players);

        member = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        DocumentReference docname = db.collection("Players").document(member);
        docname.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                roomid = documentSnapshot.getString("roomid");
                key = documentSnapshot.getString("joinkey");
                getBossName();
                getPlayersName();
            }
        });
        members_joined= (ListView) findViewById(R.id.waiting_player_listview);

        roomtext = getString(R.string.yourroom);
        toolbarRoomWait = findViewById(R.id.app_toolbar);
        textViewRoomName = findViewById(R.id.app_toolbar_nametxt);
        textViewRoomName.setText(roomtext);
        textViewRoomName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textViewRoomName.getLayoutParams();
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        textViewRoomName.setLayoutParams(lp);


        bossTextView = findViewById(R.id.boss_text);
        boss_name = findViewById(R.id.boss_name);

        joinCodeDisplay = findViewById(R.id.join_code_display);


       // assert roomid != null;
        /*
        DocumentReference docRef = db.collection(roomid).document(member);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                boss = documentSnapshot.getString("Owner");
                setTexts();
            }
        });*/


    }

    public void setPlayerNames()
    {
        players = new String[list.size()];
        players = list.toArray(players);
       // Toast.makeText(this, players.toString(), Toast.LENGTH_SHORT).show();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,players);
        members_joined.setAdapter(adapter);
    }

    public void setTexts()
    {
        joinCodeDisplay.setText(key);

        if(boss_namee.equals(member))
        {
            bossTextView.setText(R.string.bosstextview);
            boss_name.setVisibility(View.GONE);
        }
        else
        {
          //  String text = R.string.membertextview+"";
            boss_name.setText(boss_namee);
            bossTextView.setText(R.string.membertextview);
        }
    }

    public void getBossName()
    {
        DocumentReference doc = db.collection("keyValues").document(key);
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                boss_namee = documentSnapshot.getString("owner");
                setTexts();
            }
        });
    }

    public void getPlayersName()
    {
        db.collection(roomid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                list = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    if(!document.getId().equals("CurrentPlayer"))
                    list.add(document.getId());
                }
                // Log.d(TAG, list.toString());
                // Toast.makeText(WaitingForPlayersActivity.this, list.toString(), Toast.LENGTH_SHORT).show();
                setPlayerNames();

            }
        });
    }

    public void qr_code(View view) {
        Intent qrcode=new Intent(this,qr_code_generator.class);
        qrcode.putExtra("Join Code",key);
        startActivity(qrcode);
        finish();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Room?");
        builder.setMessage("Do you really wish to leave the room?");
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection(roomid).document(member).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(member.equals(boss_namee))
                        {
                            db.collection(roomid).document("CurrentPlayer").delete();
                        }

                        DocumentReference updateRef = db.collection("Players").document(member);
                        updateRef.update("inRoom",0).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(WaitingForPlayersActivity.this,AfterRegistrationMainActivity.class));
                                finish();
                                //Toast.makeText(WaitingForPlayersActivity.this, "Left the room", Toast.LENGTH_SHORT).show();
                                //Toast.makeText(WaitingForPlayersActivity.this, "User details updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });
        builder.setNegativeButton(R.string.no,null);
        builder.show();
    }

    public void start_game(View view) {
        startActivity(new Intent(WaitingForPlayersActivity.this,Start_Game.class));
        finish();

    }
}
