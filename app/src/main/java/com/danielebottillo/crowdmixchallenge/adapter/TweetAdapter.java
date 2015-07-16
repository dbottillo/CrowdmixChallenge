package com.danielebottillo.crowdmixchallenge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielebottillo.crowdmixchallenge.R;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TweetAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Tweet> tweets;
    SimpleDateFormat formatter;
    SimpleDateFormat dateFormatter;

    public TweetAdapter(Context context, ArrayList<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
        formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.getDefault());
        dateFormatter = new SimpleDateFormat("EEEE dd MMMM, HH:mm", Locale.getDefault());
    }

    @Override
    public int getCount() {
        return tweets.size();
    }

    @Override
    public Tweet getItem(int position) {
        return tweets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Tweet tweet = tweets.get(position);
        final ArtistHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.tweet_row, parent, false);
            holder = new ArtistHolder(convertView);
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (ArtistHolder) convertView.getTag();
        }

        holder.name.setText(tweet.text);
        holder.author.setText(tweet.user.name);
        holder.authorusername.setText("@" + tweet.user.screenName);
        Picasso.with(context).load(tweet.user.profileImageUrl).into(holder.image);

        holder.time.setText(getDateFormatted(tweet.createdAt));

        return convertView;
    }

    private class ArtistHolder {
        TextView name;
        TextView author;
        TextView authorusername;
        ImageView image;
        TextView time;

        public ArtistHolder(View row) {
            name = (TextView) row.findViewById(R.id.tweet_text);
            author = (TextView) row.findViewById(R.id.tweet_author);
            authorusername = (TextView) row.findViewById(R.id.tweet_author_name);
            image = (ImageView) row.findViewById(R.id.tweet_image);
            time = (TextView) row.findViewById(R.id.tweet_time);
        }
    }

    private String getDateFormatted(String input) {
        try {
            Date dateDelivery = formatter.parse(input);
            return dateFormatter.format(dateDelivery);
        } catch (ParseException e) {
            return input;
        }
    }
}
