package com.example.padel;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CourtTrainingFragment extends Fragment {


    private TrainingActivity trainingActivity;

    ImageView verde, blu, rosso, arancione;


    public CourtTrainingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        trainingActivity = (TrainingActivity) getActivity();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_court_training, container, false);

        verde = (ImageView) view.findViewById(R.id.campo_verde);
        blu = (ImageView) view.findViewById(R.id.campo_blu);
        rosso = (ImageView) view.findViewById(R.id.campo_rosso);
        arancione = (ImageView) view.findViewById(R.id.campo_arancione);

        verde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor("verde");
                trainingActivity.editTextCampo.setText("Campo " + trainingActivity.getColor());
                getActivity().onBackPressed();
            }
        });
        blu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor("blu");
                trainingActivity.editTextCampo.setText("Campo " + trainingActivity.getColor());
                getActivity().onBackPressed();
            }
        });
        rosso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor("rosso");
                trainingActivity.editTextCampo.setText("Campo " + trainingActivity.getColor());
                getActivity().onBackPressed();
            }
        });
        arancione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor("arancione");
                trainingActivity.editTextCampo.setText("Campo " + trainingActivity.getColor());
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    public void setColor(String col){
        TrainingActivity.color = col;
    }
}