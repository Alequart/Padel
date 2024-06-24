package com.example.padel;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder{

    TextView prenotazioneView, giocatore1View, giocatore2View, giocatore3View, giocatore4View, dataView, orarioView, campoView;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        prenotazioneView = itemView.findViewById(R.id.prenotazione);
        giocatore1View = itemView.findViewById(R.id.giocatore1);
        giocatore2View = itemView.findViewById(R.id.giocatore2);
        giocatore3View = itemView.findViewById(R.id.giocatore3);
        giocatore4View = itemView.findViewById(R.id.giocatore4);
        dataView = itemView.findViewById(R.id.data);
        orarioView = itemView.findViewById(R.id.orario);
        campoView = itemView.findViewById(R.id.campo);
    }
}
