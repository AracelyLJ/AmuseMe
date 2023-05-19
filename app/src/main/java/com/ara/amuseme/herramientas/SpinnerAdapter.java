package com.ara.amuseme.herramientas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ara.amuseme.R;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<String> {
    ArrayList<String> valores;

    public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<String> valores) {
        super(context, textViewResourceId, valores);
        this.valores = valores;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(final int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.spin_value, parent, false);
        final TextView label=(TextView)row.findViewById(R.id.txtOpcion);
        label.setText(valores.get(position));
        return row;
    }
}
