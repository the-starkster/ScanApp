package com.example.scanner;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button scanButton = findViewById(R.id.button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Setup Scan Options
                ScanOptions options = new ScanOptions();
                options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
                options.setPrompt("");
                options.setCameraId(0);  // Use a specific camera of the device
                options.setBeepEnabled(false);
                options.setBarcodeImageEnabled(true);
                options.setOrientationLocked(false);
                barcodeLauncher.launch(options);
            }

            // Setup intent for Scan
            private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    Toast.makeText(MainActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                }
            });

        });

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

}