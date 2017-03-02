package pl.librus.client.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

import java8.util.stream.StreamSupport;
import pl.librus.client.BuildConfig;
import pl.librus.client.R;

public class AboutActivity extends AppCompatActivity {

    private static final Map<String, String[]> libs = new ImmutableMap.Builder<String, String[]>()
            .put("Apache License v2.0", new String[]{
                    "Android Compatibility Library v4\nby Google",
                    "Android Compatibility Library v7\nby Google",
                    "Android Multidex Support Library \nby Google",
                    "Android Design Support Library \nby Google",
                    "Android SDK\nby Google",
                    "Guava\nby Google",
                    "Joda-Time\nby JodaOrg",
                    "Jackson\nby FasterXML",
                    "Requery\nby requery",
                    "Flexible Adapter\nby davideas",


            })
            .put("MIT", new String[]{
                    "material-dialogs\nby afollestad",
                    "TextDrawable\nby amulyakhare",
                    "AsyncManager\nby boxme",
                    "Lorem\nby mdeanda"
            })
            .put("GPLv2", new String[]{
                    "streamsupport\nby streamsupport"
            })
            .build();
    private static final List<Author> authors = Lists.newArrayList(
            new Author("shymmq", "https://github.com/shymmq"),
            new Author("Frioo", "https://github.com/Frioo"),
            new Author("rowysock", "https://github.com/rowysock"),
            new Author("xdk78", "https://github.com/xdk78"),
            new Author("mimi89999", "https://github.com/mimi89999")
    );

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        LinearLayout libsContainer = (LinearLayout) findViewById(R.id.libs_container);
        LinearLayout authorsContainer = (LinearLayout) findViewById(R.id.authors_container);
        TextView version = (TextView) findViewById(R.id.about_version);

        LayoutInflater inflater = LayoutInflater.from(this);

        version.setText(new StringBuilder()
                .append(BuildConfig.VERSION_NAME)
                .append(" | ")
                .append(BuildConfig.FLAVOR));

        StreamSupport.stream(libs.entrySet()).forEach(entry -> {
            TextView section = (TextView) inflater.inflate(R.layout.text_list_item, libsContainer, false);
            section.setTypeface(section.getTypeface(), Typeface.BOLD);
            section.setText(entry.getKey());
            libsContainer.addView(section);

            for (String libraryName : entry.getValue()) {
                TextView item = (TextView) inflater.inflate(R.layout.text_list_item, libsContainer, false);
                item.setText(libraryName);
                libsContainer.addView(item);
            }
        });

        StreamSupport.stream(authors).forEach(author -> {
            View view = inflater.inflate(R.layout.about_author, authorsContainer, false);

            TextView name = (TextView) view.findViewById(R.id.author_nick);
            View icon = view.findViewById(R.id.author_link);

            name.setText(author.getName());
            icon.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(author.getLink()));
                startActivity(intent);
            });
            authorsContainer.addView(view);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    static class Author {
        private final String name;
        private final String link;

        Author(String name, String link) {
            this.name = name;
            this.link = link;
        }

        public String getName() {
            return name;
        }

        public String getLink() {
            return link;
        }
    }
}
