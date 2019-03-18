package arocher.fr.topmap.controler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.android.volley.RequestQueue;

import java.util.ArrayList;
import java.util.List;

import arocher.fr.topmap.R;
import arocher.fr.topmap.SessionManager;
import arocher.fr.topmap.VolleySingleton;
import arocher.fr.topmap.myrequest.MyRequest;

public class MesGroupesActivity extends AppCompatActivity {

    private ListView lv_groupe;
    private final List list_groupe = new ArrayList();

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

        lv_groupe = (ListView) findViewById(R.id.lv_groupe);
        final List<String> liste = new ArrayList<String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, liste);
        request.recupGroupe(sessionManager.getId(), new MyRequest.recupGroupeCallback() {
            @Override
            public void onSuccess(String nom, int nbGroupe) {
                liste.add(nom);
                lv_groupe.setAdapter(adapter);
            }
        });

    }
}
