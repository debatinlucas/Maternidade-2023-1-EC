package br.com.dlweb.maternidade.bebe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import br.com.dlweb.maternidade.R;
import br.com.dlweb.maternidade.mae.Mae;
import br.com.dlweb.maternidade.medico.Medico;

public class AdicionarFragment extends Fragment {

    private EditText etNome;
    private EditText etDataNascimento;
    private EditText etPeso;
    private EditText etAltura;
    private Spinner spMedico;
    private Spinner spMae;
    private FirebaseFirestore db;
    private List<String> maesIds;
    private List<String> maesNomes;
    private List<String> medicosIds;
    private List<String> medicosNomes;

    public AdicionarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bebe_fragment_adicionar, container, false);

        db = FirebaseFirestore.getInstance();

        etNome = v.findViewById(R.id.editTextNome);
        etDataNascimento = v.findViewById(R.id.editTextDataNascimento);
        etPeso = v.findViewById(R.id.editTextPeso);
        etAltura = v.findViewById(R.id.editTextAltura);
        spMedico = v.findViewById(R.id.spinnerMedico);
        spMae = v.findViewById(R.id.spinnerMae);

        // Obtém as mães para adicionar ao spinner
        CollectionReference collectionMae = db.collection("Maes");
        collectionMae.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Obtém os IDs dos documentos das mães
                    maesIds = new ArrayList<String>();
                    maesNomes = new ArrayList<String>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        maesIds.add(document.getId());
                        maesNomes.add(document.toObject(Mae.class).getNome());
                    }
                    ArrayAdapter<String> spMaeArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, maesNomes);
                    spMae.setAdapter(spMaeArrayAdapter);
                } else {
                    Toast.makeText(getActivity(), "Erro ao buscar as mães!", Toast.LENGTH_LONG).show();
                    Log.d("ListarMae", "mensagem de erro: ", task.getException());
                }
            }
        });

        // Obtém os médicos para adicionar ao spinner
        CollectionReference collectionMedico = db.collection("Medicos");
        collectionMedico.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Obtém os IDs dos documentos das médicos
                    medicosIds = new ArrayList<String>();
                    medicosNomes = new ArrayList<String>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        medicosIds.add(document.getId());
                        medicosNomes.add(document.toObject(Medico.class).getNome());
                    }
                    ArrayAdapter<String> spMedicoArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, medicosNomes);
                    spMedico.setAdapter(spMedicoArrayAdapter);
                } else {
                    Toast.makeText(getActivity(), "Erro ao buscar os médicos!", Toast.LENGTH_LONG).show();
                    Log.d("ListarMedico", "mensagem de erro: ", task.getException());
                }
            }
        });

        Button btnSalvar = v.findViewById(R.id.buttonAdicionar);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionar();
            }
        });

        return v;
    }

    private void adicionar () {
        if (spMae.getSelectedItem() == null) {
            Toast.makeText(getActivity(), "Por favor, selecione a mãe!", Toast.LENGTH_LONG).show();
        } else if (spMedico.getSelectedItem() == null) {
            Toast.makeText(getActivity(), "Por favor, selecione o médico!", Toast.LENGTH_LONG).show();
        } else if (etNome.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Por favor, informe o nome!", Toast.LENGTH_LONG).show();
        } else if (etPeso.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Por favor, informe o peso!", Toast.LENGTH_LONG).show();
        } else if (etAltura.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Por favor, informe a altura!", Toast.LENGTH_LONG).show();
        } else if (etDataNascimento.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Por favor, informe a data de nascimento!", Toast.LENGTH_LONG).show();
        } else {
            Bebe b = new Bebe();
            String nomeMae = spMae.getSelectedItem().toString();
            b.setMaeId(maesIds.get(maesNomes.indexOf(nomeMae)));
            String nomeMedico = spMedico.getSelectedItem().toString();
            b.setMedicoId(medicosIds.get(medicosNomes.indexOf(nomeMedico)));
            b.setNome(etNome.getText().toString());
            b.setData_nascimento(etDataNascimento.getText().toString());
            b.setPeso(Float.parseFloat(etPeso.getText().toString()));
            b.setAltura(Integer.parseInt(etAltura.getText().toString()));

            CollectionReference collectionBebe = db.collection("Bebes");
            collectionBebe.add(b).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(getActivity(), "Bebê salvo!", Toast.LENGTH_LONG).show();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameBebe, new ListarFragment()).commit();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Erro ao salvar o bebê!", Toast.LENGTH_LONG).show();
                    Log.d("AdicionarBebe", "mensagem de erro: ", e);
                }
            });

        }
    }
}