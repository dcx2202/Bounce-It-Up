package com.madcomp19gmail.bouncyball;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ShopMenu extends AppCompatActivity {

    private final int background_music_id = R.raw.background_music_1;

    private boolean changingActivity;

    private AdView mAdView;

    private View view;

    private StorageManager storage;
    private MediaPlayerManager mediaPlayerManager;
    TextView coins;
    TextView gems;

    Dialog coinDialog;
    Dialog gemDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_menu);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        changingActivity = false;

        storage = StorageManager.getInstance();
        mediaPlayerManager = MediaPlayerManager.getInstance();
        coins = findViewById(R.id.coins);
        gems = findViewById(R.id.gems);

        if (storage.getShopMusicSetting()) {
            mediaPlayerManager.pause();
            mediaPlayerManager.loadSound(background_music_id, "Shop");
            mediaPlayerManager.play();
        }
        else
            mediaPlayerManager.pause();

        SoundPoolManager.getInstance().loadSound(R.raw.cash);

        if(!storage.getNoAds()) {
            mAdView = findViewById(R.id.bannerAdTrailShop);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        coinDialog = new Dialog(this);
        gemDialog = new Dialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        coins.setText(storage.getTotalBounces() + "");
        gems.setText(storage.getTotalGems() + "");

        if (storage.getShopMusicSetting()) {
            mediaPlayerManager.pause();
            mediaPlayerManager.loadSound(background_music_id, "Shop");
            mediaPlayerManager.play();
        }
        else
            mediaPlayerManager.pause();

        SoundPoolManager.getInstance().loadSound(R.raw.cash);

        if(view != null)
            view.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!this.isFinishing() && !changingActivity && storage.getShopMusicSetting())
            mediaPlayerManager.pause();

        changingActivity = false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        //if(storage.getMenuMusicSetting())
        //mediaPlayerManager.stop();
    }

    public void onShopMenuButtonClick(View view) {

        view.setEnabled(false);
        this.view = view;

        if (view.getId() == R.id.ballsButton) {
            changingActivity = true;
            Intent intent = new Intent(this, BallShop.class);
            startActivityForResult(intent, 1);
        }

        if (view.getId() == R.id.skinsButton) {
            changingActivity = true;
            Intent intent = new Intent(this, SkinShop.class);
            startActivityForResult(intent, 1);
        }

        if (view.getId() == R.id.trailsButton) {
            changingActivity = true;
            Intent intent = new Intent(this, TrailShop.class);
            startActivityForResult(intent, 1);
        }

        if (view.getId() == R.id.soundButton) {
            changingActivity = true;
            Intent intent = new Intent(this, SoundShop.class);
            startActivityForResult(intent, 1);
        }

        if (view.getId() == R.id.boostButton) {
            changingActivity = true;
            Intent intent = new Intent(this, BoostShop.class);
            startActivityForResult(intent, 1);
        }

        if (view.getId() == R.id.backgroundsButton) {
            changingActivity = true;
            Intent intent = new Intent(this, BackgroundShop.class);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("result", false);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", result);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
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
}
