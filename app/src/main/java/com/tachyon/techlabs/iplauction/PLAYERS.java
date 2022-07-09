package com.tachyon.techlabs.iplauction;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PLAYERS extends AppCompatActivity implements Animation.AnimationListener {
    ListView listView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    final List<String> list = new ArrayList<>();
    final List<Object> price = new ArrayList<>();
    Animation animBlink;
    String  player;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        final int[] pric={500000,4000000,244423,23,233};
       // final int[] points={30,23,34,45,344};
        animBlink = AnimationUtils.loadAnimation(this,R.anim.textblink);
        animBlink.setAnimationListener(this);
        TextView playerauction=findViewById(R.id.player_auction);
        playerauction.setAnimation(animBlink);








        final DocumentReference docRef = db.collection("Players").document(Objects.requireNonNull(currentUser.getEmail())).collection("PlayersList").document("All Players");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                               @Override
                                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                   if (task.isSuccessful()) {
                                                       {
                                                           DocumentSnapshot d = task.getResult();
                                                           if (d!=null)
                                                           {
                                                               List<String> list = (List<String>) d.get("PlayersName");
                                              //                 for (String item : list) {
                                                //                   Log.d("playername", item);
                                                            //   }
                                                               List<Long> valuee = (List<Long>) d.get("PlayersPrice");
                                                               for (Long item : valuee) {
                                                                   Log.d("playerprice", item+"");
                                                               }
                                                               List<Integer>  points = (List<Integer>) d.get("Points");
                                                               for (Long item : valuee) {
                                                                   Log.d("playerpoints", item+"");
                                                               }
                                                            //  Log.d("firestore array",player);
                                                               int[] playerprice=(int[]) d.get("PlayersPrice");
                                                               Map<String,Object> map = task.getResult().getData();
                                                               Log.d("Hashmap",map.entrySet().toString());
                                                              // Toast.makeText(PLAYERS.this, map.toString(), Toast.LENGTH_SHORT).show();
                                                               alldata(map);

                                                               String[] players=new String[map.size()];
                                                               int[] playersworth=new int[map.size()];
                                                               players=list.toArray(players);
                                                              // playersworth=price.toArray(playersworth);
                                                               //for(int i=0;i<price.size();i++)
                                                                 //  playersworth[i] =(Integer) price[i];
                                                                for(Object s:price)
                                                                 Log.d("price",s+"");
                                                                 // Log.d("arra",players.toString());
                                                               opponents_team_playerslist_adapter opponents_team_playerslist_adapter = new opponents_team_playerslist_adapter(getApplicationContext(),list,valuee,points);
                                                               listView.setAdapter(opponents_team_playerslist_adapter);


                                                           }
                                                       }
                                                   }
                                               }
                                           });


            //String [{} players_name = list.toArray(new String[0]);
        //Integer pri[]= price.toArray(new Integer[price.size()]);
     //   Toast.makeText(this, list.toString(), Toast.LENGTH_LONG).show();
       // Toast.makeText(this, price.toString()+"hey", Toast.LENGTH_SHORT).show();
        Log.d("PlayersList",list.toString());




    listView=findViewById(R.id.listofallplayers);
       // listView.setBackgroundColor(getResources().getColor(R.color.colorAccent));




       // listView.setAdapter(opponents_team_playerslist_adapter);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fadein_slidedown);
        listView.startAnimation(animation);

       /* DocumentReference messageRef = db
                .collection("rooms").document("roomA")
                .collection("messages").document("message1");*/


    }
    public void alldata(Map<String,Object> map)
    {

        for (Map.Entry<String, Object> entry : map.entrySet())
        {
            list.add(entry.getKey());
            // Toast.makeText(PLAYERS.this, list.toString(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(PLAYERS.this, entry.getKey()+"abhi", Toast.LENGTH_SHORT).show();
            price.add(entry.getValue()+"");
            Log.d("Hash",list.toString());
            Log.d("map",entry.getValue()+"");
            // Toast.makeText(PLAYERS.this, entry.getValue().toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == animBlink) {
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,WaitingForPlayersActivity.class));
    }
}
