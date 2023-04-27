package br.com.dlweb.maternidade.bebe;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import br.com.dlweb.maternidade.R;
import br.com.dlweb.maternidade.mae.Mae;
import br.com.dlweb.maternidade.medico.Medico;

public class EditarFragment extends Fragment {

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
    private Bebe b;

    public EditarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bebe_fragment_editar, container, false);
        etNome = v.findViewById(R.id.editTextNome);
        etDataNascimento = v.findViewById(R.id.editTextDataNascimento);
        etPeso = v.findViewById(R.id.editTextPeso);
        etAltura = v.findViewById(R.id.editTextAltura);
        spMedico = v.findViewById(R.id.spinnerMedico);
        spMae = v.findViewById(R.id.spinnerMae);

        // id enviado via parâmetro no ListarFragment
        Bundle bundle = getArguments();
        String id_bebe = bundle != null ? bundle.getString("id") : null;

        Button btnEditar = v.findViewById(R.id.buttonEditar);
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editar(id_bebe);
            }
        });

        Button btnExcluir = v.findViewById(R.id.buttonExcluir);
        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.dialog_excluir_bebe);
                builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        excluir(id_bebe);
                    }
                });
                builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Não faz nada
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        db = FirebaseFirestore.getInstance();

        // Obtém os dados do bebê que será editado
        assert id_bebe != null;
        DocumentReference documentBebe = db.collection("Bebes").document(id_bebe);
        documentBebe.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        b = task.getResult().toObject(Bebe.class);
                        assert b != null;
                        etNome.setText(b.getNome());
                        etPeso.setText(String.valueOf(b.getPeso()));
                        etAltura.setText(String.valueOf(b.getAltura()));
                        etDataNascimento.setText(b.getData_nascimento());
                        // Obtém as mães para adicionar ao spinner
                        spinnerMaes();
                        // Obtém os médicos para adicionar ao spinner
                        spinnerMedicos();
                    } else {
                        Toast.makeText(getActivity(), "Erro ao buscar o bebê!", Toast.LENGTH_LONG).show();
                        Log.d("ListarBebe", "nenhum documento encontrado");
                    }
                } else {
                    Toast.makeText(getActivity(), "Erro ao buscar o bebê!", Toast.LENGTH_LONG).show();
                    Log.d("ListarBebe", "erro: ", task.getException());
                }
            }
        });

        return v;
    }

    private void editar (String id) {
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

            DocumentReference documentBebe = db.collection("Bebes").document(id);
            documentBebe.set(b).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Bebê atualizado!", Toast.LENGTH_LONG).show();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameBebe, new ListarFragment()).commit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("EditarBebe", "erro: ", e);
                        }
                    });

        }
    }

    private void excluir(String id) {
        db.collection("Bebes").document(id)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Bebê excluído!", Toast.LENGTH_LONG).show();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameBebe, new ListarFragment()).commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ExcluirBebe", "erro: ", e);
                    }
                });

    }

    private void spinnerMaes() {
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
                    // seta a seleção do spinner da mãe com os dados que estão no firestore
                    spMae.setSelection(maesIds.indexOf(b.getMaeId()));
                } else {
                    Toast.makeText(getActivity(), "Erro ao buscar as mães!", Toast.LENGTH_LONG).show();
                    Log.d("ListarMae", "mensagem de erro: ", task.getException());
                }
            }
        });
    }

    private void spinnerMedicos() {
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
                    // seta a seleção do spinner do médico com os dados que estão no firestore
                    spMedico.setSelection(medicosIds.indexOf(b.getMedicoId()));
                } else {
                    Toast.makeText(getActivity(), "Erro ao buscar os médicos!", Toast.LENGTH_LONG).show();
                    Log.d("ListarMedico", "mensagem de erro: ", task.getException());
                }
            }
        });
    }
}