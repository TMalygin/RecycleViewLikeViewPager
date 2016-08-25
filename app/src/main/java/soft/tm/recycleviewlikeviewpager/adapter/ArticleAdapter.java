package soft.tm.recycleviewlikeviewpager.adapter;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import soft.tm.recycleviewlikeviewpager.R;
import soft.tm.recycleviewlikeviewpager.recyclepager.Article;

public class ArticleAdapter extends RecyclerView.Adapter {
    private final List<Article> articles = new ArrayList<>();
    private OnItemClickListener itemClickListener;

    public ArticleAdapter() {
        for (int i = 1; i < 6; i++) {
            articles.add(new Article("description " + i, i + " page"));
        }
    }

    public OnItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        ArticleViewHolder articleViewHolder = (ArticleViewHolder) viewHolder;
        Article article = articles.get(position);
        articleViewHolder.textContent.setText(article.text);
        articleViewHolder.textTitle.setText(article.title);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClicked(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 : articles.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {

        public TextView textContent;
        public TextView textTitle;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            textContent = (TextView) itemView.findViewById(R.id.article_text);
            textTitle = (TextView) itemView.findViewById(R.id.article_title);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(int pos);
    }
}
