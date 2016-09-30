package net.mhoff.flexibletasks.system;

import android.support.v7.app.AppCompatActivity;

public abstract class RefreshingAppCompatActivity extends AppCompatActivity {

    private final RefreshHandler refreshHandler = new RefreshHandler() {
        @Override
        protected void refresh() {
            onRefresh();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        refreshHandler.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        refreshHandler.pause();
    }

    protected abstract void onRefresh();

}
