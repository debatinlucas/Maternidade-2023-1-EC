package br.com.dlweb.maternidade.mae;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
        View v = inflater.inflate(R.layout.mae_fragment_listar, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        RecyclerView recyclerViewMaes = v.findViewById(R.id.recyclerViewMaes);

        CollectionReference collectionMae = db.collection("Maes");
        collectionMae.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            LinearLayoutManager manager = new LinearLayoutManager(v.getContext());
                            recyclerViewMaes.setLayoutManager(manager);
                            recyclerViewMaes.addItemDecoration(new DividerItemDecoration(v.getContext(), LinearLayoutManager.VERTICAL));
                            recyclerViewMaes.setHasFixedSize(true);
                            List<Mae> maes = task.getResult().toObjects(Mae.class);
                            // Obtém os IDs dos documentos das mães
                            List<String> maesIds = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                maesIds.add(document.getId());
                            }
                            MaeAdapter adapterMaes = new MaeAdapter(maes, maesIds, getActivity());
                            recyclerViewMaes.setAdapter(adapterMaes);
                        } else {
                            Toast.makeText(getActivity(), "Erro ao buscar as mães!", Toast.LENGTH_LONG).show();
                            Log.d("ListarMae", "mensagem de erro: ", task.getException());
                        }
                    }
                });
        return v;
    }
}