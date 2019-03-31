package arocher.fr.topmap.controler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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

public class MesAmisActivity extends AppCompatActivity {

    private ListView lv_amis;
    private MyRequest request;
    private SessionManager sessionManager;
    private EditText inputPseudo;
    private String pseudoAmi;
    private View LASTVIEWAMI;
    private String PSEUDO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_amis);

        // INITIALISATION

            // Initialisation de SessionManager pour récupérer les informations de l'utilisateur connecté
            sessionManager = new SessionManager(this);

            // Récupération des boutons du xml
            Button btn_ajouterAmi = findViewById(R.id.btn_ajouterAmi);
            final Button btn_supprimerAmi = findViewById(R.id.btn_supprimerAmi);
            // Récupération des ListView du xml
            lv_amis = findViewById(R.id.lv_amis);

            // Initialisation de queue et request pour les requetes
            RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
            request = new MyRequest(this, queue);

            // Initialisation des boutons non dispo
            btn_supprimerAmi.setEnabled(false);

        // INITIALISATION, CREATION ET PEUPLEMENT DES LISTVIEW

            // Déclaration et initialisation des Listes et des ArrayAdapter pour le contenu des ListView

            final List<String> amiListe = new ArrayList<>();
            final ArrayAdapter<String> amiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, amiListe);

            // Récupération des amis de l'utilisateur pour peupler le ListView lv_amis
            request.recupAmis(sessionManager.getId(), new MyRequest.recupAmisCallback() {
                @Override
                public void onSuccess(String pseudo, int nbAmis) {
                    amiListe.add(pseudo);
                    lv_amis.setAdapter(amiAdapter);
                }

                @Override
                public void estVide() {

                }
            });

            // Activation du bouton supprimer
            lv_amis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    btn_supprimerAmi.setEnabled(true);
                    if(LASTVIEWAMI != null) {
                        LASTVIEWAMI.setBackgroundColor(Color.rgb(247, 247, 247));
                    }
                    Object pseudo = parent.getItemAtPosition(position);
                    PSEUDO = pseudo.toString();
                    view.setBackgroundColor(Color.LTGRAY);
                    LASTVIEWAMI = view;
                }
            });

        // INITIALISATION ET CREATION DES BOITES DE DIALOGUE

            // CREATION DE LA BOITE DE DIALOGUE POUR SAISIR UN PSEUDO
            AlertDialog.Builder builderAmi = new AlertDialog.Builder(this);
            builderAmi.setTitle("Ajouter un ami");
            inputPseudo = new EditText(this);
            builderAmi.setView(inputPseudo);
            builderAmi.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pseudoAmi = inputPseudo.getText().toString();
                    request.ajouterAmi(pseudoAmi, sessionManager.getId(), new MyRequest.ajouterAmiCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            amiListe.add(pseudoAmi);
                            lv_amis.setAdapter(amiAdapter);
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            builderAmi.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            final AlertDialog ajoutAmi = builderAmi.create();

    // CREATION DE LA BOITE DE DIALOGUE POUR DEMANDER CONFIRMATION AVANT DE SUPPRIMER UN AMI
            AlertDialog.Builder builderConfirmationSupprAmi = new AlertDialog.Builder(this);
            builderConfirmationSupprAmi.setTitle("Voulez vous vraiment quitter le groupe selectionné ?");
            builderConfirmationSupprAmi.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    request.supprimerAmi(PSEUDO, sessionManager.getId(), new MyRequest.supprimerAmiCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            amiListe.clear();
                            lv_amis.setAdapter(null);
                            request.recupAmis(sessionManager.getId(), new MyRequest.recupAmisCallback() {
                                @Override
                                public void onSuccess(String pseudo, int nbAmis) {
                                    amiListe.add(pseudo);
                                    lv_amis.setAdapter(amiAdapter);
                                    btn_supprimerAmi.setEnabled(false);
                                }

                                @Override
                                public void estVide() {

                                }


                            });
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            builderConfirmationSupprAmi.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            final AlertDialog confirmationSupprAmi = builderConfirmationSupprAmi.create();


        // COMPORTEMENT ASSOCIE AUX BOUTONS

        // Bouton ajouter
        btn_ajouterAmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ajoutAmi.show();
            }
        });

        // Bouton supprimer
        btn_supprimerAmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationSupprAmi.show();
            }
        });

    }
}
