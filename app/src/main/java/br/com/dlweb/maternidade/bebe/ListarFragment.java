package br.com.dlweb.maternidade.bebe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import br.com.dlweb.maternidade.R;

public class ListarFragment extends Fragment {

    public ListarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bebe_fragment_listar, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        RecyclerView recyclerViewBebes = v.findViewById(R.id.recyclerViewBebes);

        CollectionReference collectionBebe = db.collection("Bebes");
        collectionBebe.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    LinearLayoutManager manager = new LinearLayoutManager(v.getContext());
                    recyclerViewBebes.setLayoutManager(manager);
                    recyclerViewBebes.addItemDecoration(new DividerItemDecoration(v.getContext(), LinearLayoutManager.VERTICAL));
                    recyclerViewBebes.setHasFixedSize(true);
                    List<Bebe> bebes = task.getResult().toObjects(Bebe.class);
                    // Obtém os IDs dos documentos das mães
                    List<String> bebesIds = new ArrayList<String>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        bebesIds.add(document.getId());
                    }
                    BebeAdapter adapterBebes = new BebeAdapter(bebes, bebesIds, getActivity());
                    recyclerViewBebes.setAdapter(adapterBebes);
                } else {
                    Toast.makeText(getActivity(), "Erro ao buscar as mães!", Toast.LENGTH_LONG).show();
                    Log.d("ListarBebe", "mensagem de erro: ", task.getException());
                }
            }
        });
        return v;
    }
}