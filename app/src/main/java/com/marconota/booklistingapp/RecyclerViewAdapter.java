package com.marconota.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    List<Book> bookList;
    LayoutInflater bookInflater;
    Context context;
    private static final String LOG_TAG = RecyclerViewAdapter.class.getName();

    //constructor
    public RecyclerViewAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookInflater = LayoutInflater.from(context);
        this.bookList = bookList;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = bookInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Book currentBook = bookList.get(position);
        holder.title.setText(currentBook.getTitle());
        holder.author.setText("by " + currentBook.getAuthor());
        float rating = (float) currentBook.getRating();
        holder.ratingBar.setRating(rating);

        //get thumbnails and assign them
        Uri uri = Uri.parse(currentBook.getThumbnail());
        if (currentBook.getThumbnail() != null && !currentBook.getThumbnail().isEmpty()) {
            Picasso.with(context).load(uri).into(holder.thumbnail);
        } else {
            holder.thumbnail.setImageResource(R.drawable.cover_temp);
        }

        holder.book_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(currentBook.getUrl()));
                context.startActivity(intent);
            }
        });
    }

    public void addAll(List<Book> data) {
        bookList.clear();
        bookList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        int items = this.bookList.size();
        return items;
    }

    //the holder class
    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.imageView)
        ImageView thumbnail;
        @BindView(R.id.book_layout)
        View book_layout;
        @BindView(R.id.rating_bar)
        RatingBar ratingBar;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}