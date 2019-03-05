package com.madcomp19gmail.bouncyball;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TrailShop extends AppCompatActivity {

    private StorageManager storage;
    TextView coins;
    TextView gems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail_shop);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setOwnedTrailsPage(this.findViewById(android.R.id.content));

        storage = StorageManager.getInstance();
        coins = findViewById(R.id.coins);
        gems = findViewById(R.id.gems);
    }

    @Override
    protected void onResume(){
        super.onResume();

        coins.setText(storage.getTotalTouches() + "");
        gems.setText(storage.getTotalGems() + "");
    }

    public void onClickTrail(View view)
    {
        switch (view.getId()) {
            case R.id.trail_1_Button:
            case R.id.trail_1_Label:
                buyOrSetTrail(R.integer.trail_1, R.id.trail_1_Label);
                break;

            case R.id.trail_2_Button:
            case R.id.trail_2_Label:
                buyOrSetTrail(R.integer.trail_2, R.id.trail_2_Label);
                break;

            case R.id.trail_3_Button:
            case R.id.trail_3_Label:
                buyOrSetTrail(R.integer.trail_3, R.id.trail_3_Label);
                break;

            case R.id.trail_4_Button:
            case R.id.trail_4_Label:
                buyOrSetTrail(R.integer.trail_4, R.id.trail_4_Label);
                break;

            case R.id.trail_5_Button:
            case R.id.trail_5_Label:
                buyOrSetTrail(R.integer.trail_5, R.id.trail_5_Label);
                break;

            case R.id.trail_6_Button:
            case R.id.trail_6_Label:
                buyOrSetTrail(R.integer.trail_6, R.id.trail_6_Label);
                break;

            case R.id.trail_7_Button:
            case R.id.trail_7_Label:
                buyOrSetTrail(R.integer.trail_7, R.id.trail_7_Label);
                break;

            case R.id.trail_8_Button:
            case R.id.trail_8_Label:
                buyOrSetTrail(R.integer.trail_8, R.id.trail_8_Label);
                break;

            case R.id.trail_9_Button:
            case R.id.trail_9_Label:
                buyOrSetTrail(R.integer.trail_9, R.id.trail_9_Label);
                break;

            case R.id.trail_10_Button:
            case R.id.trail_10_Label:
                buyOrSetTrail(R.integer.trail_10, R.id.trail_10_Label);
                break;

            case R.id.trail_11_Button:
            case R.id.trail_11_Label:
                buyOrSetTrail(R.integer.trail_11, R.id.trail_11_Label);
                break;

            case R.id.trail_12_Button:
            case R.id.trail_12_Label:
                buyOrSetTrail(R.integer.trail_12, R.id.trail_12_Label);
                break;

            case R.id.trail_13_Button:
            case R.id.trail_13_Label:
                buyOrSetTrail(R.integer.trail_13, R.id.trail_13_Label);
                break;

            case R.id.trail_14_Button:
            case R.id.trail_14_Label:
                buyOrSetTrail(R.integer.trail_14, R.id.trail_14_Label);
                break;

            case R.id.trail_15_Button:
            case R.id.trail_15_Label:
                buyOrSetTrail(R.integer.trail_15, R.id.trail_15_Label);
                break;

            case R.id.trail_16_Button:
            case R.id.trail_16_Label:
                buyOrSetTrail(R.integer.trail_16, R.id.trail_16_Label);
                break;

            case R.id.trail_17_Button:
            case R.id.trail_17_Label:
                buyOrSetTrail(R.integer.trail_17, R.id.trail_17_Label);
                break;

            case R.id.trail_18_Button:
            case R.id.trail_18_Label:
                buyOrSetTrail(R.integer.trail_18, R.id.trail_18_Label);
                break;

            case R.id.trail_19_Button:
            case R.id.trail_19_Label:
                buyOrSetTrail(R.integer.trail_19, R.id.trail_19_Label);
                break;

            case R.id.trail_20_Button:
            case R.id.trail_20_Label:
                buyOrSetTrail(R.integer.trail_20, R.id.trail_20_Label);
                break;

            case R.id.trail_21_Button:
            case R.id.trail_21_Label:
                buyOrSetTrail(R.integer.trail_21, R.id.trail_21_Label);
                break;

            case R.id.trail_22_Button:
            case R.id.trail_22_Label:
                buyOrSetTrail(R.integer.trail_22, R.id.trail_22_Label);
                break;

            case R.id.trail_23_Button:
            case R.id.trail_23_Label:
                buyOrSetTrail(R.integer.trail_23, R.id.trail_23_Label);
                break;

            case R.id.trail_24_Button:
            case R.id.trail_24_Label:
                buyOrSetTrail(R.integer.trail_24, R.id.trail_24_Label);
                break;

            case R.id.trail_25_Button:
            case R.id.trail_25_Label:
                buyOrSetTrail(R.integer.trail_25, R.id.trail_25_Label);
                break;
        }
    }

    private void buyOrSetTrail(int aTrail, int label_id)
    {
        int trail = getResources().getInteger(aTrail);

        StorageManager storageManager = StorageManager.getInstance();

        if (storageManager.getOwnedTrails().contains(trail)) {
            storageManager.setSelectedTrail(trail);

            ((TextView) findViewById(storageManager.getSelectedTrailLabel())).setText("Owned");

            storageManager.setSelectedTrailLabel(label_id);

            ((TextView) findViewById(label_id)).setText("Selected");
        } else {
            TextView label_text = (TextView) findViewById(label_id);
            int price = Integer.parseInt(label_text.getText() + "");
            int total_touches = storageManager.getTotalTouches();

            if (total_touches >= price) {
                storageManager.setTotalTouches(total_touches - price);
                storageManager.addOwnedTrail(trail);
                storageManager.setSelectedTrail(trail);
                storageManager.addOwnedTrailLabel(label_id);

                if (storageManager.getSelectedTrailLabel() != 0)
                    ((TextView) findViewById(storageManager.getSelectedTrailLabel())).setText("Owned");

                storageManager.setSelectedTrailLabel(label_id);

                label_text.setText("Selected");
                Toast.makeText(this, "Unlocked", Toast.LENGTH_LONG).show();
                coins.setText(storageManager.getTotalTouches() + "");
            } else
                Toast.makeText(this, "You need " + (price - total_touches) + " more touches", Toast.LENGTH_LONG).show();
        }
    }

    private void setOwnedTrailsPage(View view)
    {
        StorageManager storageManager = StorageManager.getInstance();

        ArrayList<Integer> label_ids = storageManager.getOwnedTrailsLabels();

        ViewGroup viewGroup = (ViewGroup) view;

        int childCount = viewGroup.getChildCount();

        for(int i = 0; i < childCount; i++)
        {
            View v = viewGroup.getChildAt(i);

            if(v instanceof TextView)
            {
                if(label_ids.contains(v.getId()))
                    ((TextView) v).setText("Owned");

                if (storageManager.getSelectedTrailLabel() == v.getId())
                    ((TextView) v).setText("Selected");
            }
            else if(v instanceof ViewGroup)
                setOwnedTrailsPage(v);
        }
    }
}