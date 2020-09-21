package com.yashraj.quizzy;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResultActivity extends AppCompatActivity {
    DatabaseReference user_pool;
    TextView result_text;
    FirebaseAuth mAuth;
    String uid;
    int a,b,c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        result_text = findViewById(R.id.result);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        user_pool = FirebaseDatabase.getInstance().getReference().child("user_pool");

        user_pool.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                a= Integer.parseInt(snapshot.child("a").getValue().toString());
                b= Integer.parseInt(snapshot.child("b").getValue().toString());
                c= Integer.parseInt(snapshot.child("c").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(a>b && a>c){
            result_text.setText("Most selected option is Option A");
        }else if(b>a && b>c){
            result_text.setText("Most selected option is Option B");
        }else{
            result_text.setText("Most selected option is Option C");
        }

        Log.i("TAG", "onDataChange: "+a+"A ki value");

    }
}