package com.yashraj.quizzy;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class QuizActivity extends AppCompatActivity {

    DatabaseReference databaseReference, reference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String uid = "", email_str = "", str_choosed_option;
    TextView question, option_selected, t, points_text;
    Button optA, optB, optC;
    CountDownTimer timer;
    Boolean isClicked = false;
    int points=20, a, b, c;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizactivity);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("user_database");
        reference = FirebaseDatabase.getInstance().getReference().child("user_pool");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        uid = mUser.getUid();
        email_str = mUser.getEmail();

        question = findViewById(R.id.question);
        optA = (Button) findViewById(R.id.option_A);
        optB = (Button) findViewById(R.id.option_B);
        optC = (Button) findViewById(R.id.option_C);
        option_selected = findViewById(R.id.option_Selected);
        points_text = findViewById(R.id.points);

        HashMap<String, Object> mapdata = new HashMap<>();
        mapdata.put("email", email_str);
        mapdata.put("uid", uid);
        mapdata.put("points", points);

        databaseReference.child(mUser.getUid()).updateChildren(mapdata).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(QuizActivity.this, "Changes Saved Successfully!", Toast.LENGTH_SHORT).show();

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), "Error:" + error, Toast.LENGTH_LONG).show();
                }
            }
        });
        final HashMap<String,Object> resultmap=new HashMap<>();


        optA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optB.setEnabled(false);
                optC.setEnabled(false);
                optA.setEnabled(false);
//                option_selected.setVisibility(View.VISIBLE);
//                option_selected.setText("Selected Option is " + optA.getText().toString());
//                str_choosed_option = option_selected.getText().toString();
                isClicked = true;
                Log.i("TAG", "onClick: " + str_choosed_option);
                resultmap.put("a",a=a+1);
                reference.updateChildren(resultmap);
                points=points-10;
                databaseReference.child(uid).child("points").setValue(points);
            }
        });

        optB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optB.setEnabled(false);
                optA.setEnabled(false);
                optC.setEnabled(false);
                isClicked = true;
//                option_selected.setVisibility(View.VISIBLE);
//                option_selected.setText("Selected Option is " + optB.getText().toString());
//                str_choosed_option = option_selected.getText().toString();
                Log.i("TAG", "onClick: " + str_choosed_option);
                resultmap.put("b",b=b+1);
                reference.updateChildren(resultmap);
                points=points-10;
                databaseReference.child(uid).child("points").setValue(points);
            }
        });
        optC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClicked = true;
                optC.setEnabled(false);
                optB.setEnabled(false);
                optA.setEnabled(false);
//                option_selected.setVisibility(View.VISIBLE);
//                option_selected.setText("Selected Option is " + optC.getText().toString());
//                str_choosed_option = option_selected.getText().toString();
                Log.i("TAG", "onClick: " + str_choosed_option);
                resultmap.put("c",c=c+1);
                reference.updateChildren(resultmap);
                points=points-10;
                databaseReference.child(uid).child("points").setValue(points);
            }
        });

        reference.updateChildren(resultmap);
        databaseReference.child(uid).child("points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                points_text.setText(snapshot.getValue().toString()+" Points");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        timer(20);

    }

    public void timer(int seconds) {
        t = findViewById(R.id.timer);
        timer = new CountDownTimer(seconds * 1000 + 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                t.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                t.setText("TIME OUT");
                optA.setEnabled(false);
                optB.setEnabled(false);
                optC.setEnabled(false);
                option_selected.setVisibility(View.VISIBLE);
                option_selected.setText("Timed Out");
                sentoResultActivity();
            }
        }.start();
    }

    private void sentoResultActivity() {
        Intent intent = new Intent(QuizActivity.this, QuizActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(QuizActivity.this, MainActivity.class));
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return super.onCreateOptionsMenu(menu);

    }

}
