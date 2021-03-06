package com.madcomp19gmail.bouncyball;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;

public class BoostShop extends AppCompatActivity implements BillingProcessor.IBillingHandler{

    private final int background_music_id = R.raw.background_music_1;

    private AdView mAdView;
    StorageManager storageManager;
    TextView coins;
    TextView gems;

    private MediaPlayerManager mediaPlayerManager;

    int number = 1;

    public int time;
    public int seconds;
    public int updatedTime;
    public int updatedSeconds;
    public int price;

    BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boost_shop);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        storageManager = StorageManager.getInstance();
        mediaPlayerManager = MediaPlayerManager.getInstance();
        coins = findViewById(R.id.coins);
        gems = findViewById(R.id.gems);

        coins.setText(storageManager.getTotalBounces() + "");
        gems.setText(storageManager.getTotalGems() + "");

        if(!storageManager.getNoAds()) {
            mAdView = findViewById(R.id.bannerAdBoostShop);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        initializeImageButtons(findViewById(R.id.boostShopLinearLayout));

        updateLabel();

        bp = new BillingProcessor(this, MainMenu.KEY, this);
        bp.initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        coins.setText(storageManager.getTotalBounces() + "");
        gems.setText(storageManager.getTotalGems() + "");

        if(storageManager.getShopMusicSetting())
        {
            mediaPlayerManager.loadSound(background_music_id, "Shop");
            mediaPlayerManager.play();
        }
    }

    @Override
    protected void onDestroy() {

        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(!this.isFinishing() && storageManager.getShopMusicSetting())
            mediaPlayerManager.pause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initializeImageButtons(View view) {
        ViewGroup viewGroup = (ViewGroup) view;

        int childCount = viewGroup.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View v = viewGroup.getChildAt(i);

            if (v instanceof ImageButton) {

                if(v.getId() == R.id.play_icon_vector)
                    continue;

                ImageButton imageButton = (ImageButton) v;

                int image_id = getResources().getIdentifier("boosts_" + number, "drawable", getPackageName());

                number++;

                Glide.with(this).load(image_id).into(imageButton);
            } else if (v instanceof ViewGroup)
                initializeImageButtons(v);
        }
    }

    public void onClickPlay(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", true);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void onClickBoost(View view) {
        String view_id = view.getResources().getResourceEntryName(view.getId());
        view_id = view_id.split("_Label")[0].split("_Button")[0];

        String label = view_id + "_Label";
        int id = getResources().getIdentifier(label, "id", getApplicationContext().getPackageName());
        int label_id = this.getResources().getIdentifier(label, "id", getPackageName());

        //If there is a boost already active, nothing happens
        if (Calendar.getInstance().getTimeInMillis() < storageManager.getActiveBoostTime()) {
            Toast.makeText(this, "Wait for the active boost to end", Toast.LENGTH_SHORT).show();
            return;
        }

        int boost = parseInt(view_id.split("_")[0].replace("x", ""));
        time = parseInt(view_id.split("_")[1].replace("m", ""));
        seconds = 0;

        final TextView label_text = findViewById(id);
        if (label_text.getText() == "")
            return;
        price = Integer.parseInt(label_text.getText() + "");
        int total_gems = storageManager.getTotalGems();
        if (total_gems >= price) {
            storageManager.takeGems(price);
            gems.setText(storageManager.getTotalGems() + "");
            storageManager.setActiveBoost(boost);
            storageManager.setActiveBoostLabel(label_id);
            storageManager.setActiveBoostTime(Calendar.getInstance().getTimeInMillis() + time * 60000);
            Toast.makeText(this, "Boost is now active", Toast.LENGTH_SHORT).show();
            label_text.setCompoundDrawables(null, null, null, null);
            label_text.setPadding(0, 0, 0, 0);

            SoundPoolManager.getInstance().playSound();

            Timer t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {


                            if (time < 10 && seconds < 10)
                                label_text.setText("0" + String.valueOf(time) + " : " + "0" + String.valueOf(seconds));
                            if (time >= 10 && seconds >= 10)
                                label_text.setText(String.valueOf(time) + " : " + String.valueOf(seconds));
                            if (time >= 10 && seconds < 10)
                                label_text.setText(String.valueOf(time) + " : " + "0" + String.valueOf(seconds));
                            if (time < 10 && seconds >= 10)
                                label_text.setText("0" + String.valueOf(time) + " : " + String.valueOf(seconds));

                            seconds -= 1;

                            if (seconds < 0) {
                                seconds = 59;
                                time = time - 1;
                            }

                            if (time < 0) {
                                label_text.setText(price + "");
                                label_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gem_icon_small, 0);
                                label_text.setPadding(110, 0, 110, 0);
                                cancel();
                            }
                        }
                    });
                }

            }, 0, 1000);
        } else
        {
            BuyGemsDialog dialog = new BuyGemsDialog(this);
            dialog.Show();
        }
            //Toast.makeText(this, "You need " + (price - total_gems) + " more Gems!", Toast.LENGTH_LONG).show();
    }

    public void updateLabel() {
        if (Calendar.getInstance().getTimeInMillis() > storageManager.getActiveBoostTime())
            return;

        final TextView activeLabel = ((TextView) findViewById(storageManager.getActiveBoostLabel()));
        activeLabel.setCompoundDrawables(null, null, null, null);
        activeLabel.setPadding(0, 0, 0, 0);
        long milliseconds = storageManager.getActiveBoostTime() - Calendar.getInstance().getTimeInMillis();
        final String text = activeLabel.getText().toString();

        updatedSeconds = (int) (milliseconds / 1000) % 60;
        updatedTime = (int) ((milliseconds / (1000 * 60)) % 60);

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if (updatedTime < 10 && updatedSeconds < 10)
                            activeLabel.setText("0" + String.valueOf(updatedTime) + " : " + "0" + String.valueOf(updatedSeconds));
                        if (updatedTime >= 10 && updatedSeconds >= 10)
                            activeLabel.setText(String.valueOf(updatedTime) + " : " + String.valueOf(updatedSeconds));
                        if (updatedTime >= 10 && updatedSeconds < 10)
                            activeLabel.setText(String.valueOf(updatedTime) + " : " + "0" + String.valueOf(updatedSeconds));
                        if (updatedTime < 10 && updatedSeconds >= 10)
                            activeLabel.setText("0" + String.valueOf(updatedTime) + " : " + String.valueOf(updatedSeconds));

                        updatedSeconds -= 1;

                        if (updatedSeconds < 0) {
                            updatedSeconds = 59;
                            updatedTime = updatedTime - 1;
                        }

                        if (updatedTime < 0) {
                            activeLabel.setText(text);
                            activeLabel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.gem_icon_small, 0);
                            activeLabel.setPadding(110, 0, 110, 0);
                            cancel();
                        }
                    }
                });
            }

        }, 0, 1000);
    }

    public void buyCoins(View view)
    {
        BuyCoinsDialog dialog = new BuyCoinsDialog(this);
        dialog.Show();
    }

    public void buyGems(View view)
    {
        BuyGemsDialog dialog = new BuyGemsDialog(this);
        dialog.Show();
    }

    // In app purchases
    // region

    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

        if(productId.equals("gem_pack_1"))
            storageManager.addGems(50);

        if(productId.equals("gem_pack_2"))
            storageManager.addGems(200);

        if(productId.equals("gem_pack_3"))
            storageManager.addGems(500);

        Toast.makeText(this, "Purchase Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(this, "Something went wrong with the purchase", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }
    // endregion
}
