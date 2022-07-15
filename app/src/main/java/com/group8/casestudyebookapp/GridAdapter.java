package com.group8.casestudyebookapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//
//public class GridAdapter extends ArrayAdapter<Books> {
//
//    private Context context;
//    private int resource;
//
//    public GridAdapter(@NonNull Context context, int resource, ArrayList<Books> userArrayList) {
//        super(context, resource, userArrayList);
//        this.context = context;
//        this.resource = resource;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//
//        convertView = inflater.inflate(resource, parent, false);
//
//        TextView title = convertView.findViewById(R.id.book_title_grid);
//        TextView author = convertView.findViewById(R.id.book_author_grid);
//        TextView dateAdded = convertView.findViewById(R.id.book_date_added_grid);
//
//        title.setText(getItem(position).getTitle());
//        author.setText(getItem(position).getAuthor());
//        dateAdded.setText(getItem(position).getDateAdded());
//
//        return convertView;
//    }
//}


public class GridAdapter extends FirebaseRecyclerAdapter<Books,GridAdapter.myviewholder>{


    public GridAdapter(@NonNull FirebaseRecyclerOptions<Books> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final GridAdapter.myviewholder holder, int position, @NonNull final Books model) {
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
    public GridAdapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.book_grid_item,parent,false);
        return  new GridAdapter.myviewholder(view);
    }

    public class myviewholder extends RecyclerView.ViewHolder {
        TextView title,author,dateAdded;
        Button read_btn;

        public myviewholder(@NonNull View view) {
            super(view);

            title=view.findViewById(R.id.book_title_grid);
            author=view.findViewById(R.id.book_author_grid);
            dateAdded=view.findViewById(R.id.book_date_added_grid);
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



