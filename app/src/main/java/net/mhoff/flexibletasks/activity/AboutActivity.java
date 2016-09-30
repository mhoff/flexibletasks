package net.mhoff.flexibletasks.activity;

import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import net.mhoff.flexibletasks.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(getString(R.string.about_text), Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(getString(R.string.about_text));
        }

        ((TextView) findViewById(R.id.about_text_view)).setText(result);
    }
}
