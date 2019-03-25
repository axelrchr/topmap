package arocher.fr.topmap.controler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import java.util.ArrayList;
import java.util.List;
import arocher.fr.topmap.R;
import arocher.fr.topmap.SessionManager;
import arocher.fr.topmap.VolleySingleton;
import arocher.fr.topmap.myrequest.MyRequest;

public class MesGroupesActivity extends AppCompatActivity {

    private ListView lv_groupe, lv_membres;
    private String NOM = "";
    private MyRequest request;
    private SessionManager sessionManager;
    private EditText input;
    private String pseudoAmi;

    /**
     * Constructeur appellé au lancement de l'activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_groupes);

        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        final List<String> groupeListe = new ArrayList<>();
        final ArrayAdapter<String> groupeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groupeListe);
        final List<String> membreListe = new ArrayList<>();
        final ArrayAdapter<String> membreAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, membreListe);

        sessionManager = new SessionManager(this);

        // CREATION DE LA BOITE DE DIALOGUE POUR SAISIR UN PSEUDO
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajouter un membre");
        input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pseudoAmi = input.getText().toString();
                request.ajouterMembre(pseudoAmi, NOM, new MyRequest.ajouterMembreCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        membreListe.add(pseudoAmi);
                        lv_membres.setAdapter(membreAdapter);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog ad = builder.create();
        final Button btn_ajouterMembres = findViewById(R.id.btn_ajouterMembres);

        Button btn_quitterGroupe = findViewById(R.id.btn_quitterGroupe);
        lv_groupe = findViewById(R.id.lv_groupe);
        lv_membres = findViewById(R.id.lv_membres);



        request.recupGroupe(sessionManager.getId(), new MyRequest.recupGroupeCallback() {
            @Override
            public void onSuccess(String nom, int nbGroupe) {
                groupeListe.add(nom);
                lv_groupe.setAdapter(groupeAdapter);
            }
        });

        lv_groupe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                membreListe.clear();
                lv_membres.setAdapter(null);
                Object nom = parent.getItemAtPosition(position);
                NOM = nom.toString();
                request.recupMembres(NOM, new MyRequest.recupMembresCallback() {
                    @Override
                    public void onSuccess(String pseudo, int nbGroupe) {
                        membreListe.add(pseudo);
                        lv_membres.setAdapter(membreAdapter);
                    }
                });
            }
        });

        btn_quitterGroupe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.quitterGroupe(NOM, sessionManager.getId(), new MyRequest.quitterGroupeCallback() {
                    @Override
                    public void onSucces(String message) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        groupeListe.clear();
                        lv_groupe.setAdapter(null);
                        membreListe.clear();
                        lv_membres.setAdapter(null);
                        request.recupGroupe(sessionManager.getId(), new MyRequest.recupGroupeCallback() {
                            @Override
                            public void onSuccess(String nom, int nbGroupe) {
                                groupeListe.add(nom);
                                lv_groupe.setAdapter(groupeAdapter);
                            }
                        });
                    }
                });
            }
        });


        btn_ajouterMembres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.show();
            }
        });
    }


}

































