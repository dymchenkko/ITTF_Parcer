package com.example.ittf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class ViewArticle extends AppCompatActivity {
    public Elements content;
    TextView tw;
    TextView title;
    TextView introduce;
    Elements title_el;
    Elements date_article;
    Elements introd_post;
    TextView date;
    Document doc;
    String newString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_article);
        tw = findViewById(R.id.article);
        title = findViewById(R.id.header_title);
        date = findViewById(R.id.date);
        introduce = findViewById(R.id.post_introduce);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                newString = null;
            } else {
                newString = extras.getString("extra_link");
            }
        } else {
            newString = (String) savedInstanceState.getSerializable("extra_link");
        }
        new NewThread().execute();

    }

    public class NewThread extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                doc = Jsoup.connect(newString).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            content = doc.select(".content");
            title_el = doc.select(".page-header");
            date_article = doc.select(".col-xs-12").select("p").select(".text-muted");
            introd_post = doc.select(".post-introduction");
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            tw.setText(content.text());
            title.setText(title_el.text());
            date.setText(date_article.get(0).text());
            introduce.setText(introd_post.text());
        }
    }

}