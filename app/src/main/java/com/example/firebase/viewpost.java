package com.example.firebase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class viewpost extends AppCompatActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {
    ImageView imageView3;
    ListView list;
    ArrayList<String> usernamess;
    ArrayAdapter adapter;
     FirebaseAuth mAuthe;
    TextView tv30;
    ArrayList<DataSnapshot> dataSnapshots;



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpost);
        mAuthe=FirebaseAuth.getInstance();

        list=findViewById(R.id.list);
        imageView3=findViewById(R.id.imageView3);
        tv30=findViewById(R.id.tv30);
        usernamess=new ArrayList<>();
        dataSnapshots=new ArrayList<>();
        adapter=new ArrayAdapter(this, android.R.layout.simple_list_item_1,usernamess);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        list.setOnItemLongClickListener(this);





        FirebaseDatabase.getInstance().getReference().child("my_users").child(mAuthe.getCurrentUser().getUid()).child("recieved_post").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                dataSnapshots.add(dataSnapshot);

                String fromwhere= (String) dataSnapshot.child("from_whom").getValue();

                usernamess.add(fromwhere);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;

                for (DataSnapshot snapshot : dataSnapshots) {

                    if (snapshot.getKey().equals(dataSnapshot.getKey())) {

                        dataSnapshots.remove(i);
                        usernamess.remove(i);

                    }

                    i++;

                }
                adapter.notifyDataSetChanged();
                imageView3.setImageResource(R.drawable.harsh);
                tv30.setText("Picture Description");

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DataSnapshot mydatasnapshot=dataSnapshots.get(position);
        String downloadlink=(String) mydatasnapshot.child("image_link").getValue();
        Picasso.get().load(downloadlink).into(imageView3);
        tv30.setText((String)mydatasnapshot.child( "image_description").getValue());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        FirebaseDatabase.getInstance().getReference().child("my_users").child(mAuthe.getCurrentUser().getUid()).child("recieved_post").child(dataSnapshots.get(position).getKey()).removeValue();
                        FirebaseStorage.getInstance().getReference().child("my_images").child((String) dataSnapshots.get(position).child("image_identifier").getValue()).delete();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        return false;
    }
}
