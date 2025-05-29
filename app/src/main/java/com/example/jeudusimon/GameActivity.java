package com.example.jeudusimon;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.os.Handler;
import android.app.AlertDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GameActivity extends AppCompatActivity {
    boolean tour = false;
    int sequenceIndex = 0;
    TextView scoreTextView;
    List<Integer> sequence = new ArrayList<>();
    int score = 0;
    Button redButton, blueButton, greenButton, yellowButton;

    SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        blueButton = findViewById(R.id.buttonBlue);
        redButton = findViewById(R.id.buttonRed);
        greenButton = findViewById(R.id.buttonGreen);
        yellowButton = findViewById(R.id.buttonYellow);

        scoreTextView = findViewById(R.id.textViewScore);

        redButton.setOnClickListener(v -> boutonClick(0));
        blueButton.setOnClickListener(v -> boutonClick(1));
        greenButton.setOnClickListener(v -> boutonClick(2));
        yellowButton.setOnClickListener(v -> boutonClick(3));

        // Initialiser SoundManager et ajouter les sons
        soundManager = new SoundManager();
        soundManager.initSounds(this);
        soundManager.ajouterSon(0, R.raw.son1);
        soundManager.ajouterSon(1, R.raw.son2);
        soundManager.ajouterSon(2, R.raw.son3);
        soundManager.ajouterSon(3, R.raw.son4);
        soundManager.ajouterSon(4, R.raw.error);

        start();
    }

    private void start() {
        sequence.clear();
        score = 0;
        scoreTextView.setText(String.valueOf(score));
        tour = false;
        sequenceIndex = 0;
        genererSequence();
        jouerSequence();
    }

    private void genererSequence() {
        Random random = new Random();
        sequence.add(random.nextInt(4)); // Ajoutez une couleur aléatoire à la séquence
    }

    private void jouerSequence() {
        tour = false; // Ne laissez pas le joueur jouer pendant que la séquence est jouée
        new Handler().postDelayed(() -> {
            montrerSequence(0);
        }, 1000); // Ajoutez un délai avant de jouer la séquence
    }

    private void montrerSequence(final int index) {
        if (index < sequence.size()) {
            Button button = getBouton(sequence.get(index));
            soundManager.jouerSon(sequence.get(index)); // Jouer le son associé
            button.setAlpha(0.5f);
            new Handler().postDelayed(() -> {
                button.setAlpha(1f);
                new Handler().postDelayed(() -> {
                    montrerSequence(index + 1);
                }, 1000); // Délai de 1 seconde entre chaque bouton dans la séquence
            }, 500); // Temps pendant lequel le bouton est éclairé
        } else {
            tour = true;
            Toast.makeText(this, "À votre tour !", Toast.LENGTH_SHORT).show();
        }
    }

    private Button getBouton(int couleurIndx) {
        switch (couleurIndx) {
            case 0:
                return redButton;
            case 1:
                return blueButton;
            case 2:
                return greenButton;
            case 3:
                return yellowButton;
            default:
                return null;
        }
    }

    private void boutonClick(int couleurIndx) {
        if (!tour) return;
        soundManager.jouerSon(couleurIndx); // Jouer le son associé au bouton cliqué
        if (sequence.get(sequenceIndex) == couleurIndx) {
            sequenceIndex++;
            if (sequenceIndex >= sequence.size()) {
                score++;
                scoreTextView.setText(String.valueOf(score));
                genererSequence();
                sequenceIndex = 0;
                jouerSequence();
            }
        } else {
            soundManager.jouerSon(4); // Jouer le son d'erreur
            gameDialogue();
        }
    }

    private void gameDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over");
        builder.setMessage("Vous avez perdu. Entrez votre nom:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String playerName = input.getText().toString();
            sauvgarderScore(playerName, score);
            envoyerResultat(playerName, score);
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void sauvgarderScore(String playerName, int score) {
        SharedPreferences prefs = getSharedPreferences("highscores", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Récupérer les scores actuels
        int[] highScores = new int[3];
        String[] highNames = new String[3];
        for  (int i = 0; i < 3; i++) {
            highScores[i] = prefs.getInt("score" + i, 0);
            highNames[i] = prefs.getString("name" + i, "");
        }

        // Insérer le nouveau score si c'est un top 3
        for (int i = 0; i < 3; i++) {
            if (score > highScores[i]) {
                for (int j = 2; j > i; j--) {
                    highScores[j] = highScores[j - 1];
                    highNames[j] = highNames[j - 1];
                }
                highScores[i] = score;
                highNames[i] = playerName;
                break;
            }
        }

        // Sauvegarder les scores mis à jour
        for (int i = 0; i < 3; i++) {
            editor.putInt("score" + i, highScores[i]);
            editor.putString("name" + i, highNames[i]);
        }

        editor.apply();
    }

    private void envoyerResultat(String playerName, int score) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("PLAYER_NAME", playerName);
        resultIntent.putExtra("SCORE", score);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}

