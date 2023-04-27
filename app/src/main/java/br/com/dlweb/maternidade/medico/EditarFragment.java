package br.com.dlweb.maternidade.medico;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.dlweb.maternidade.R;

public class EditarFragment extends Fragment {


    private FirebaseFirestore db;
    private Medico m;
    private EditText etNome;
    private EditText etEspecialidade;
    private EditText etCelular;

    public EditarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.medico_fragment_editar, container, false);
        etNome = v.findViewById(R.id.editTextNome);
        etEspecialidade = v.findViewById(R.id.editTextEspecialidade);
        etCelular = v.findViewById(R.id.editTextCelular);

        // id enviado via parâmetro no ListarFragment
        Bundle b = getArguments();
        String id_medico = b != null ? b.getString("id") : null;

        Button btnEditar = v.findViewById(R.id.buttonEditar);
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editar(id_medico);
            }
        });

        Button btnExcluir = v.findViewById(R.id.buttonExcluir);
        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.dialog_excluir_medico);
                builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        excluir(id_medico);
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

        // Obtém os dados do médico que será editado
        assert id_medico != null;
        DocumentReference documentMedico = db.collection("Medicos").document(id_medico);
        documentMedico.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        m = task.getResult().toObject(Medico.class);
                        assert m != null;
                        etNome.setText(m.getNome());
                        etEspecialidade.setText(m.getEspecialidade().toString());
                        etCelular.setText(m.getCelular());
                    } else {
                        Toast.makeText(getActivity(), "Erro ao buscar o médico!", Toast.LENGTH_LONG).show();
                        Log.d("ListarMedico", "nenhum documento encontrado");
                    }
                } else {
                    Toast.makeText(getActivity(), "Erro ao buscar o médico!", Toast.LENGTH_LONG).show();
                    Log.d("ListarMedico", "erro: ", task.getException());
                }
            }
        });

        return v;
    }


    private void editar (String id) {
        if (etNome.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Por favor, informe o nome!", Toast.LENGTH_LONG).show();
        } else if (etEspecialidade.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Por favor, informe a especialidade!", Toast.LENGTH_LONG).show();
        } else if (etCelular.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Por favor, informe o número do celular!", Toast.LENGTH_LONG).show();
        } else {
            m.setNome(etNome.getText().toString());
            m.setEspecialidade(etEspecialidade.getText().toString());
            m.setCelular(etCelular.getText().toString());
            DocumentReference documentMedico = db.collection("Medicos").document(id);
            documentMedico.set(m).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Médico atualizado!", Toast.LENGTH_LONG).show();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameMedico, new ListarFragment()).commit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("EditarMedico", "erro: ", e);
                        }
                    });
        }
    }

    private void excluir(String id) {
        // TODO: verificar se tem algum bebê associado ao médico antes de excluir
        db.collection("Medicos").document(id)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Médico excluído!", Toast.LENGTH_LONG).show();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameMedico, new ListarFragment()).commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ExcluirMedico", "erro: ", e);
                    }
                });

    }
}