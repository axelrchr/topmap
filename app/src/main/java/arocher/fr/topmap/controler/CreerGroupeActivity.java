package arocher.fr.topmap.controler;

import android.app.DownloadManager;
import android.hardware.camera2.TotalCaptureResult;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import arocher.fr.topmap.R;
import arocher.fr.topmap.SessionManager;
import arocher.fr.topmap.VolleySingleton;
import arocher.fr.topmap.myrequest.MyRequest;

public class CreerGroupeActivity extends AppCompatActivity {

    private Button btn_retour, btn_creer;
    private TextInputLayout til_groupeName;
    private ProgressBar pb_loader;
    private RequestQueue queue;
    private MyRequest request;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creer_groupe);

        btn_retour = (Button) findViewById(R.id.btn_retour);
        btn_creer = (Button) findViewById(R.id.btn_creer);

        til_groupeName = (TextInputLayout) findViewById(R.id.til_groupeName);

        pb_loader = (ProgressBar) findViewById(R.id.pb_loader);

        queue = VolleySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);
        sessionManager = new SessionManager(this);

        btn_creer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb_loader.setVisibility(View.VISIBLE);
                String nom = til_groupeName.getEditText().getText().toString().trim();
                if(nom.length() > 0) {
                    request.creerGroupe(nom, sessionManager.getId(), new MyRequest.CreerGroupeCallback() {
                        @Override
                        public void onSuccess(String message) {
                            pb_loader.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onError(String message) {
                            pb_loader.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    pb_loader.setVisibility(View.GONE);
                }
            }
        });
    }


    public void retour(View view) { this.finish(); }
}
