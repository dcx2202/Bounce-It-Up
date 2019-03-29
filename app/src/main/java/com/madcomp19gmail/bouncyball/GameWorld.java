package com.madcomp19gmail.bouncyball;

import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.seismic.ShakeDetector;

import java.util.Calendar;


public class GameWorld extends AppCompatActivity implements ShakeDetector.Listener {

    static GameView gameView;
    //public static MediaPlayer mediaPlayer;
    /*SoundPool soundPool;
    HashMap<Integer, Integer> soundPoolMap;
    int soundID = 1;*/

    private static final int background_music_id = R.raw.background_music_2;

    private static StorageManager storageManager;
    private static MediaPlayerManager mediaPlayerManager;
    private static int touches;
    private static int total_bounces_ever;
    private static int gems;

    ImageView game_background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_world);

        StorageManager.initialize(this);
        storageManager = StorageManager.getInstance();

        // Get the game background image view
        game_background = findViewById(R.id.game_background);
        gameView = findViewById(R.id.game_view);

        // Load the background into that view
        int selected_background = storageManager.getSelectedBackground();
        //selected_background = R.drawable.background_40;


        //gameView.setBackgroundResource(selected_background);

        Glide.with(this).load(selected_background).centerCrop().into(game_background);

        // Make it fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set up the shake detector
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);
        sd.setSensitivity(11);


        //StorageManager.initialize(this);
        //storageManager = StorageManager.getInstance();
        touches = storageManager.getTotalBounces();
        total_bounces_ever = storageManager.getTotalBouncesEver();
        gems  = storageManager.getTotalGems();

        // Set up game world music
        if(storageManager.getGameMusicSetting() != 0)
        {
            mediaPlayerManager.pause();
            mediaPlayerManager.changeVolume((float) storageManager.getGameMusicSetting() / 10);
            mediaPlayerManager.loadSound(background_music_id, "Game");
            mediaPlayerManager.play();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //int selected_background = storageManager.getSelectedBackground();
        int selected_background = R.drawable.background_22;

        //gameView.setBackgroundResource(selected_background);

        Glide.with(this).load(selected_background).centerCrop().into(game_background);

        if(storageManager.getGameMusicSetting() != 0)
        {
            mediaPlayerManager.pause();
            mediaPlayerManager.changeVolume((float) storageManager.getGameMusicSetting() / 10);
            mediaPlayerManager.loadSound(background_music_id, "Game");
            mediaPlayerManager.play();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        storageManager.setTotalBounces(touches);
        storageManager.setTotalBouncesEver(total_bounces_ever);
        storageManager.setTotalGems(gems);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        storageManager.setTotalBounces(touches);
        storageManager.setTotalBouncesEver(total_bounces_ever);
        storageManager.setTotalGems(gems);


        MainMenu.prev_act_GameWorld = true;


        if(!this.isFinishing() && storageManager.getGameMusicSetting() != 0)
            mediaPlayerManager.pause();
        else
            mediaPlayerManager.changeVolume(1);

        //SoundPoolManager.getInstance().loadSound(R.raw.cash);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        SoundPoolManager.getInstance().release();
        storageManager.setTotalBounces(touches);
        storageManager.setTotalBouncesEver(total_bounces_ever);
        storageManager.setTotalGems(gems);
    }

    public static void addTouches(int t)
    {
        touches += t;
        total_bounces_ever++;
    }

    public static void addGems(int g)
    {
        gems += g;
    }

    public static void addDrop(Vector2 position)
    {
        int boost;
        int value = 1;

        if(Calendar.getInstance().getTimeInMillis() < storageManager.getActiveBoostTime())
            boost = storageManager.getActiveBoost();
        else
        {
            boost = 1;
            storageManager.setActiveBoost(boost);
        }

        switch (boost)
        {
            case 1: boost = 1; value = 1; break;
            case 2: boost = 1; value = 2; break;
            case 5: boost = 1; value = 5; break;
            case 10: boost = 2; value = 5; break;
            case 50: boost = 2; value = 25; break;
            default: break;
        }

        for(int i = 0; i < boost; i++)
        {
            if(Math.random() * 100 < 1)
                gameView.spawnGem(position, 1);
            else
                gameView.spawnCoin(position, value);
        }
//        if(Math.random() * 100 < 1)
//            gameView.addGems(position);
//        else
//            gameView.spawnCoin(position);
    }

    public static int getTouches()
    {
        return touches;
    }

    public static int getGems()
    {
        return gems;
    }

    @Override public void hearShake()
    {
        gameView.shake();
    }
}

