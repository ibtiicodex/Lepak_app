package com.codextech.ibtisam.lepak_app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.adapters.BooksAdapter;
import com.codextech.ibtisam.lepak_app.adapters.RealmBooksAdapter;
import com.codextech.ibtisam.lepak_app.app.Prefs;
import com.codextech.ibtisam.lepak_app.model.Book;
import com.codextech.ibtisam.lepak_app.realm.RealmController;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class AllTicketsActivity extends AppCompatActivity {
    private final static String TAG = "AllTicketsActivity";
    private BooksAdapter adapter;
    private Realm realm;
    private LayoutInflater inflater;
//    private FloatingActionButton fab;
    private RecyclerView recycler;
    String messag;
    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tickets);
        System.out.println(currentDateTimeString);
//        fab = (FloatingActionButton) findViewById(R.id.fab);
        recycler = (RecyclerView) findViewById(R.id.recycler);

        //get realm instance
        this.realm = RealmController.with(this).getRealm();

        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupRecycler();

        if (!Prefs.with(this).getPreLoad()) {
            setRealmData();
        }

        // refresh the realm instance
        RealmController.with(this).refresh();
        // get all persisted objects
        // create the helper adapter and notify data set changes
        // changes will be reflected automatically
        setRealmAdapter(RealmController.with(this).getBooks());

        Toast.makeText(this, "Press card item for edit, long press to remove item", Toast.LENGTH_LONG).show();


        //add new item
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                inflater = AllTicketsActivity.this.getLayoutInflater();
//                View content = inflater.inflate(R.layout.edit_item, null);
////                final EditText editTitle = (EditText) content.findViewById(R.id.title);
////                final EditText editAuthor = (EditText) content.findViewById(R.id.author);
////                final EditText editNumber = (EditText) content.findViewById(R.id.number);
////                final EditText editPrice = (EditText) content.findViewById(R.id.price);
////                final EditText editLocation = (EditText) content.findViewById(R.id.Locations);
//
//
//                Intent intent = getIntent();
//                final String mess = intent.getStringExtra(HomeActivity.EXTRA_MESSAGE);
//                Book book = new Book();
//                //book.setId(RealmController.getInstance().getBooks().size() + 1);
//                book.setId(RealmController.getInstance().getBooks().size() + System.currentTimeMillis());
//                book.setTitle("Ali");
//                book.setAuthor(currentDateTimeString);
//                book.setNumber(mess);
//                book.setPrice("20");
//                book.setLocation("70");
//                // book.setImageUrl(editThumbnail.getText().toString());
//
////                                if (editTitle.getText() == null || editTitle.getText().toString().equals("") || editTitle.getText().toString().equals(" ")) {
////                                    Toast.makeText(AllTicketsActivity.this, "Entry not saved, missing title", Toast.LENGTH_SHORT).show();
////                                } else
//                {
//                    // Persist your data easily
//                    realm.beginTransaction();
//                    realm.copyToRealm(book);
//                    realm.commitTransaction();
//
//                    adapter.notifyDataSetChanged();
//
//                    // scroll the recycler view to bottom
//                    recycler.scrollToPosition(RealmController.getInstance().getBooks().size() - 1);
//                }
//            }
//
//
//        });

    }

    public void setRealmAdapter(RealmResults<Book> books) {

        RealmBooksAdapter realmAdapter = new RealmBooksAdapter(this.getApplicationContext(), books, true);
        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }

    private void setupRecycler() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recycler.setHasFixedSize(true);

        // use a linear layout manager since the cards are vertically scrollable
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);

        // create an empty adapter and add it to the recycler view
        adapter = new BooksAdapter(this);
        recycler.setAdapter(adapter);
    }

    private void setRealmData() {

        ArrayList<Book> books = new ArrayList<>();

       /* Book book = new Book();
        book.setId(1 + System.currentTimeMillis());
        book.setAuthor("Reto Meier");
        book.setTitle("Android 4 Application Development");

        books.add(book);
*/
       /* book = new Book();
        book.setId(2 + System.currentTimeMillis());
        book.setAuthor("Itzik Ben-Gan");
        book.setTitle("Microsoft SQL Server 2012 T-SQL Fundamentals");
        book.setImageUrl("http://api.androidhive.info/images/realm/2.png");
        books.add(book);

        book = new Book();
        book.setId(3 + System.currentTimeMillis());
        book.setAuthor("Magnus Lie Hetland");
        book.setTitle("Beginning Python: From Novice To Professional Paperback");
        book.setImageUrl("http://api.androidhive.info/images/realm/3.png");
        books.add(book);

        book = new Book();
        book.setId(4 + System.currentTimeMillis());
        book.setAuthor("Chad Fowler");
        book.setTitle("The Passionate Programmer: Creating a Remarkable Career in Software Development");
        book.setImageUrl("http://api.androidhive.info/images/realm/4.png");
        books.add(book);

        book = new Book();
        book.setId(5 + System.currentTimeMillis());
        book.setAuthor("Yashavant Kanetkar");
        book.setTitle("Written Test Questions In C Programming");
        book.setImageUrl("http://api.androidhive.info/images/realm/5.png");
        books.add(book);*/


        for (Book b : books) {
            // Persist your data easily
            realm.beginTransaction();
            realm.copyToRealm(b);
            realm.commitTransaction();
        }

        Prefs.with(this).setPreLoad(true);

    }
}