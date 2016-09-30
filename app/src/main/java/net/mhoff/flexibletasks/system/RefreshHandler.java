package net.mhoff.flexibletasks.system;

import android.os.Handler;

public abstract class RefreshHandler {

    private static final int INITIAL_UPDATE_DELAY = 100;
    private static final int UPDATE_RATE = 1000;

    private final Handler handler = new Handler();
    private final Runnable runner = new Runnable() {
        public void run() {
            refresh();
            handler.postDelayed(runner, UPDATE_RATE);
        }
    };

    public void resume() {
        handler.removeCallbacks(runner);
        handler.postDelayed(runner, INITIAL_UPDATE_DELAY);
    }

    public void pause() {
        handler.removeCallbacks(runner);
    }

    protected abstract void refresh();

}
