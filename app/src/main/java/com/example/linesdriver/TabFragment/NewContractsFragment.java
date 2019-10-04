package com.example.linesdriver.TabFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linesdriver.Adapter.NewContractAdapter;
import com.example.linesdriver.R;

public class NewContractsFragment extends Fragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    RecyclerView rv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View v= inflater.inflate(R.layout.fragment_new_contracts, container, false);
      rv=v.findViewById(R.id.rv);
      rv.setHasFixedSize(true);
      rv.setLayoutManager(new LinearLayoutManager(getContext()));
        NewContractAdapter adapter=new NewContractAdapter(getContext());
        rv.setAdapter(adapter);
        return v;
    }


}
