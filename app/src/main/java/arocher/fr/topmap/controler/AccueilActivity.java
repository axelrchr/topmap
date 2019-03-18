package arocher.fr.topmap.controler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.w3c.dom.Text;

import arocher.fr.topmap.R;
import arocher.fr.topmap.SessionManager;
import arocher.fr.topmap.VolleySingleton;
import arocher.fr.topmap.myrequest.MyRequest;

public class AccueilActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tv1, tv2;
    private Button btn_logout, btn_creerGroupe, btn_carte, btn_test, btn_mes_groupes;

    private RequestQueue queue;
    private MyRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_creerGroupe = (Button) findViewById(R.id.btn_creerGroupe);
        btn_carte = (Button) findViewById(R.id.btn_carte);
        btn_test = (Button) findViewById(R.id.btn_test);
        btn_mes_groupes = (Button) findViewById(R.id.btn_mes_groupes) ;

        queue = VolleySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        sessionManager = new SessionManager(this);
        if(sessionManager.isLogged())
        {
            String mail = sessionManager.getMail();
            String id = sessionManager.getId();
            String pseudo = sessionManager.getPseudo();
            tv1.setText("Bonjour " + pseudo);
            tv2.setText("Que voulez vous faire ?");
        }

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                Intent intent = new Intent(getApplicationContext(), ConnexionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_creerGroupe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreerGroupeActivity.class);
                startActivity(intent);
               // finish();
            }
        });

        btn_carte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CarteActivity.class);
                startActivity(intent);
                // finish();
            }
        });

        btn_mes_groupes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MesGroupesActivity.class);
                startActivity(intent);
            }
        });



        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}
