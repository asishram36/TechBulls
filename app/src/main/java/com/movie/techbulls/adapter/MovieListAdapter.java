package com.movie.techbulls.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.movie.techbulls.R;
import com.movie.techbulls.model.MovieListDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MyHolder> implements Filterable {

    private List<MovieListDataModel> lists;
    private List<MovieListDataModel> mFilteredList;

    Context context;

    private SharedPreferences sharedPreferences;

    CustomFilter filter;

    public static class MyHolder extends RecyclerView.ViewHolder {

        private TextView textTitle, textReleaseYear;

        private ConstraintLayout myMovieCard;

        private ImageView imagePoster;

        public MyHolder(View view) {

            super(view);

            textTitle = (TextView) view.findViewById(R.id.textTitle);
            textReleaseYear = (TextView) view.findViewById(R.id.textReleaseYear);

            imagePoster = (ImageView) view.findViewById(R.id.imagePoster);

            myMovieCard = (ConstraintLayout) view.findViewById(R.id.myMovieCard);

        }
    }

    public MovieListAdapter(List<MovieListDataModel> lists, Context context) {
        this.lists = lists;
        this.mFilteredList = lists;
        this.context = context;
        filter = new CustomFilter(MovieListAdapter.this);

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_item, parent, false);

        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int listPosition) {

        holder.textTitle.setText(lists.get(listPosition).getMovieName());
        holder.textReleaseYear.setText(lists.get(listPosition).getMovieYear());

        if (lists.get(listPosition).getImageUrl().equalsIgnoreCase("")){
            holder.imagePoster.setImageResource(R.drawable.batman);
        } else {

            Picasso.with(context).load(lists.get(listPosition).getImageUrl()).into(holder.imagePoster);

        }

        holder.myMovieCard.setOnClickListener(onClickListener(listPosition));
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {


            }

        };
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    //INNER CLASS
    class CustomFilter extends Filter {

        private MovieListAdapter movieListAdapter;

        private CustomFilter(MovieListAdapter movieListAdapter) {
            super();
            this.movieListAdapter = movieListAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            // TODO Auto-generated method stub
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {

                //CONSTARINT TO UPPER
                constraint = constraint.toString().toLowerCase();
                ArrayList<MovieListDataModel> filters = new ArrayList<MovieListDataModel>();
                //get specific items
                for (int i = 0; i < mFilteredList.size(); i++) {

                    if (mFilteredList.get(i).getMovieName().toLowerCase().contains(constraint) ||
                            mFilteredList.get(i).getMovieYear().toLowerCase().contains(constraint)) {

                        MovieListDataModel dm = new MovieListDataModel(mFilteredList.get(i).getImageUrl(),
                                mFilteredList.get(i).getMovieName(), mFilteredList.get(i).getMovieYear());

                        filters.add(dm);
                    }
                }
                results.count = filters.size();
                results.values = filters;
            } else {
                results.count = mFilteredList.size();
                results.values = mFilteredList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // TODO Auto-generated method stub
            lists = (ArrayList<MovieListDataModel>) results.values;
            this.movieListAdapter.notifyDataSetChanged();
        }
    }
}
