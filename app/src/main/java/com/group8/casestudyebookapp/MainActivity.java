package com.group8.casestudyebookapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;



public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private GestureDetector gestureDetector;
    BottomSheetDialog dialog;
    private float x1, x2, y1, y2;
    private static int MIN_DISTANCE = 150;
    private String Tag = "MainActivity";

    RecyclerView listView, gridView;
    ConstraintLayout constraintLayout;

    Animation fadeIn;

    StorageReference storageReference;
    DatabaseReference databaseReference;

    DatabaseReference recentBookReference;

    Button browsePDF, uploadPDF;
    Uri pdfUri;

    EditText fileName, author, book_title;  // for the name of the file to be uploaded
    ImageButton readRecentBook, readPlayButton;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    private String urlName;

    ListAdapter adapter;
    GridAdapter gridAdapter;

    private int mTotalScrolled = 0;
    private int ScrollRanged = 0;

    private TextView status, timeAgo, recentBookTitle, recentAuthorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("ebooks");

        createDialog();

        this.gestureDetector = new GestureDetector(MainActivity.this, this);

        listView = findViewById(R.id.book_list);
        gridView = findViewById(R.id.book_grid_view);

        status = findViewById(R.id.status);
        timeAgo = findViewById(R.id.timeAgo);
        recentBookTitle = findViewById(R.id.recent_title);
        recentAuthorName = findViewById(R.id.recentAuthor);
        readRecentBook = findViewById(R.id.RreadBtn);
        readPlayButton = findViewById(R.id.playReadBtn);


        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        constraintLayout = findViewById(R.id.mainLayout);

        calendar = Calendar.getInstance();

        dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a");

        gridView.setLayoutManager(new GridLayoutManager(this,2));
        listView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Books> options =
                new FirebaseRecyclerOptions.Builder<Books>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("ebooks"), Books.class)
                        .build();

        adapter = new ListAdapter(options);
        listView.setAdapter(adapter);

        gridAdapter = new GridAdapter(options);
        gridView.setAdapter(gridAdapter);

        browsePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent=new Intent();
                                intent.setType("application/pdf");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent,"Select Pdf Files"),101);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        recentBookReference = FirebaseDatabase.getInstance().getReference("RecentBooks");

        recentBookReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String author = Objects.requireNonNull(snapshot.child("author").getValue()).toString();
                    String book_title = Objects.requireNonNull(snapshot.child("title").getValue()).toString();
                    Long dateRead = (Long) Objects.requireNonNull(snapshot.child("dateRead").getValue());
                    urlName = Objects.requireNonNull(snapshot.child("fileurl").getValue()).toString();

                    String text = TimeAgo.using(dateRead);
                    recentBookTitle.setText(book_title);
                    recentAuthorName.setText(author);
                    timeAgo.setText(text);
                    status.setText("Recently Read");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        readRecentBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, view_book.class);
                intent.putExtra("fileurl", urlName);
                intent.putExtra("filename", recentBookTitle.getText().toString());
                startActivity(intent);
            }
        });

        readPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, view_book.class);
                intent.putExtra("fileurl", urlName);
                intent.putExtra("filename", recentBookTitle.getText().toString());
                startActivity(intent);
            }
        });


        uploadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processupload(pdfUri);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101 && resultCode==RESULT_OK)
        {
            pdfUri=data.getData();
            fileName.setText(pdfUri.toString());
        }
    }
//
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        gridAdapter.startListening();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }



    public void processupload(Uri pdfUri)
    {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("File Uploading....!!!");
        pd.show();

        final StorageReference reference=storageReference.child("uploads/"+System.currentTimeMillis()+".pdf");
        reference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Books books=new Books();
                                books.setFileurl(uri.toString());
                                books.setFilename(fileName.getText().toString());
                                books.setAuthor(author.getText().toString());
                                books.setTitle(book_title.getText().toString());

                                date = dateFormat.format(calendar.getTime());

                                books.setDateAdded(date);
                                databaseReference.child(databaseReference.push().getKey()).setValue(books);

                                pd.dismiss();
                                Toast.makeText(getApplicationContext(),"File Uploaded",Toast.LENGTH_LONG).show();

                                fileName.setText("");
                                author.setText("");
                                book_title.setText("");
                                dialog.dismiss();

                            }
                        });

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        float percent=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        pd.setMessage("Uploaded :"+(int)percent+"%");
                    }
                });
    }
    // call book fragment
    public void callBookFragment(View v) {
        //start activity
        Intent intent = new Intent(this, BooksFragment.class);
        startActivity(intent);
    }

    public void showDialog(View v) {
        dialog.show();
    }

    public void onCustomToggleClick(View v) {
        //on off toggle
        boolean checked = ((ToggleButton) v).isChecked();
        if (checked) {
            viewVisibleAnimator(gridView);
            viewGoneAnimator(listView);
        } else {
            viewVisibleAnimator(listView);
            viewGoneAnimator(gridView);
        }
    }

    private void viewGoneAnimator(final View view) {
        view.animate()
                .translationX(view.getWidth())
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    private void viewVisibleAnimator(final View view) {
        view.animate()
                .translationX(0)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }
                });
    }



    private void createDialog(){

        dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);

        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_upload_pdf,
                (ViewGroup) findViewById(R.id.upload_btn_dialog));


        fileName = view.findViewById(R.id.Input_PDF_File);
        browsePDF = view.findViewById(R.id.browse_pdf);
        uploadPDF = view.findViewById(R.id.upload_pdf_btn);
        author = view.findViewById(R.id.Input_Author);
        book_title = view.findViewById(R.id.Input_Book_Title);

        // make dialog background transparent
        dialog.setContentView(view);


    }




    @Override
    public boolean onTouchEvent(MotionEvent event){

        gestureDetector.onTouchEvent(event);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();

                float valueY = y2 - y1;

                if(Math.abs(valueY)> MIN_DISTANCE){
                    if(y2 < y1){
                          dialog.show();
                    }
                }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    // getFadeTransition

}