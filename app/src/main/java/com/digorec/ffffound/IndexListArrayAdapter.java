package com.digorec.ffffound;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaScannerConnection;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndexListArrayAdapter extends ArrayAdapter<String> {
    private final Activity context;
    public  final List<String> names;
    public  final List<String> names_urls;
    private final List<String> urls;
    public  final List<String> more_urls;
    public  final List<Number> img_h;
    public  final List<Number> img_w;

    public IndexListArrayAdapter(Activity context, List<String> urls) {
        super(context, R.layout.rowlayout, urls);
        this.context    = context;
        this.urls       = urls;
        this.names      = new ArrayList<String>();
        this.names_urls = new ArrayList<String>();
        this.more_urls  = new ArrayList<String>();
        this.img_h      = new ArrayList<Number>();
        this.img_w      = new ArrayList<Number>();

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        this.cell_width = dm.widthPixels;
    }

    static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView textHeader;

    }

    private int cell_width = 0;

    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;
        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return ret;
    }

    public void updateMediaGallery(String filename) {
        MediaScannerConnection.scanFile(context,
                new String[]{filename}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String url     = urls.get(position);
        final String caption = names.get(position);
        final String caption_url = names_urls.get(position);
        final String more_url    = more_urls.get(position);

        final ViewHolder holder;

        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.rowlayout, null, true);
            holder = new ViewHolder();
            holder.textView   = (TextView) rowView.findViewById(R.id.label);
            holder.imageView  = (ImageView) rowView.findViewById(R.id.icon);
            holder.textHeader = (TextView) rowView.findViewById(R.id.textHeader);
            rowView.setTag(holder);

        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        TextView textSave = (TextView) rowView.findViewById(R.id.textSave);
        textSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createDirIfNotExists("/ffffound/");
                String fn = url;
                int cut = fn.lastIndexOf('/');
                if (cut != -1) { fn = fn.substring(cut + 1); }

                final String filename = Environment.getExternalStorageDirectory() + "/ffffound/" + fn;

                try {
                    if (url.endsWith(".gif")) {
                        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
                        req.setTitle("ffffound");
                        req.setDescription("Download "+fn+"...");
                        req.setDestinationInExternalPublicDir(
                                Environment.getExternalStorageDirectory() + "/ffffound/", fn);
                        req.allowScanningByMediaScanner();

                        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                        downloadManager.enqueue(req);

                        Toast.makeText(context, "File download "+fn, Toast.LENGTH_LONG).show();

                    } else {
                        Picasso.with(context).load(url).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                try {
                                    FileOutputStream fos;
                                    fos = new FileOutputStream(new File(filename));
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                    fos.flush();
                                    fos.close();
                                    Toast.makeText(context, "File saved ", Toast.LENGTH_LONG).show();

                                    updateMediaGallery(filename);

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, "FileNotFoundException", Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, "Error save file", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) { }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                holder.textView.setText("error");
                            }
                        });
                    }
                } catch (RuntimeException e) {
                    Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        TextView textMore = (TextView) rowView.findViewById(R.id.textMore);
        textMore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), MoreActivity.class);
                myIntent.putExtra("url", more_url);
                getContext().startActivity(myIntent);
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), MoreActivity.class);
                myIntent.putExtra("url", more_url);
                getContext().startActivity(myIntent);
            }
        });

        holder.textHeader.setText(caption);
        holder.textHeader.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), WebViewActivity.class);
                myIntent.putExtra("url", caption_url);
                getContext().startActivity(myIntent);
            }
        });

        holder.textView.setText(url);

        int width = 0;
        int height = 0;
        try {
            width  = img_w.get(position).intValue();
            height = img_h.get(position).intValue();
        } finally { }

        int calc_h = 0;
        int iv_width = cell_width;
        float delta = 0;
        delta = (float) height / width;
        if (iv_width > width) { width = iv_width; }
        calc_h = Math.round(delta * iv_width);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, calc_h);
        holder.imageView.setLayoutParams(params);
        holder.imageView.requestLayout();

            Picasso.with(context).load(url).into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            int width = 0;
                            int height = 0;
                            try {
                                width = holder.imageView.getDrawable().getIntrinsicWidth();
                                height = holder.imageView.getDrawable().getIntrinsicHeight();
                            } finally {
                            }

                            int calc_h = 0;
                            int iv_width = cell_width;
                            float delta = 0;
                            delta = (float) height / width;

                            if (iv_width > width) { width = iv_width; }
                            calc_h = Math.round(delta * iv_width);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, calc_h);
                            holder.imageView.setLayoutParams(params);
                            holder.imageView.requestLayout();

                            holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                            holder.textView.setText(
                                    "  width:  " + Integer.toString(width) +
                                    "  height: " + Integer.toString(height) +
                                    "  delta: " + Float.toString(delta)
                            );

                            holder.textHeader.setText(caption);

                            if (url.endsWith(".gif")) {
                                Glide.with(context).
                                        load(url).
                                        error(R.mipmap.load).
                                        into(holder.imageView);
                            }
                        }

                        @Override
                        public void onError() {
                            holder.textView.setText("error");
                        }
                    }
            );

        return rowView;
    }
}