package com.digorec.ffffound;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.firezenk.progressbar.FZProgressBar;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MoreActivity extends ActionBarActivity {

    private IndexListArrayAdapter indexListAdapter;
    Activity activity;
    private FZProgressBar progressBar;
    public String activity_title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);

        activity = this;

        progressBar = (FZProgressBar) findViewById(R.id.myProgressBar);
        progressBar.animation_config(2, 20);
        progressBar.animation_start(FZProgressBar.Mode.INDETERMINATE);

        List<String> test_list = new ArrayList<String>();
        indexListAdapter = new IndexListArrayAdapter(this, test_list);

        final ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(indexListAdapter);

        String url = "";
        Bundle extras = getIntent().getExtras();
        if(extras !=null){ url = extras.getString("url"); }

        new ParseSite().execute(url);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.animation_start(FZProgressBar.Mode.INDETERMINATE);

        activity.setTitle("...");
    }

    private class ParseSite extends AsyncTask<String, Void, List<String>> {
        protected List<String> doInBackground(String... arg) {
            List<String> output = new ArrayList<String>();
            try {
                HtmlCleaner cleaner = new HtmlCleaner();
                TagNode node = cleaner.clean(new URL(arg[0]));

                Object[] title_atags = node.evaluateXPath("//div[@class='title']/a");
                for (Object tag : title_atags) {
                    TagNode aTag = (TagNode) tag;
                    String content = aTag.getText().toString().trim();
                    activity_title = content;
                }

                Object[] tags = node.evaluateXPath("//div[@class='related_to_item']/table/tbody/tr/td/a/img");
                for (Object tag : tags) {
                    TagNode aTag = (TagNode) tag;
                    String href = aTag.getAttributeByName("src").trim();
                    href = href.replace("_s.", "_m.");
                    output.add(href);
                    indexListAdapter.names.add("You may like these image");
                    indexListAdapter.names_urls.add(href);
                    indexListAdapter.img_h.add(300);
                    indexListAdapter.img_w.add(300);
                }

                Object[] atags = node.evaluateXPath("//div[@class='related_to_item']/table/tbody/tr/td/a");
                for (Object tag : atags) {
                    TagNode aTag = (TagNode) tag;
                    String href = aTag.getAttributeByName("href").trim();
                    indexListAdapter.more_urls.add("http://ffffound.com"+href);
                }

                Object[] liked_tags = node.evaluateXPath("//div[@class='more_images_item']/table/tbody/tr/td/a/img");
                for (Object tag : liked_tags) {
                    TagNode aTag = (TagNode) tag;
                    String href = aTag.getAttributeByName("src").trim();
                    href = href.replace("_s.", "_m.");
                    output.add(href);

                    indexListAdapter.names.add("More image");
                    indexListAdapter.names_urls.add(href);
                    indexListAdapter.img_h.add(300);
                    indexListAdapter.img_w.add(300);
                }

                Object[] liked_atags = node.evaluateXPath("//div[@class='more_images_item']/table/tbody/tr/td/a");
                for (Object tag : liked_atags) {
                    TagNode aTag = (TagNode) tag;
                    String href = aTag.getAttributeByName("href").trim();
                    indexListAdapter.more_urls.add(href);
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return output;
        }

        protected void onPostExecute(List<String> output) {
            ListView listview = (ListView) findViewById(R.id.listView);
            indexListAdapter.addAll(output);
            activity.setTitle(activity_title);
            progressBar.setVisibility(View.GONE);
            progressBar.animation_stop();
        }
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
