package com.group8.casestudyebookapp;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;


public class ListAdapter extends FirebaseRecyclerAdapter<Books,ListAdapter.myviewholder> {


    public ListAdapter(@NonNull FirebaseRecyclerOptions<Books> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final myviewholder holder, int position, @NonNull final Books model) {
        holder.title.setText(model.getTitle());

        holder.author.setText(model.getAuthor());
        holder.dateAdded.setText(model.getDateAdded());


        holder.read_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.read_btn.getContext(), view_book.class);
                intent.putExtra("title", model.getTitle());
                intent.putExtra("fileurl", model.getFileurl());

                RecentBookUpload(model.getTitle(),model.getAuthor(),model.getFileurl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.read_btn.getContext().startActivity(intent);

            }
        });


    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item,parent,false);
        return  new myviewholder(view);
    }

    public class myviewholder extends RecyclerView.ViewHolder
    {
        TextView title,author,dateAdded;
        Button read_btn;

        public myviewholder(@NonNull View view) {
            super(view);

            title=view.findViewById(R.id.book_title);
            author=view.findViewById(R.id.book_author);
            dateAdded=view.findViewById(R.id.book_date_added);
            read_btn=view.findViewById(R.id.read_btn);

        }
    }

    public void RecentBookUpload(String title, String author, String fileurl)
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RecentBooks");
        long timeInMillis = System.currentTimeMillis();
        databaseReference.child("title").setValue(title);
        databaseReference.child("author").setValue(author);
        databaseReference.child("fileurl").setValue(fileurl);
        databaseReference.child("dateRead").setValue(timeInMillis);

    }

}
