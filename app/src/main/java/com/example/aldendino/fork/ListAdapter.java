package com.example.aldendino.fork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by aldendino on 2014-09-09.
 */
public class ListAdapter extends BaseAdapter {

    Context context;
    ListTree[] data;
    private static LayoutInflater inflater = null;

    public ListAdapter(Context context, ListTree[] data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.list_item, null);
        ListTree list = data[position];
        TextView index = (TextView) vi.findViewById(R.id.index);
        index.setText((position + 1) + ".");
        TextView text = (TextView) vi.findViewById(R.id.text);
        text.setText(list.name);
        TextView number = (TextView) vi.findViewById(R.id.number);
        if(list.isList()) {
            if(list.list.size() > 0) {
                number.setText("[" + list.list.size() + "]");
            }
        }
        else number.setText("");
        return vi;
    }

}
