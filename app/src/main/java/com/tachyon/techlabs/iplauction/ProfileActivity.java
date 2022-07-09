package com.tachyon.techlabs.iplauction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Objects;

import javax.annotation.Nullable;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private FirebaseAuth mAuth;
    private String username,userphoto;
    private TextView txtUsername;
    private ImageView imgUserphoto;
    private ListView listView;
    private ListView teams;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;
    private ProgressBar progressBar_indeterminate_circle;
    private int animation_type = ItemAnimation.FADE_IN;
    int [] val = new int[3];
    String [] text = {"Initial Balance","Current Balance","Power Cards"};
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> rooms=new ArrayList<String>();
    SharedPreferences sp;
    Spinner spin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        progressBar_indeterminate_circle = findViewById(R.id.profile_progress);

        listView = findViewById(R.id.profile_listview);
        //teams=findViewById(R.id.team_listview);

        txtUsername = findViewById(R.id.profile_username);
        imgUserphoto = findViewById(R.id.profile_image);

        username = currentUser.getDisplayName();
        userphoto = currentUser.getPhotoUrl().toString();

        DocumentReference documentReference = db.collection("Players").document(Objects.requireNonNull(currentUser.getEmail()));
        /*documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                val[0] = Objects.requireNonNull(documentSnapshot.getLong("Current_Amount")).intValue();
                val[1] = Objects.requireNonNull(documentSnapshot.getLong("Initial_Amount")).intValue();
                val[2] = Objects.requireNonNull(documentSnapshot.getLong("numberOfCards")).intValue();
                setData();
            }
        });*/
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                val[0] = Objects.requireNonNull(documentSnapshot.getLong("Current_Amount")).intValue();
                val[1] = Objects.requireNonNull(documentSnapshot.getLong("Initial_Amount")).intValue();
                val[2] = Objects.requireNonNull(documentSnapshot.getLong("numberOfCards")).intValue();
                setData();
            }
        });



        //int [] value = {10000000,10000000,0};


        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(setPhoto())
                {
                    txtUsername.setText(username);
                    progressBar_indeterminate_circle.setVisibility(View.GONE);
                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein);
                    txtUsername.setAnimation(anim);
                    imgUserphoto.setAnimation(anim);
                }
                else
                {
                    progressBar_indeterminate_circle.setIndeterminate(true);
                    handler.postDelayed(this,1000);
                }
            }
        };
        handler.post(runnable);


        /*
        sp=getSharedPreferences("joined_rooms",MODE_PRIVATE);
        String joined_room_ids= sp.getString("joined_room",null);
        //String array=("{"+joined_room_ids+"}").toString();
        String[] arr=joined_room_ids.split(",");
        ArrayAdapter adapter=new ArrayAdapter<String>(this,R.layout.profile_myteams,arr);
        teams.setAdapter(adapter);
        */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);

        //String name = getIntent().getExtras().getString("name");
        navigationView.setNavigationItemSelectedListener(this);

        /*
        teams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selecteditem=(String) adapterView.getItemAtPosition(i);
                Toast.makeText(getApplicationContext(),selecteditem,Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, selecteditem, Toast.LENGTH_SHORT).show();

            }
        });


        spin=findViewById(R.id.spin);
        ArrayAdapter spin_adapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,arr);
        spin_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spin.setAdapter(spin_adapter);

        */


    }

    public void setData()
    {
        ProfileListViewAdapter profileListViewAdapter = new ProfileListViewAdapter(getApplicationContext(),text,val);
        listView.setAdapter(profileListViewAdapter);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fadein_slidedown);
        listView.startAnimation(animation);
    }


    public boolean setPhoto()
    {
        Glide.with(ProfileActivity.this).load(userphoto).apply(RequestOptions.circleCropTransform()).into(imgUserphoto);
        return true;
    }



    public void profileback(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(this.mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            startActivity(new Intent(ProfileActivity.this,AfterRegistrationMainActivity.class));
            finish();
        }

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
                startActivity(new Intent(ProfileActivity.this,AfterRegistrationMainActivity.class));
                break;

            case R.id.nav_profile:
                break;

            case R.id.nav_opponents:
                break;

            case R.id.nav_cards:
                startActivity(new Intent(ProfileActivity.this,PowerCards.class));
                finish();
                break;

            case R.id.nav_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                startActivity(Intent.createChooser(share,"Share Via"));

            case R.id.nav_about_us:
                startActivity(new Intent(ProfileActivity.this,About.class));
                finish();
                break;

            case R.id.nav_developer:
                break;

            case R.id.nav_about_app:
                break;

            case R.id.nav_logout:
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;


    }


}
