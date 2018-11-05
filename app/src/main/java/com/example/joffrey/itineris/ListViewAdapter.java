package com.example.joffrey.itineris;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<Batiment> batimentsNomsList = null;
    private ArrayList<Batiment> arraylistComplet;
    private SearchView sv;

    public ListViewAdapter(Context context, List<Batiment> batimentsNomsList, SearchView sv) {
        mContext = context;
        this.batimentsNomsList = batimentsNomsList;
        inflater = LayoutInflater.from(mContext);
        this.arraylistComplet = new ArrayList<Batiment>();
        this.arraylistComplet.addAll(batimentsNomsList);
        this.sv = sv;
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return batimentsNomsList.size();
    }

    @Override
    public Batiment getItem(int position) {
        return batimentsNomsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item, null);
            holder.name = (TextView) view.findViewById(R.id.batimentNom);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(batimentsNomsList.get(position).getNom());
        // listview item click
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sv.setQuery(batimentsNomsList.get(position).getNom(), true);
            }
        });
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        batimentsNomsList.clear();
        if (charText.length() == 0) {
            batimentsNomsList.addAll(arraylistComplet);
        } else {
            for (Batiment b : arraylistComplet) {
                if (b.getNom().toLowerCase(Locale.getDefault()).contains(charText)) {
                    batimentsNomsList.add(b);
                }
            }
        }
        notifyDataSetChanged();
    }

}