package org.baiyu.jiaapp.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.baiyu.jiaapp.R;

public class SearchCityFrag extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_city_frag, container, false);
        view.setBackgroundResource(R.color.green);
        return view;
    }
}
