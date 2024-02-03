package com.example.scanner;


import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityLog";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Setup intent for Scan
        ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                String codiceFiscaleScanned = result.getContents(); // Il codice fiscale ottenuto dal QR Code
                //Toast.makeText(MainActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                queryTaxIdCode(codiceFiscaleScanned);
            }
        });

        // Setup Scan Options
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
        options.setPrompt("");
        options.setCameraId(0);  // Use a specific camera of the device
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);


        Button scanButton = findViewById(R.id.button);
        scanButton.setOnClickListener(view -> barcodeLauncher.launch(options));

    }

    //Squaring logo
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Get the width of the ImageView
            ImageView logoImageView = findViewById(R.id.imageView);

            // Set the height of the ImageView to match its width
            logoImageView.getLayoutParams().height = logoImageView.getWidth();
            logoImageView.requestLayout();
        }
    }


    //Controllo se il codice fiscale si trovan nel data base
    public void queryTaxIdCode(String codiceFiscaleScanned){

        db.collection("Users Data")
                .whereEqualTo("Tax ID Code", codiceFiscaleScanned)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // Nessun documento corrispondente trovato, mostra "Accesso negato"
                            Log.d(TAG, "Nessun documento corrispondente trovato per il codice fiscale: " + codiceFiscaleScanned);
                            Toast.makeText(MainActivity.this, "Accesso negato", Toast.LENGTH_LONG).show();
                        } else {
                            // Documenti corrispondenti trovati, mostra "Accesso consentito"
                            Log.d(TAG, "Documenti corrispondenti trovati per il codice fiscale: " + codiceFiscaleScanned);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            Toast.makeText(MainActivity.this, "Accesso consentito", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.w(TAG, "Errore nella ricerca dei dati per il codice fiscale: " + codiceFiscaleScanned, task.getException());
                        Toast.makeText(MainActivity.this, "Errore nella ricerca dei dati", Toast.LENGTH_LONG).show();
                    }
                });
    }
}