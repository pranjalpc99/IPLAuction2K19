package com.tachyon.techlabs.iplauction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PowerCards extends AppCompatActivity {
    public static final String CHANNEL_ID="1001";
    Context context;
    ImageView cardback;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    public RelativeLayout bottonSheetLayout;
    public BottomSheetBehavior bottomSheetBehavior;
    View bgView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView txt_bs_name,txt_bs_desc,txt_bs_value;
    DocumentReference mainDoc;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String userEmail;
    int currentAmount,currentNumOfCards,itemsPurchased;
    AppConstants appConstants = new AppConstants();
    int card_amount=0;
    Button buyBtn;
    int flag=0;
    String history;
    Map<String, Object> payHistory = new HashMap<>();
    Pinview pin;
    TextView pinText;
    PaymentInfo paymentInfo = new PaymentInfo();
    ViewGroup.LayoutParams params;
    String num="";
    StringBuilder stringBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_recyclerview);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        int[] imgs = {R.drawable.ipl_logo,
                R.drawable.ipl_logo,
                R.drawable.ipl_logo,
                R.drawable.ipl_logo};

        String[] names = {"YORKER",
                "NO BALL",
                "RIGHT TO MATCH",
                "LEGEND CARDS"};

        String[] disc = {"DESCRIPTION",
                "DESCRIPTION",
                "DESCRIPTION",
                "DESCRIPTION"};

        String[] price = {"PRICE",
                "PRICE",
                "PRICE",
                "PRICE"};

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userEmail = Objects.requireNonNull(currentUser).getEmail();

        context = getApplicationContext();

        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        buyBtn = findViewById(R.id.card_pay_bs);
        pin = (Pinview) findViewById(R.id.pinView);
        pinText = findViewById(R.id.pinText);

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        bottonSheetLayout = findViewById(R.id.botton_sheet_layout_id);
        bottomSheetBehavior = BottomSheetBehavior.from(bottonSheetLayout);
        params = bottonSheetLayout.getLayoutParams();

        txt_bs_name = findViewById(R.id.card_name_bs);
        txt_bs_desc = findViewById(R.id.card_desc_bs);
        txt_bs_value = findViewById(R.id.card_pricevalue_bs);

        //num = paymentInfo.cardnum4.getText().toString();
        //num = paymentInfo.stringBuilder.substring(12,16);
        readCardNum();

        adapter = new cards_adapter(context,imgs,disc,names,price,bottonSheetLayout,bottomSheetBehavior,txt_bs_name,txt_bs_desc,txt_bs_value);

        recyclerView.setAdapter(adapter);

        mainDoc = db.collection("Players").document(userEmail);
        mainDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentAmount = Objects.requireNonNull(documentSnapshot.getLong("Current_Amount")).intValue();
                currentNumOfCards = Objects.requireNonNull(documentSnapshot.getLong("numberOfCards")).intValue();
                itemsPurchased = Objects.requireNonNull(documentSnapshot.getLong("itemsPurchased")).intValue();
                itemsPurchased = itemsPurchased +1;
                Toast.makeText(context, itemsPurchased+"", Toast.LENGTH_SHORT).show();
            }
        });



        bgView = findViewById(R.id.bgVisible);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_COLLAPSED)
                {
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    bgView.setVisibility(View.GONE);
                    pinText.setVisibility(View.GONE);
                    getWindow().setStatusBarColor(ContextCompat.getColor(bottomSheet.getContext(), R.color.white));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                bgView.setVisibility(View.VISIBLE);
                bgView.setAlpha(slideOffset);
            }
        });

        bgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    pin.setVisibility(View.GONE);
                    pinText.setVisibility(View.GONE);
                    getWindow().setStatusBarColor(ContextCompat.getColor(v.getContext(), R.color.white));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }

            }
        });


        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyCard();
            }
        });

        /*
        gv= (GridView) findViewById(R.id.gv);

        adapter=new cards_adapter(this,getData());
        gv.setAdapter(adapter);

        */

    }

    public void readCardNum()
    {
        File sdCard = Environment.getExternalStorageDirectory();
        File file = new File(sdCard,"CardNum.txt");
        stringBuilder = new StringBuilder();

        try
        {
            BufferedReader br =  new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

       num = stringBuilder.substring(12,16);
    }

    public void buyCard()
    {
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        bottonSheetLayout.setLayoutParams(params);
        pin.setVisibility(View.VISIBLE);
        pinText.setVisibility(View.VISIBLE);
        String cardName = txt_bs_name.getText().toString();
        switch (cardName)
        {
            case "YORKER": card_amount = appConstants.Yorker_price ;
                flag=1;
                break;
            case "NO BALL" : card_amount = appConstants.noBall_price;
                flag=2;
                break;
            case "RIGHT TO MATCH": card_amount = appConstants.rightToMatch_price;
                flag=3;
                break;
            case "LEGEND CARDS": card_amount = appConstants.legendCards;
                flag=4;
                break;
        }

        //payHistory.put("0",0);
        //documentReference2.set(payHistory);
        pin.setPinViewEventListener(new Pinview.PinViewEventListener()
        {
            @Override
            public void onDataEntered(Pinview pinview, boolean b)
            {
                DocumentReference documentReference2 = db.collection("Players").document(userEmail).collection("paymentHistory").document(itemsPurchased+"");
                if(pinview.getValue().equals(num))
                {
                    switch (flag) {
                        case 1:
                            //history = "Yorker#" + appConstants.Yorker_price;
                            payHistory.put("0","Yorker");
                            payHistory.put("1",appConstants.Yorker_price);
                            documentReference2.set(payHistory);
                            break;
                        case 2:
                            payHistory.put("0","No Ball");
                            payHistory.put("1",appConstants.noBall_price);
                            documentReference2.set(payHistory);
                            break;
                        case 3:
                            payHistory.put("0","Right To Match");
                            payHistory.put("1",appConstants.rightToMatch_price);
                            documentReference2.set(payHistory);
                            break;
                        case 4:
                            payHistory.put("0","Legend Cards");
                            payHistory.put("1",appConstants.legendCards);
                            documentReference2.set(payHistory);
                            break;
                    }

                    int current_amount = currentAmount - card_amount;
                    DocumentReference documentReference = db.collection("Players").document(userEmail);
                    documentReference.update("Current_Amount",current_amount);
                    documentReference.update("numberOfCards",currentNumOfCards+1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                    pin.setVisibility(View.GONE);
                                    pinText.setVisibility(View.GONE);

                                    //Toast.makeText(context, "Bought", Toast.LENGTH_SHORT).show();
                                }
                            });

                    mainDoc.update("itemsPurchased",itemsPurchased).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startActivity(new Intent(PowerCards.this,PaymentInfo.class));
                            finish();
                        }
                    });


                }
            }

        });
    }

    public void cardback(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
        {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            pin.setVisibility(View.GONE);
            pinText.setVisibility(View.GONE);
        }
        else
        {
            super.onBackPressed();
            final Intent cardtomain = new Intent(PowerCards.this,AfterRegistrationMainActivity.class);
            startActivity(cardtomain);
            finish();
        }

    }

    /*
    private ArrayList getData()
    {
        ArrayList<Power_Cards> Power_Cardss=new ArrayList<>();

        Power_Cards s=new Power_Cards();
        s.setName("Yorker");
        s.setDescription("Description");
        s.setImage(R.drawable.ipl_logo);
        s.setPrice(2000);
        Power_Cardss.add(s);

        s=new Power_Cards();
        s.setName("No Ball");
        s.setDescription("Description");
        s.setImage(R.drawable.ipl_logo);
        s.setPrice(2000);
        Power_Cardss.add(s);

        s=new Power_Cards();
        s.setName("Right To Match");
        s.setDescription("Description");
        s.setImage(R.drawable.ipl_logo);
        s.setPrice(200);
        Power_Cardss.add(s);

        s=new Power_Cards();
        s.setName("Legend Cards");
        s.setDescription("Description");
        s.setImage(R.drawable.ipl_logo);
        s.setPrice(200);
        Power_Cardss.add(s);

        return Power_Cardss;
    }

    */

    /*
    NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(c, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Card Purchase")
            .setContentText("You purschased"+"card")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    public void buy_card(View view) {
       // notificationManager.notify(12345, mBuilder.build());
        Log.d("LOGMessage","Buy card");
        Toast.makeText(this, "buy_card_fn_call", Toast.LENGTH_SHORT).show();



    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name =("Cards");
            String description = "heyyy";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
           // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(PowerCards.this);
            notificationManager.createNotificationChannel(channel);
        }
    }

    */



}