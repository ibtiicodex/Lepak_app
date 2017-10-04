package com.codextech.ibtisam.lepak_app.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.app.Prefs;
import com.codextech.ibtisam.lepak_app.model.Book;
import com.codextech.ibtisam.lepak_app.realm.RealmController;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HP on 9/27/2017.
 */

public class BooksAdapter extends RealmRecyclerViewAdapter<Book> {

    final Context context;
    private Realm realm;
    private LayoutInflater inflater;

    public BooksAdapter(Context context) {

        this.context = context;
    }

    // create new views (invoked by the layout manager)
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_books, parent, false);
        return new CardViewHolder(view);
    }

    // replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        realm = RealmController.getInstance().getRealm();

        // get the article
        final Book book = getItem(position);
        // cast the generic view holder to our specific one
        final CardViewHolder holder = (CardViewHolder) viewHolder;

        // set the title and the snippet
        holder.textTitle.setText(book.getTitle());
        holder.textAuthor.setText(book.getAuthor());
        holder.textNumber.setText(book.getNumber());
        holder.textPrice.setText(book.getPrice());
        holder.textLocation.setText(book.getLocation());
        // holder.textDescription.setText(book.getDescription());

        // load the background image
//        if (book.getImageUrl() != null) {
//            Glide.with(context)
//                    .load(book.getImageUrl().replace("https", "http"))
//                    .asBitmap()
//                    .fitCenter()
//                    .into(holder.imageBackground);
//        }

        //remove single match from realm
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                RealmResults<Book> results = realm.where(Book.class).findAll();

                // Get the book title to show it in toast message
                Book b = results.get(position);
                String title = b.getTitle();

                // All changes to data must happen in a transaction
                realm.beginTransaction();

                // remove single match
                results.remove(position);
                realm.commitTransaction();

                if (results.size() == 0) {
                    Prefs.with(context).setPreLoad(false);
                }

                notifyDataSetChanged();

                Toast.makeText(context, title + " is removed from Realm", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //update single match from realm
        holder.card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//
//                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View content = inflater.inflate(R.layout.edit_item, null);
//                final EditText editTitle = (EditText) content.findViewById(R.id.title);
//                final EditText editAuthor = (EditText) content.findViewById(R.id.author);
//                final EditText editNumber = (EditText) content.findViewById(R.id.number);
//                final EditText editPrice = (EditText) content.findViewById(R.id.price);
//                final EditText editLocation = (EditText) content.findViewById(R.id.Locations);
//
//                //  final EditText editThumbnail = (EditText) content.findViewById(R.id.thumbnail);
//
//                editTitle.setText(book.getTitle());
//                editAuthor.setText(book.getAuthor());
//                editNumber.setText(book.getNumber());
//                editPrice.setText(book.getPrice());
//                editLocation.setText(book.getLocation());
//                // editThumbnail.setText(book.getImageUrl());
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setView(content)
//                        .setTitle("Edit Ticket")
//                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                RealmResults<Book> results = realm.where(Book.class).findAll();
//
//                                realm.beginTransaction();
//                                results.get(position).setAuthor(editAuthor.getText().toString());
//                                results.get(position).setTitle(editTitle.getText().toString());
//                                results.get(position).setTitle(editNumber.getText().toString());
//                                results.get(position).setTitle(editPrice.getText().toString());
//                                results.get(position).setTitle(editLocation.getText().toString());
//                                // results.get(position).setImageUrl(editThumbnail.getText().toString());
//
//                                realm.commitTransaction();
//
//                                notifyDataSetChanged();
//                            }
//                        })
//                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                AlertDialog dialog = builder.create();
//                dialog.show();
            }
        });
    }

    // return the size of your data set (invoked by the layout manager)
    public int getItemCount() {

        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public CardView card;
        public TextView textTitle;
        public TextView textAuthor;
        public TextView textNumber;
        public TextView textPrice;
        public TextView textLocation;
        //  public ImageView imageBackground;

        public CardViewHolder(View itemView) {
            // standard view holder pattern with Butterknife view injection
            super(itemView);

            card = (CardView) itemView.findViewById(R.id.card_books);
            textTitle = (TextView) itemView.findViewById(R.id.text_books_title);
            textAuthor = (TextView) itemView.findViewById(R.id.text_books_author);
            textNumber = (TextView) itemView.findViewById(R.id.Dnumber);
            textPrice = (TextView) itemView.findViewById(R.id.Dprice);
            textLocation = (TextView) itemView.findViewById(R.id.Dlocation);
            // textDescription = (TextView) itemView.findViewById(R.id.text_books_description);
            //imageBackground = (ImageView) itemView.findViewById(R.id.image_background);
        }
    }
}
