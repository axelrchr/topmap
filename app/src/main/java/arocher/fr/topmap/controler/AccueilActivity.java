package arocher.fr.topmap.controler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import arocher.fr.topmap.R;
import arocher.fr.topmap.SessionManager;

public class AccueilActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tv_mail;
    private Button btn_logout, btn_creerGroupe, btn_carte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        tv_mail = (TextView) findViewById(R.id.tv_mail);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_creerGroupe = (Button) findViewById(R.id.btn_creerGroupe);
        btn_carte = (Button) findViewById(R.id.btn_carte);

        sessionManager = new SessionManager(this);
        if(sessionManager.isLogged())
        {
            String mail = sessionManager.getMail();
            String id = sessionManager.getId();
            String pseudo = sessionManager.getPseudo();
            tv_mail.setText("Bonjour "+pseudo);
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
    }
}
