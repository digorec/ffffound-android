package com.digorec.ffffound;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

public class WebViewActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);

        String url = "";
        Bundle extras = getIntent().getExtras();
        if(extras !=null){
            url = extras.getString("url");
        }

        TextView textHeader = (TextView) this.findViewById(R.id.textView);
        textHeader.setText(url);

        WebView webView = (WebView) this.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
