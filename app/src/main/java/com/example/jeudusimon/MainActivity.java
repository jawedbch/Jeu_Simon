package com.example.jeudusimon;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    Button boutton_play, boutton_quitter;
    TextView topScoresText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        boutton_play = findViewById(R.id.buttonPlay);
        boutton_quitter = findViewById(R.id.buttonQuit);
        topScoresText = findViewById(R.id.tvScores);
        topScores();
    }

    public void Quitter(View v) {
        finish();
    }

    public void Jouer(View v) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
    }

    private void topScores() {
        SharedPreferences prefs = getSharedPreferences("highscores", MODE_PRIVATE);

        StringBuilder highScoresText = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            int score = prefs.getInt("score" + i, 0);
            String nom = prefs.getString("nom" + i, "");
            highScoresText.append((i + 1)).append(". ").append(nom).append(" : ").append(score).append("\n");
        }
        topScoresText.setText(highScoresText.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        topScores();
    }
}
