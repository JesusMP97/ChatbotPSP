package com.example.chatbotpsp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterMultiType extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int TYPE_USER = 1;
    private static int TYPE_BOT = 2;
    private static int TYPE_SYSTEM = 3;
    private Context context;
    public ArrayList<Mensaje> mensajes;

    public AdapterMultiType(Context context) {
        this.context = context;
        this.mensajes = new ArrayList<>();
    }

    public void setMensajes(ArrayList<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == TYPE_USER) {
            view = LayoutInflater.from(context).inflate(R.layout.item_user, viewGroup, false);
            return new UserViewHolder(view);

        } else if(viewType == TYPE_BOT){
            view = LayoutInflater.from(context).inflate(R.layout.item_bot, viewGroup, false);
            return new BotViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_system, viewGroup, false);
            return new SystemViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mensajes.get(position).talker.compareToIgnoreCase("User") == 0) {
            return TYPE_USER;
        } else if (mensajes.get(position).talker.compareToIgnoreCase("Bot") == 0){
            return TYPE_BOT;
        } else {
            return TYPE_SYSTEM;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_USER) {
            ((UserViewHolder) viewHolder).setMensajeDetails(mensajes.get(position));
        } else if(getItemViewType(position) == TYPE_BOT){
            ((BotViewHolder) viewHolder).setMensajeDetails(mensajes.get(position));
        } else {
            ((SystemViewHolder) viewHolder).setMensajeDetails(mensajes.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMensaje, tvHora;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
            tvHora = itemView.findViewById(R.id.tvHora);
        }

        void setMensajeDetails(Mensaje mensaje) {
            tvMensaje.setText(mensaje.sentenceEs);
            tvHora.setText(mensaje.time);
        }
    }

    class BotViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMensaje, tvHora;

        BotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
            tvHora = itemView.findViewById(R.id.tvHora);
        }

        void setMensajeDetails(Mensaje mensaje) {
            tvMensaje.setText(mensaje.sentenceEs);
            tvHora.setText(mensaje.time);
        }
    }

    class SystemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvFecha;

        SystemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvMensaje);
        }

        void setMensajeDetails(Mensaje mensaje) {
            tvFecha.setText(mensaje.time);
        }
    }

}
