package br.com.dlweb.maternidade.bebe;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import br.com.dlweb.maternidade.R;
import br.com.dlweb.maternidade.mae.Mae;

public class BebeAdapter extends RecyclerView.Adapter<BebeAdapter.BebeViewHolder>{
    private final List<Bebe> bebes;
    private final List<String> bebesIds;
    private final FragmentActivity activity;

    BebeAdapter(List<Bebe> bebes, List<String> bebesIds, FragmentActivity activity){
        this.bebes = bebes;
        this.bebesIds = bebesIds;
        this.activity = activity;
    }

    static class BebeViewHolder extends RecyclerView.ViewHolder {
        private final TextView nomeView;
        private final TextView nomeMaeView;

        BebeViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeView = itemView.findViewById(R.id.tvListBebeNome);
            nomeMaeView = itemView.findViewById(R.id.tvListBebeNomeMae);
        }
    }

    @NonNull
    @Override
    public BebeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bebe_item, parent, false);
        return new BebeViewHolder(v);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(BebeViewHolder viewHolder, int i) {
        final String id = bebesIds.get(i);
        viewHolder.nomeView.setText(bebes.get(i).getNome());

        // setar o nome da mãe (como se fosse uma FK de um BD relacional)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentMae = db.collection("Maes").document(bebes.get(i).getMaeId());
        documentMae.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Mae m = task.getResult().toObject(Mae.class);
                        viewHolder.nomeMaeView.setText(m.getNome());
                    } else {
                        Toast.makeText(activity, "Erro ao buscar o nome da mãe!", Toast.LENGTH_LONG).show();
                        Log.d("ListarMae", "nenhum documento encontrado");
                    }
                } else {
                    Toast.makeText(activity, "Erro ao buscar o nome da mãe!", Toast.LENGTH_LONG).show();
                    Log.d("ListarMae", "erro: ", task.getException());
                }
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("id", id);

                EditarFragment editarFragment = new EditarFragment();
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                editarFragment.setArguments(b);
                ft.replace(R.id.frameBebe, editarFragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bebes.size();
    }
}
