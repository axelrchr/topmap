package arocher.fr.topmap.controler;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import arocher.fr.topmap.R;
import arocher.fr.topmap.SessionManager;
import arocher.fr.topmap.VolleySingleton;
import arocher.fr.topmap.myrequest.MyRequest;

public class ConnexionActivity extends AppCompatActivity {

    // DECLARATION DES VARIABLES

        private TextInputLayout til_mail, til_password;
        private ProgressBar pb_loader;
        private Handler handler;
        private MyRequest request;
        private SessionManager sessionManager;

    /**
     * Constructeur appellé au lancement de l'activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        // INITIALISATION

            // Si l'utilisateur est dans le deja connecté alors sa passe l'étape de connexion
            sessionManager = new SessionManager(this);
            if(sessionManager.isLogged()) {
                Intent intent = new Intent(this, AccueilActivity.class);
                startActivity(intent);
                finish();
            }

            // Initialisation de queue et request pour les requetes
            RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
            request = new MyRequest(this, queue);
            handler = new Handler();

            // Récupération des TextInputLayout du xml
            til_mail = findViewById(R.id.til_mail_log);
            til_password = findViewById(R.id.til_password_log);
            // Récupération des boutons du xml
            Button btn_login = findViewById(R.id.btn_login);
            Button btn_register = findViewById(R.id.btn_register);
            // Récupération de la ProgressBar
            pb_loader = findViewById(R.id.pb_loader);

        Intent intent = getIntent();
        if(intent.hasExtra("REGISTER")) {
            Toast.makeText(this, intent.getStringExtra("REGISTER"), Toast.LENGTH_SHORT).show();
        }

        // COMPORTEMENT ASSOCIE AUX BOUTONS

            // Bouton créer un compte
            btn_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                }
            });

            // Bouton se connecter
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String mail = til_mail.getEditText().getText().toString().trim();
                    final String password = til_password.getEditText().getText().toString().trim();
                    pb_loader.setVisibility(View.VISIBLE);
                    if(mail.length() > 0 && password.length() > 0) {
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run() {
                                request.connexion(mail, password, new MyRequest.LoginCallback() {
                                    @Override
                                    public void onSuccess(String id, String mail, String pseudo) {
                                        pb_loader.setVisibility(View.GONE);
                                        sessionManager.insertUser(id, mail, pseudo);
                                        Intent intent = new Intent(getApplicationContext(), AccueilActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    @Override
                                    public void onError(String message) {
                                        pb_loader.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        },1000);
                    }
                    else {
                        pb_loader.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}
