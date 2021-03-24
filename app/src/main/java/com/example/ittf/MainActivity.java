package com.example.ittf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Documented;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public Elements content;
    ArrayList<String> titles;
    ArrayList<Bitmap> bitmap;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    Bitmap image;
    Document doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.list_view);
        titles = new ArrayList<>();
        bitmap = new ArrayList<>();
        recyclerAdapter = new RecyclerAdapter(titles, bitmap);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        new NewThread().execute();
    }


    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements View.OnClickListener {
        public RecyclerAdapter(ArrayList<String> titles, ArrayList<Bitmap> bitmap) {
            this.titles = titles;
            this.bitmap = bitmap;
        }


        ArrayList<String> titles;
        ArrayList<Bitmap> bitmap;


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            try {
                holder.imageView.setImageBitmap(bitmap.get(position));
                holder.textView.setText(titles.get(position));
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ViewArticle.class);

                    try {
                        for (int i = 0; i < content.size(); i++) {
                            if (i == position) {
                                Element featureImage = doc.select(".media").get(i);
                                Log.d("ssss", featureImage.select("a").get(0).attr("href"));

                                intent.putExtra("extra_link", featureImage.select("a").get(0).attr("href"));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return titles.size();
        }

        @Override
        public void onClick(View v) {
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView2);
                textView = itemView.findViewById(R.id.pro_item);
            }
        }

    }

    public class NewThread extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                doc = Jsoup.connect("https://www.ittf.com").get();
                content = doc.select(".col-xs-12");

                try {
                    for (int i = 0; i <= content.size(); i++) {
                        Element featureImage = doc.select(".media-left").get(i);
                        String temp = featureImage.getElementsByAttribute("style").toString();
                        // URL of image
                        titles.add(content.get(i + 2).text());
                        String imageStrg = temp.substring(temp.indexOf("(") + 1, temp.indexOf(")"));
                        image = getImageBitmap(imageStrg);
                        bitmap.add(image);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            recyclerView.removeAllViews();

            recyclerView.setAdapter(recyclerAdapter);
        }

        private Bitmap getImageBitmap(String url) {
            Bitmap bm = null;
            try {
                // See what we are getting
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);

                bis.close();
                is.close();
            } catch (IOException e) {
            }
            return bm;
        }
    }
}