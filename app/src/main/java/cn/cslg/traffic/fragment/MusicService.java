package cn.cslg.traffic.fragment;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;
import cn.cslg.traffic.R;

/**
 * Created by user on 2019/6/13.
 */
public class MusicService extends Service {
    private MediaPlayer mp;
    private IBinder binder=new MyBinder();

    public class MyBinder extends Binder {
        public MusicService getService(){
            return MusicService.this;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        play();
        return super.onStartCommand(intent, flags, startId);
    }

    public void play(){
        mp = MediaPlayer.create(this, R.raw.backmusic);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(getApplicationContext(),"播放完毕",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void pause(){
        mp.pause();
    }
    public void start(){
        mp.start();
    }
    public boolean isPlay(){
        return mp.isPlaying();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mp.isPlaying())mp.stop();
        mp.release();
    }


}

