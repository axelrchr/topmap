package arocher.fr.topmap.controler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

    private ListView lv_groupe, lv_membres;
    private Button btn_ajouterMembres, btn_quitterGroupe;
    private String NOM = "";

    private RequestQueue queue;
    private MyRequest request;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mes_groupes);

        queue = VolleySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        sessionManager = new SessionManager(this);

        btn_ajouterMembres = (Button) findViewById(R.id.btn_ajouterMembres);
        btn_quitterGroupe = (Button) findViewById(R.id.btn_quitterGroupe);

        lv_groupe = (ListView) findViewById(R.id.lv_groupe);
        lv_membres = (ListView) findViewById(R.id.lv_membres);

        final List<String> groupeListe = new ArrayList<String>();
        final ArrayAdapter<String> groupeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groupeListe);

        final List<String> membreListe = new ArrayList<String>();
        final ArrayAdapter<String> membreAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, membreListe);


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
    }
}
