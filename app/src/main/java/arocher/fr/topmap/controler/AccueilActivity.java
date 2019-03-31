package arocher.fr.topmap.controler;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.RequestQueue;

import arocher.fr.topmap.R;
import arocher.fr.topmap.SessionManager;
import arocher.fr.topmap.VolleySingleton;
import arocher.fr.topmap.myrequest.MyRequest;

public class AccueilActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private MyRequest request;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        TextView tv1 = findViewById(R.id.tv1);
        TextView tv2 = findViewById(R.id.tv2);
        Button btn_logout = findViewById(R.id.btn_logout);
        Button btn_carte = findViewById(R.id.btn_carte);
        Button btn_mes_groupes = findViewById(R.id.btn_mes_groupes);
        Button btn_amis = findViewById(R.id.btn_amis);

        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        sessionManager = new SessionManager(this);
        if(sessionManager.isLogged()) {
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

        btn_carte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CarteActivity.class);
                startActivity(intent);
            }
        });

        btn_mes_groupes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MesGroupesActivity.class);
                startActivity(intent);
            }
        });

        btn_amis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MesAmisActivity.class);
                startActivity(intent);
            }
        });
    }
}
