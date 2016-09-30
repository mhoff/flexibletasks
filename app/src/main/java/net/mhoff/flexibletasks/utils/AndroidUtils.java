package net.mhoff.flexibletasks.utils;

import android.content.Intent;
import android.view.ViewGroup;

import java.io.Serializable;

public class AndroidUtils {

    public static Intent createResultIntent(String resultKey, Serializable result) {
        Intent container = new Intent();
        container.putExtra(resultKey, result);
        return container;
    }

    public static ViewGroup getEmptyViewGroup() {
        return null;
    }
}
