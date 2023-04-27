package br.com.dlweb.maternidade.medico;

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
        View v = inflater.inflate(R.layout.medico_fragment_listar, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        RecyclerView recyclerViewMedicos = v.findViewById(R.id.recyclerViewMedicos);

        CollectionReference collectionMedico = db.collection("Medicos");
        collectionMedico.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    LinearLayoutManager manager = new LinearLayoutManager(v.getContext());
                    recyclerViewMedicos.setLayoutManager(manager);
                    recyclerViewMedicos.addItemDecoration(new DividerItemDecoration(v.getContext(), LinearLayoutManager.VERTICAL));
                    recyclerViewMedicos.setHasFixedSize(true);
                    List<Medico> medicos = task.getResult().toObjects(Medico.class);
                    // Obtém os IDs dos documentos dos médicos
                    List<String> medicosIds = new ArrayList<String>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        medicosIds.add(document.getId());
                    }
                    MedicoAdapter adapterMedicos = new MedicoAdapter(medicos, medicosIds, getActivity());
                    recyclerViewMedicos.setAdapter(adapterMedicos);
                } else {
                    Toast.makeText(getActivity(), "Erro ao buscar os médicos!", Toast.LENGTH_LONG).show();
                    Log.d("ListarMedico", "mensagem de erro: ", task.getException());
                }
            }
        });
        return v;
    }
}