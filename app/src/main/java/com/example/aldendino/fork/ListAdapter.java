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
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
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
