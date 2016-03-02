package com.guo.material.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guo.material.R;
import com.guo.material.widget.loading.ShapeLoadingDialog;

/**
 * Created by admin on 2016/3/1.
 */
public class LoadingFragment extends Fragment {

    private FloatingActionButton btn_float;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_loading, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_float = (FloatingActionButton) view.findViewById(R.id.btn_float);
        btn_float.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShapeLoadingDialog dialog = new ShapeLoadingDialog(getActivity());
                dialog.show();
            }
        });
    }
}
