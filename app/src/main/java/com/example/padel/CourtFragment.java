package com.example.padel;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CourtFragment extends Fragment {


    private PrenotaMatchActivity prenotaMatchActivity;

    ImageView verde, blu, rosso, arancione;

    public CourtFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        prenotaMatchActivity = (PrenotaMatchActivity) getActivity();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_court, container, false);
        verde = (ImageView) view.findViewById(R.id.campo_verde);
        blu = (ImageView) view.findViewById(R.id.campo_blu);
        rosso = (ImageView) view.findViewById(R.id.campo_rosso);
        arancione = (ImageView) view.findViewById(R.id.campo_arancione);

        verde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor("verde");
                prenotaMatchActivity.editTextCampo.setText("Campo " + prenotaMatchActivity.getColor());
                getActivity().onBackPressed();
            }
        });
        blu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor("blu");
                prenotaMatchActivity.editTextCampo.setText("Campo " + prenotaMatchActivity.getColor());
                getActivity().onBackPressed();
            }
        });
        rosso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor("rosso");
                prenotaMatchActivity.editTextCampo.setText("Campo " + prenotaMatchActivity.getColor());
                getActivity().onBackPressed();
            }
        });
        arancione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor("arancione");
                prenotaMatchActivity.editTextCampo.setText("Campo " + prenotaMatchActivity.getColor());
                getActivity().onBackPressed();
            }
        });

        return view;
    }


    public void setColor(String col){
        PrenotaMatchActivity.color = col;
    }
}