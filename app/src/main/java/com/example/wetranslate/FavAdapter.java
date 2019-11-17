package com.example.wetranslate;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FavAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<Favorites> mFavorites;
    private static LayoutInflater inflater = null;

    public FavAdapter(Activity activity, ArrayList<Favorites> favorites) {
        this.activity = activity;
        this.mFavorites = favorites;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mFavorites.size();
    }

    @Override
    public Favorites getItem(int i) {
        return mFavorites.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = view;
        itemView = (itemView == null) ? inflater.inflate(R.layout.list_item, null): itemView;
        TextView viewInput = (TextView) itemView.findViewById(R.id.listInput);
        TextView viewResult = (TextView) itemView.findViewById(R.id.listResult);
        TextView viewLanguage = (TextView) itemView.findViewById(R.id.listLanguage);
        Favorites selectedFav = mFavorites.get(i);
        viewInput.setText("Input Text:   " + selectedFav.getInput());
        viewResult.setText("Translated Text:   " + selectedFav.getResult());
        viewLanguage.setText("Translation Language:   " +selectedFav.getLanguage());
        return itemView;
    }

}
