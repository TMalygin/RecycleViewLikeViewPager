package soft.tm.recycleviewlikeviewpager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import soft.tm.recycleviewlikeviewpager.adapter.ArticleAdapter;
import soft.tm.recycleviewlikeviewpager.recyclepager.PagerManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new PagerManager());
        recyclerView.setAdapter(new ArticleAdapter());
    }
}
