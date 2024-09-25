package bsce20024.ahmad.firebaselogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button level1, level2, level3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        level1 = findViewById(R.id.btnlevel1);
        level2 = findViewById(R.id.btnlevel2);
        level3 = findViewById(R.id.btnlevel3);

        level1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Level1Activity level1act = new Level1Activity(getApplicationContext());
//                setContentView(level1act);
                Intent intent = new Intent(getApplicationContext(), Level1Activity.class);
                startActivity(intent);
            }
        });
        level2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(intent);
//                GameActivity gameView = new GameActivity(getApplicationContext());
//                setContentView(gameView);
            }
        });
        level3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Level3Activity level3act = new Level3Activity(getApplicationContext());
//                setContentView(level3act);
                Intent intent = new Intent(getApplicationContext(), Level3Activity.class);
                startActivity(intent);
                }
        });
    }
}