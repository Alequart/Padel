package com.example.padel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<Item> items;

    private ItemClickListener mItemListener;

    public MyAdapter(Context context, List<Item> items, ItemClickListener mItemListener){
        this.context = context;
        this.items = items;
        this.mItemListener = mItemListener;
    }

    public MyAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.prenotazioneView.setText(items.get(position).getPrenotazione());
        holder.giocatore1View.setText(items.get(position).getGiocatore1());
        holder.giocatore2View.setText(items.get(position).getGiocatore2());
        holder.giocatore3View.setText(items.get(position).getGiocatore3());
        holder.giocatore4View.setText(items.get(position).getGiocatore4());
        holder.dataView.setText(items.get(position).getData());
        holder.orarioView.setText(items.get(position).getOrario());
        holder.campoView.setText(items.get(position).getCampo());

        holder.itemView.setOnClickListener(view -> {
            mItemListener.onItemClick(items.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface ItemClickListener{
        void onItemClick(Item item);
    }
}
