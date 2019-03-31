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
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import java.util.ArrayList;
import java.util.List;
import arocher.fr.topmap.R;
import arocher.fr.topmap.SessionManager;
import arocher.fr.topmap.VolleySingleton;
import arocher.fr.topmap.myrequest.MyRequest;

public class MesGroupesActivity extends AppCompatActivity {

    // DECLARATION DES VARIABLES

        private ListView lv_groupe, lv_membres;
        private String NOM;
        private String PSEUDO;
        private String PSEUDOAMI;
        private String pseudoAmi, nomGroupe;
        private MyRequest request;
        private SessionManager sessionManager;
        private EditText inputGroupe, inputPseudo;
        private View LASTVIEWGROUPE, LASTVIEWMEMBRE;
        private Spinner spinnerAmi;


    /**
     * Constructeur appellé au lancement de l'activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_groupes);

        // INITIALISATION

            // Initialisation de SessionManager pour récupérer les information de l'utilisateur connecté
            sessionManager = new SessionManager(this);

            // Récupération des boutons du xml
            final Button btn_ajouterMembres = findViewById(R.id.btn_ajouterMembres);
            Button btn_creerGroupe = findViewById(R.id.btn_creerGroupe);
            final Button btn_quitterGroupe = findViewById(R.id.btn_quitterGroupe);
            final Button btn_supprimerMembre = findViewById(R.id.btn_supprimerMembre);
            // Récupération des ListView du xml
            lv_groupe = findViewById(R.id.lv_groupe);
            lv_membres = findViewById(R.id.lv_membres);

            // Initialisation de queue et request pour les requetes
            RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
            request = new MyRequest(this, queue);

            // Initialisation des boutons non dispo
            btn_ajouterMembres.setEnabled(false);
            btn_supprimerMembre.setEnabled(false);
            btn_quitterGroupe.setEnabled(false);

        // INITIALISATION, CREATION ET PEUPLEMENT DES LISTVIEW

            // Déclaration et initialisation des Listes et des ArrayAdapter pour le contenu des ListView et spinner
            final List<String> amiListe = new ArrayList<>();
            final ArrayAdapter<String> amiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, amiListe);
            final List<String> groupeListe = new ArrayList<>();
            final ArrayAdapter<String> groupeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groupeListe);
            final List<String> membreListe = new ArrayList<>();
            final ArrayAdapter<String> membreAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, membreListe);

            // Récupération des groupes de l'utilisateur pour peupler le ListView lv_groupe
            request.recupGroupe(sessionManager.getId(), new MyRequest.recupGroupeCallback() {
                @Override
                public void onSuccess(String nom, int nbGroupe) {
                    groupeListe.add(nom);
                    lv_groupe.setAdapter(groupeAdapter);
                }

                @Override
                public void estVide() {
                    btn_quitterGroupe.setEnabled(false);
                }
            });

            // Récupération des membres du groupe selectionné dans lv_groupe pour peupler le ListView lv_membres
            lv_groupe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(LASTVIEWGROUPE != null) {
                        LASTVIEWGROUPE.setBackgroundColor(Color.rgb(247, 247, 247));
                    }
                    membreListe.clear();
                    lv_membres.setAdapter(null);
                    Object nom = parent.getItemAtPosition(position);
                    NOM = nom.toString();
                    request.recupMembres(NOM, new MyRequest.recupMembresCallback() {
                        @Override
                        public void onSuccess(String pseudo, String estChef, int nbGroupe) {
                            btn_quitterGroupe.setEnabled(true);
                            if(estChef.equals("1")) {
                                membreListe.add(pseudo + "   [Chef de groupe]");
                                if(sessionManager.getPseudo().equals(pseudo)) {
                                    btn_ajouterMembres.setEnabled(true);
                                    btn_supprimerMembre.setEnabled(true);
                                }
                            }
                            else{
                                if(sessionManager.getPseudo().equals(pseudo)) {
                                    btn_ajouterMembres.setEnabled(false);
                                    btn_supprimerMembre.setEnabled(false);
                                }
                                membreListe.add(pseudo);
                            }
                            lv_membres.setAdapter(membreAdapter);
                        }
                    });
                    view.setBackgroundColor(Color.LTGRAY);
                    LASTVIEWGROUPE = view;
                }
            });

            // Récupération du pseudo du membre selectionné
            lv_membres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(LASTVIEWMEMBRE != null) {
                        LASTVIEWMEMBRE.setBackgroundColor(Color.rgb(247, 247, 247));
                    }
                    Object pseudo = parent.getItemAtPosition(position);
                    PSEUDO = pseudo.toString();
                    view.setBackgroundColor(Color.LTGRAY);
                    LASTVIEWMEMBRE = view;
                }
            });

        // INITIALISATION ET CREATION DES BOITES DE DIALOGUE

            // CREATION DE LA BOITE DE DIALOGUE POUR SAISIR UN PSEUDO
            AlertDialog.Builder builderMembre = new AlertDialog.Builder(this);
            builderMembre.setTitle("Ajouter un membre depuis votre liste d'ami");
            spinnerAmi = new Spinner(this);
            request.recupAmis(sessionManager.getId(), new MyRequest.recupAmisCallback() {
                @Override
                public void onSuccess(String pseudo, int nbAmis) {
                    amiListe.add(pseudo);
                    amiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    spinnerAmi.setAdapter(amiAdapter);
                    spinnerAmi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            final Object pseudo = parent.getItemAtPosition(position);
                            PSEUDOAMI = String.valueOf(pseudo);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
                @Override
                public void estVide() {
                    spinnerAmi.setEnabled(false);
                }
            });
            builderMembre.setView(spinnerAmi);
            builderMembre.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    request.ajouterMembre(PSEUDOAMI, NOM, new MyRequest.ajouterMembreCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            membreListe.add(PSEUDOAMI);
                            lv_membres.setAdapter(membreAdapter);
                        }
                        @Override
                        public void onError(String message) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            builderMembre.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            final AlertDialog ajoutMembre = builderMembre.create();

            // CREATION DE LA BOITE DE DIALOGUE POUR SAISIR UN GROUPE
            AlertDialog.Builder builderGroupe = new AlertDialog.Builder(this);
            builderGroupe.setTitle("Creer un groupe");
            inputGroupe = new EditText(this);
            builderGroupe.setView(inputGroupe);
            builderGroupe.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nomGroupe = inputGroupe.getText().toString();
                    request.creerGroupe(nomGroupe, sessionManager.getId(), new MyRequest.CreerGroupeCallback() {
                        @Override
                        public void onSuccess(String message) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            groupeListe.add(nomGroupe);
                            lv_groupe.setAdapter(groupeAdapter);
                        }
                        @Override
                        public void onError(String message) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            builderGroupe.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            final AlertDialog ajoutGroupe = builderGroupe.create();

            // CREATION DE LA BOITE DE DIALOGUE POUR DEMANDER CONFIRMATION AVANT DE QUITTER UN GROUPE
            AlertDialog.Builder builderConfirmationQuitterGroupe = new AlertDialog.Builder(this);
            builderConfirmationQuitterGroupe.setTitle("Voulez vous vraiment quitter le groupe selectionné ?");
            builderConfirmationQuitterGroupe.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    request.quitterGroupe(NOM, sessionManager.getId(), new MyRequest.quitterGroupeCallback() {
                        @Override
                        public void onSucces(String message) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            groupeListe.clear();
                            lv_groupe.setAdapter(null);
                            membreListe.clear();
                            lv_membres.setAdapter(null);
                            btn_quitterGroupe.setEnabled(false);
                            btn_ajouterMembres.setEnabled(false);
                            btn_supprimerMembre.setEnabled(false);
                            request.recupGroupe(sessionManager.getId(), new MyRequest.recupGroupeCallback() {
                                @Override
                                public void onSuccess(String nom, int nbGroupe) {
                                    groupeListe.add(nom);
                                    lv_groupe.setAdapter(groupeAdapter);
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
            builderConfirmationQuitterGroupe.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            final AlertDialog confirmationQuitterGroupe = builderConfirmationQuitterGroupe.create();

        // CREATION DE LA BOITE DE DIALOGUE POUR DEMANDER CONFIRMATION AVANT DE SUPPRESSION DU MEMBRE
        AlertDialog.Builder builderConfirmationSupprMembre = new AlertDialog.Builder(this);
        builderConfirmationSupprMembre.setTitle("Voulez vous vraiment supprimer l'utilisateur selectionné ?");
        builderConfirmationSupprMembre.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request.supprimerMembre(PSEUDO, NOM, new MyRequest.supprimerMembreCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        membreListe.clear();
                        lv_membres.setAdapter(null);
                        request.recupMembres(NOM, new MyRequest.recupMembresCallback() {
                            @Override
                            public void onSuccess(String pseudo, String estChef, int nbMembres) {
                                if(estChef.equals("1")) {
                                    membreListe.add(pseudo + "   [Chef de groupe]");
                                }else{
                                    membreListe.add(pseudo);
                                }
                                lv_membres.setAdapter(membreAdapter);
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
        builderConfirmationSupprMembre.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog confirmationSupprMembre = builderConfirmationSupprMembre.create();

        // COMPORTEMENT ASSOCIE AUX BOUTONS

            // Bouton quitter groupe
            btn_quitterGroupe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   confirmationQuitterGroupe.show();
                }
            });

            // Bouton ajouter membre
            btn_ajouterMembres.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ajoutMembre.show();
                }
            });

            // Bouton creer groupe
            btn_creerGroupe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ajoutGroupe.show();
                }
            });

            // Bouton supprimer membre
        btn_supprimerMembre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationSupprMembre.show();
            }
        });

    }
}



































