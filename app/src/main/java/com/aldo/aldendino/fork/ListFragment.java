package com.aldo.aldendino.fork;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class ListFragment extends Fragment {
    private ListView listView ;
    private Button addButton ;
    private ListTree list;

    @Override
    public void setArguments(Bundle args) {
        list = (ListTree) args.get(MainActivity.LIST_KEY);
        super.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_main, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView1) ;
        View footerView = getActivity().getLayoutInflater().inflate(R.layout.footer_button, null);
        listView.addFooterView(footerView);
        addButton = (Button) footerView.findViewById(R.id.button2) ;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if(savedInstanceState != null) {
            // Todo
        }
        populateListView();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Todo
    }

    private void populateListView()
    {
        ListTree[] items = list.getListArray() ;
        ForkListAdapter forkListAdapter = new ForkListAdapter(getActivity(), items);
        listView.setAdapter(forkListAdapter) ;
        //((MainActivity) getActivity()).setHome(); inconvertable types error?
    }
}
