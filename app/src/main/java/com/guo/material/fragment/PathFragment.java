package com.guo.material.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guo.material.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PathFragment extends Fragment {


    public PathFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_path, container, false);
    }

}
