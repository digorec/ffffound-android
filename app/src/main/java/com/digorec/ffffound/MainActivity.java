package com.digorec.ffffound;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import com.firezenk.progressbar.FZProgressBar;
import com.firezenk.progressbar.FZProgressBar.Mode;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity  {

    private boolean loading_flag = false;
    private FZProgressBar progressBar;
    private IndexListArrayAdapter indexListAdapter;
    private long offset = 0;
    Activity activity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // progressBar = (View) findViewById(R.id.progressBar);
        activity.setTitle("FFFFreader!");


        //***********************
        progressBar = (FZProgressBar) findViewById(R.id.myProgressBar);
        progressBar.animation_config(2, 20);
       /* int[] colors1 = {Color.RED, Color.TRANSPARENT};
        mBar1.bar_config(1, 0, 0, Color.TRANSPARENT, colors1);*/
        progressBar.animation_start(Mode.INDETERMINATE);

        // progressBar.animation_stop();


          //-********************
        // получаем экземпляр элемента ListView
        final ListView listView = (ListView)findViewById(R.id.listView1);

        List<String> test_list = new ArrayList<String>();
        indexListAdapter = new IndexListArrayAdapter(this, test_list);
        listView.setAdapter(indexListAdapter);

        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {  }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (totalItemCount > 0) {
                if((firstVisibleItem + visibleItemCount) ==  totalItemCount) {
                    // Log.i("Info", "Scroll Bottom");
                   // if (pd.isShowing()) {
                    if (!loading_flag) {
                        loadIndexList();
                        // Log.i("Info", "pd.isShowing");
                    }
                }}
            }
        });

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                //   Object o = listView.getItemAtPosition(position);
                   final String text = indexListAdapter.getItem(position).toString();
                   //((TextView) arg1).getText().toString();
                   Log.i("Info", text);
                   /*ImageView imageView = (ImageView) arg1.findViewById(R.id.icon);
                   imageView.setImageResource(R.mipmap.ic_launcher);*/
               }
        });

        loadIndexList();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadIndexList() {
        loading_flag = true;
        //Показываем диалог ожидания
        //pd = ProgressDialog.show(MainActivity.this, "Working...", "request to server", true, false);
        //Запускаем парсинг

        new ParseSite().execute("http://ffffound.com/?offset="+ Long.toString(offset) +"&");
        offset = offset + 25;

        progressBar.setVisibility(View.VISIBLE);
        progressBar.animation_start(Mode.INDETERMINATE);
        // Toast.makeText(activity, "load http...", Toast.LENGTH_LONG).show();
    }

    private class ParseSite extends AsyncTask<String, Void, List<String>> {
        //Фоновая операция
        protected List<String> doInBackground(String... arg) {
            // final
            List<String> output = new ArrayList<String>();
            try {
                HtmlCleaner cleaner = new HtmlCleaner();
                TagNode node = cleaner.clean(new URL(arg[0]));

                Object[] tags = node.evaluateXPath("//div/table/tbody/tr/td/a/img"); //[@class='asset']
                for (Object tag : tags) {
                    TagNode aTag = (TagNode) tag;
                    String href = aTag.getAttributeByName("src").trim();
                    String h = aTag.getAttributeByName("height").trim();
                    String w = aTag.getAttributeByName("width").trim();
                    String content = aTag.getText().toString().trim();
                   // System.out.println("link\t: " + content + "[" + href + "]");
                    output.add(href);

                    indexListAdapter.img_h.add(Integer.parseInt(h));
                    indexListAdapter.img_w.add(Integer.parseInt(w));

                    // сразу грузим пикчи
                    Picasso.with(activity).load(href);
                }

                Object[] atags = node.evaluateXPath("//div/table/tbody/tr/td/a");
                for (Object tag : atags) {
                    TagNode aTag = (TagNode) tag;
                    String href = aTag.getAttributeByName("href").trim();
                    indexListAdapter.more_urls.add(href);
                }

                Object[] captions = node.evaluateXPath("//div[@class='title']/a");//  a
                for (Object tag : captions) {
                    TagNode aTag = (TagNode) tag;
                    String href = aTag.getAttributeByName("href").trim();
                    String content = aTag.getText().toString().trim();
                    //Log.i("content: 122: ", content);
                    indexListAdapter.names.add(content);
                    indexListAdapter.names_urls.add(href);
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return output;
        }

        //Событие по окончанию парсинга
        protected void onPostExecute(List<String> output) {
            loading_flag = false;
            //Находим ListView
            ListView listview = (ListView) findViewById(R.id.listView1);
            //Загружаем в него результат работы doInBackground
            indexListAdapter.addAll(output);
            progressBar.setVisibility(View.GONE);
            progressBar.animation_stop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void clearLists() {
        indexListAdapter.clear();
        indexListAdapter.names.clear();
        indexListAdapter.names_urls.clear();
        indexListAdapter.more_urls.clear();
        indexListAdapter.img_h.clear();
        indexListAdapter.img_w.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            clearLists();
            offset = 0;
            loadIndexList();
            return true;
        }

        if (id == R.id.action_random) {
            clearLists();
            offset = Math.round( Math.random() * 100000);
            loadIndexList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
