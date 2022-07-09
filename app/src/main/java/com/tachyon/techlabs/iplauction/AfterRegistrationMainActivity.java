package com.tachyon.techlabs.iplauction;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

public class AfterRegistrationMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    NavigationView navigationView;
    Toolbar toolbarAppName;
    TextView textViewAppName;
    private GoogleSignInClient mGoogleSignInClient;
    EditText user_entered_code;
    String appName;
    Map<String, Object> owner_details = new HashMap<>();
    Map<String, Object> members = new HashMap<>();
   // Map<String, Object> nummembers = new HashMap<>();
    Map<String, Object> keyvalues = new HashMap<>();
    Map<String, Object> used = new HashMap<>();
    Map<String, Object> curr = new HashMap<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userEmail,numUsed;
    String user_joincode,used_joinKey,joinkey,roomID,numOfMembers,owner;
    int newjoinkey,numofmem;
    DataBaseHelper myDB;
    SharedPreferences sp;
    StringBuilder rooms;
    Set<String> set=new HashSet<>();
    ArrayList<String> joined_room_array=new ArrayList<>();
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_registration_main);
        /*
        sp=getSharedPreferences("joined_rooms",Context.MODE_PRIVATE);
        rooms=new StringBuilder();
        */


        if(Build.VERSION.SDK_INT>22)
        {
            //getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_grey));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


        appName = getString(R.string.app_name);
        /*toolbarAppName = findViewById(R.id.app_toolbar);
        textViewAppName = findViewById(R.id.app_toolbar_nametxt);
        textViewAppName.setText(appName);*/

        //setSupportActionBar(toolbarAppName);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(getDrawable(android.R.drawable.ic_menu_close_clear_cancel));

        myDB = new DataBaseHelper(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);

        //String name = getIntent().getExtras().getString("name");
        navigationView.setNavigationItemSelectedListener(this);


        // Access a Cloud Firestore instance from your Activity

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // Create a new user with a first and last name





// Add a new document with a generated ID



    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        //if(isAppInstalled(this,"com.njlabs.showjava"))
        //Toast.makeText(this, "Unistall the Decompiler ", Toast.LENGTH_SHORT).show();
       //  MediaPlayer mp = MediaPlayer.create(this,R.raw.zxing_beep);
         //mp.start();
         //mp.setLooping(true);


    }
    // [END on_start_check_user]

    public void updateUI(FirebaseUser user) {
        if (user != null) {

            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            DocumentReference docRef = db.collection("Players").document(userEmail);
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot.exists())
                    {
                        checkIfInRoom();
                    }
                }
            });
        }
        else
        {
            goToLogin();
        }
    }

    public void goToLogin()
    {
        Intent log = new Intent(AfterRegistrationMainActivity.this,MainActivity.class);
        startActivity(log);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        switch (id)
        {
            case R.id.nav_home:
            break;

            case R.id.nav_players:
                startActivity(new Intent(AfterRegistrationMainActivity.this,PLAYERS.class));
                finish();
                break;
            /*
            case R.id.nav_ongoing:
                startActivity(new Intent(AfterRegistrationMainActivity.this,OngoingPlayer.class));
                finish();
                break;
            */
            case R.id.nav_profile:
                //Intent prof = new Intent(AfterRegistrationMainActivity.this,ProfileActivity.class);
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(AfterRegistrationMainActivity.this,ProfileActivity.class));
                        finish();
                    }
                };
                handler.postDelayed(runnable,250);

                break;

            case R.id.nav_opponents:
                startActivity(new Intent(AfterRegistrationMainActivity.this,activity_vertical_ntb.class));
                finish();
                break;

            case R.id.nav_payments_info:
                startActivity(new Intent(AfterRegistrationMainActivity.this,PaymentInfo.class));
                finish();
                break;

            case R.id.nav_cards:
                storagepermission();

                break;

            case R.id.nav_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                startActivity(Intent.createChooser(share,"Share Via"));
                break;

            case R.id.nav_about_us:
                startActivity(new Intent(AfterRegistrationMainActivity.this,About.class));
                finish();
                break;

            case R.id.nav_developer:
                break;

            case R.id.nav_about_app:
                break;

            case R.id.nav_logout:
                signOut();

                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AfterRegistrationMainActivity.this, "signed out", Toast.LENGTH_SHORT).show();
                       startActivity(new Intent(getApplicationContext(),MainActivity.class));                        // ...
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if(this.mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            onResume();
            Intent lastActivity = new Intent(Intent.ACTION_MAIN);
            lastActivity.addCategory(Intent.CATEGORY_HOME);
            lastActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            lastActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            lastActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(lastActivity);
            finish();
            //System.exit(0);
        }

    }

   public void pushData(View view) {

       /* user.put("first", textedit.getText().toString());
        user.put("last", "Two");
        user.put("born", 3);

        db.collection("users").document(userEmail)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        readData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error adding document", e);
                    }
                }); */


    }

    public void checkIfInRoom()
    {
        //Toast.makeText(this, "checkinroom", Toast.LENGTH_SHORT).show();
        final DocumentReference documentReference = db.collection("Players").document(userEmail);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                int  in = documentSnapshot.getLong("inRoom").intValue();
                if(in==1)
                {
                    startActivity(new Intent(AfterRegistrationMainActivity.this,WaitingForPlayersActivity.class));
                    finish();
                }
            }
        });
    }

    /*
    public void about(View view)
    {
        startActivity(new Intent(this,About.class));
        finish();
    }

    public void readData()
    {
        DocumentReference docRef = db.collection("users").document(userEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        //textname.setText(document.getString("first"));
                    } else {
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    */

    public void create_room(View view) {
        Random_id_generate obj=new Random_id_generate();
        long random_id=obj.id();
        id=random_id+"";
        //textname.setText(id);

        joinkey = id.substring(4,12);

        owner_details.put("1",userEmail);
        owner_details.put("numberOfCards",0);
        owner_details.put("Owner","true");
        owner_details.put("Initial_Amount",1000000);
        owner_details.put("Current_Amount",1000000);
        owner_details.put("yorker",0);
        owner_details.put("no ball",0);
        owner_details.put("right to match",0);
        owner_details.put("legend cards",0);
        owner_details.put("inRoom",1);
        owner_details.put("roomid",id);
        owner_details.put("joinkey",joinkey);
        owner_details.put("itemsPurchased",0);



        DocumentReference docRef = db.collection(id).document(userEmail);
             docRef.set(owner_details)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {

                        setOwner();
                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error adding document", e);
                    }
                });


        //if(checkAvailability(joinkey))
       // {

       // }

        /*
        SharedPreferences.Editor ed=sp.edit();
        try {
            String room = (sp.getString("joined_room",null));
            rooms.append(room);
            rooms.append(",");
            rooms.append(joinkey);
           String all_rooms= rooms.toString();
            //set.add(joinkey);
            ed.putString("joined_room",all_rooms);
            ed.apply();
        }
        catch (Exception e) {

        }*/

    }

    public void setOwner()
    {
        DocumentReference docRef2 = db.collection("Players").document(userEmail);
        docRef2.set(owner_details)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {

                        setKey();
                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void  setKey()
    {
        keyvalues.put("roomId",id);
        keyvalues.put("joinKey",joinkey);
        keyvalues.put("owner",userEmail);
        keyvalues.put("numOfMembers",1+"");

        db.collection("keyValues").document(joinkey)
                .set(keyvalues)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {


                        setCurrentPlayer();
                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        //saveUsedKey();

                        //Boolean result = myDB.insertData(userEmail,"10000000","10000000","true","0","0","0","0","0","1",joinkey);

                        //if(result)
                        //{
                        //Toast.makeText(AfterRegistrationMainActivity.this, "in", Toast.LENGTH_SHORT).show();
                        //Intent roomCreated = new Intent(AfterRegistrationMainActivity.this,WaitingForPlayersActivity.class);
                        //startActivity(roomCreated);
                        //finish();
                        //}
                        //else
                        //{
                        //    Toast.makeText(AfterRegistrationMainActivity.this, "Data Insertion Failed", Toast.LENGTH_SHORT).show();
                        //}


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error adding document", e);
                    }
                });


    }

    public void setCurrentPlayer()
    {
        curr.put("curr",1);
        DocumentReference docRef = db.collection(id).document("CurrentPlayer");
        docRef.set(curr).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    public void saveUsedKey()
    {
        int number = Integer.parseInt(numUsed) + 1;
        used.put(number+"",joinkey);
        used.put("numUsed",number+"");
        db.collection("keyValues").document("usedKey")
                .set(used)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }

    /*
    public boolean checkAvailability(String joinkey)
    {
        DocumentReference docRef = db.collection("keyValues").document("usedKey");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists())
                {
                    //textname.setText(documentSnapshot.getString(""));
                    numUsed = documentSnapshot.getString("numUsed");
                    checkFurther(documentSnapshot.getString("numUsed"));
                }
            }
        });

        return true;
    }
    */

    /*
    public void checkFurther(final String num)
    {
        DocumentReference docRef = db.collection("keyValues").document("usedKey");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists())
                {
                    //textname.setText(documentSnapshot.getString(""));
                    used_joinKey = documentSnapshot.getString(num);
                    checkStep2(Integer.parseInt(num));
                }
            }
        });


    }

    public void checkStep2(int num)
    {
        if(joinkey.equals(used_joinKey))
        {
            Toast.makeText(this, used_joinKey, Toast.LENGTH_SHORT).show();
            newjoinkey = Integer.parseInt(joinkey) + 1;
            joinkey = newjoinkey+"";
            //checkAvailability(joinkey);
            num = num - 1;
            if(num>0)
            checkFurther(num+"");
        }
    }

    */

    public void Join_Room(View view)  {
        //Dialog_fragment dialog=new Dialog_fragment();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        final View viewlayout = inflater.inflate(R.layout.join_room_dialog, null);



        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(viewlayout)
                // Add action buttons
                .setPositiveButton(R.string.joinroom, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        user_entered_code = viewlayout.findViewById(R.id.code);
                        user_joincode = user_entered_code.getText().toString();
                        if(user_joincode.matches(""))
                        {
                            Toast.makeText(getApplicationContext(),"Enter A Valid Code",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            room_join_function(user_joincode);
                        }

                        dialog.cancel();
                        // sign in the user ...
                       // Toast.makeText(this,"Joined successfully",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
       AlertDialog alert=builder.create();
       alert.show();

    }


    public void enterRoom(String room_id,String joinkey)
    {
        DocumentReference docRef2 = db.collection(room_id).document(userEmail);
        DocumentReference docRef = db.collection("Players").document(userEmail);

        members.put("1",userEmail);
        members.put("numberOfCards",0);
        members.put("Owner","false");
        members.put("Initial_Amount",1000000);
        members.put("Current_Amount",1000000);
        members.put("yorker",0);
        members.put("no ball",0);
        members.put("right to match",0);
        members.put("legend cards",0);
        members.put("inRoom",1);
        members.put("roomid",room_id);
        members.put("joinkey",joinkey);
        members.put("itemsPurchased",0);

        docRef2
                .set(members)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        proceedFurther();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error adding document", e);
                    }
                });

        docRef
                .set(members)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        proceedFurther();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error adding document", e);
                    }
                });


      /*  docRef2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists())
                {
                    numOfMembers = documentSnapshot.getString("numberOfMembers");


                }
            }
        });*/

      /*  docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        numOfMembers = document.getString("numberOfMembers");
                        proceedFurther();
                        finish();
                    } else {
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });*/



    }
    public void room_join_function(String user_joincode)
    {
        DocumentReference docRef = db.collection("keyValues").document(user_joincode);
                       /* docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if(documentSnapshot.exists())
                                {
                                    roomID = documentSnapshot.getString("roomId");

                                }
                            }
                        });*/



        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        roomID = document.getString("roomId");
                        numOfMembers = document.getString("numOfMembers");
                        owner = document.getString("owner");
                        joinkey = document.getString("joinKey");
                        enterRoom(roomID,joinkey);
                    } else {
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }
    public void proceedFurther()
    {
        DocumentReference docRef = db.collection("keyValues").document(user_joincode);

        numofmem = (Integer.parseInt(numOfMembers));
        numofmem=numofmem+1;

        docRef.update("numOfMembers",numofmem+"");
        //joinme();



      //  members.put(numofmem+"",userEmail);
        //members.put("numberOfMembers",numofmem+"");

      /* DocumentReference docRef = db.collection(roomID).document("Members");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                       members =document.getData();
                      //  Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                       // Log.d(TAG, "No such document");
                    }
                } else {
                  //  Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        members.put(numofmem+"",userEmail);
        members.put("numberOfMembers",numofmem+"");

        db.collection(roomID).document("Members")
                .set(members)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        joinme();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error adding document", e);
                    }
                });*/
    }

    public void joinme()
    {

        Boolean result = myDB.insertData(userEmail,"10000000","10000000","true","0","0","0","0","0","1",joinkey);

        if(result)
        {
            Intent roomCreated = new Intent(AfterRegistrationMainActivity.this,WaitingForPlayersActivity.class);
            //startActivity(roomCreated);
            finish();
        }

    }

    public void qr_reader(View view) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 123);
        }
        else {
            Intent qr_scanner = new Intent(AfterRegistrationMainActivity.this,qrcode_scanner.class);
            startActivity(qr_scanner);
            finish();

        }


    }

 /*   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 123) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent qr_scanner = new Intent(AfterRegistrationMainActivity.this,qrcode_scanner.class);
                startActivity(qr_scanner);
                finish();

                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();

            }

        }}//end onRequestPermissionsResult*/

    public void storagepermission()
    {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
        }
         else if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
        {
            Handler handlerCards = new Handler();
            Runnable runnableCards = new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(AfterRegistrationMainActivity.this,PowerCards.class));
                    finish();
                }
            };
            handlerCards.postDelayed(runnableCards,250);

        }
        else {
            Handler handlerCards = new Handler();
            Runnable runnableCards = new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(AfterRegistrationMainActivity.this,PowerCards.class));
                    finish();
                }
            };
            handlerCards.postDelayed(runnableCards,250);


        }
    }
    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
