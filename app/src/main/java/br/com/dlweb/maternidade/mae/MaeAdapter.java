package br.com.dlweb.maternidade.mae;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.dlweb.maternidade.R;

public class MaeAdapter extends RecyclerView.Adapter<MaeAdapter.MaeViewHolder>{
    private final List<Mae> maes;
    private final List<String> maesIds;
    private final FragmentActivity activity;

    MaeAdapter(List<Mae> maes, List<String> maesIds, FragmentActivity activity){
        this.maes = maes;
        this.maesIds = maesIds;
        this.activity = activity;
    }

    static class MaeViewHolder extends RecyclerView.ViewHolder {
        private final TextView nomeView;
        private final TextView celularView;

        MaeViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeView = itemView.findViewById(R.id.tvListMaeNome);
            celularView = itemView.findViewById(R.id.tvListMaeCelular);
        }
    }

    @NonNull
    @Override
    public MaeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mae_item, parent, false);
        return new MaeViewHolder(v);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MaeViewHolder viewHolder, int i) {
        final String id = maesIds.get(i);
        viewHolder.nomeView.setText(maes.get(i).getNome());
        viewHolder.celularView.setText(maes.get(i).getCelular());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("id", id);

                EditarFragment editarFragment = new EditarFragment();
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                editarFragment.setArguments(b);
                ft.replace(R.id.frameMae, editarFragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return maes.size();
    }
}
