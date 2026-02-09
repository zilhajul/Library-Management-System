package com.example.library;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> implements Filterable {

    private Context context;
    private List<Book> bookList;
    private String userRole;
    private String email;
    Account account;



    // Constractor
    public BookAdapter(Context context, List<Book> bookList, String userRole, String email) {
        this.context = context;
        this.bookList = bookList;
        this.userRole = userRole;
        this.email = email;
    }
// SEARCH FILTER Start
    @Override
    public Filter getFilter() {
        return bookFilter;
    }
private Filter bookFilter = new Filter() {
    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {

        List<Book> filteredList = new ArrayList<>();

        if (charSequence == null || charSequence.length() == 0) {

            filteredList.addAll(bookList);

        }else {
            String filterPattern = charSequence.toString().toLowerCase().trim();

            for (Book book : bookList) {
                if (book.getName().toLowerCase().contains(filterPattern)) {
                    filteredList.add(book);
                }
                else if (book.getAuthor().toLowerCase().contains(filterPattern)) {
                    filteredList.add(book);
                }
            }


        }

        FilterResults results = new FilterResults();
        results.values = filteredList;


        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        bookList.clear();
        bookList.addAll((List) filterResults.values);
        notifyDataSetChanged();

    }
};  // Search filter end

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booklibrarian, parent, false);



        return new BookViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        //Taking the position from the list
        Book book = bookList.get(position);


        if (book.getBorrowDate() != null && !book.getBorrowDate().isEmpty()) {
            holder.tvBorrowDate.setVisibility(View.VISIBLE);
            holder.tvBorrowDate.setText("Borrowed on: " + book.getBorrowDate());
        } else {
            holder.tvBorrowDate.setVisibility(View.GONE);
        }


        if (userRole.equalsIgnoreCase("Librarian")) {
            // For librarian only show delete and issue buttons

            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnIssue.setVisibility(View.VISIBLE);
            holder.btnBorrow.setVisibility(View.GONE);
            holder.btnReturn.setVisibility(View.GONE);
        } else {
            // For student only show borrow and return buttons

            holder.btnDelete.setVisibility(View.GONE);
            holder.btnIssue.setVisibility(View.GONE);
            holder.btnBorrow.setVisibility(View.VISIBLE);
            holder.btnReturn.setVisibility(View.VISIBLE);
        }

        // Set name and author from database
        holder.name.setText(book.getName());
        holder.author.setText(book.getAuthor());

        // Getting image url
        String imageUrl = "http://192.168.1.196/library_system/uploads/" + book.getImagePath();

        // Set image with Glide
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.loading_icon) // For loading time
                .error(R.drawable.error_icon)
                .into(holder.image);

        Log.d("IMAGE_URL", "Full Path: " + imageUrl);

        holder.btnDelete.setOnClickListener(v -> {
            // It will show a dialog box to confirm the deletion

            new AlertDialog.Builder(context)
                    .setTitle("Delete Book")
                    .setMessage("Are you sure you want to delete this book?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // সার্ভার থেকে ডিলিট করার মেথড কল
                        deleteBookFromServer(book.getId(), position);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        holder.btnBorrow.setOnClickListener(v -> {
            // It show a dialog box to confirm the borrowing

            new AlertDialog.Builder(context)
                    .setTitle("Borrow Book")
                    .setMessage("Are you sure you want to borrow '" + book.getName() + "'?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        String studentId = email;
                        borrowBookFromServer(studentId, book.getId());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        //borrow button will be hide if book is already borrowed

        if (book.getBorrowDate() != null && !book.getBorrowDate().isEmpty()) {
            holder.btnBorrow.setVisibility(View.GONE);
        }

        holder.btnReturn.setOnClickListener(v -> {
            String studentId = email;
            returnBook(studentId, book.getId(), position);
        });



    }



    @Override
    public int getItemCount() {
        return bookList.size();
    }


    // ViewHolder class (it collect ID)
    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView name, author;
        ImageView image;
        Button btnDelete;
        Button btnIssue;
        Button btnBorrow;
        Button btnReturn;
        TextView tvBorrowDate;


        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.bookName);
            author = itemView.findViewById(R.id.authorName);
            image = itemView.findViewById(R.id.bookImage);
            tvBorrowDate = itemView.findViewById(R.id.tvBorrowDate);

            btnDelete = itemView.findViewById(R.id.deleteButton);
            btnBorrow = itemView.findViewById(R.id.borrowButton);
            btnReturn = itemView.findViewById(R.id.returnButton);
            btnIssue = itemView.findViewById(R.id.issueButton);
        }
    }

    private void deleteBookFromServer(String id, int position) {
        //This methode will delete the book from the server

        String url = "http://192.168.1.196/library_system/delete_book.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equals("success")) {
                        // ১. মেইন লিস্ট থেকে ডাটা রিমুভ করা
                        bookList.remove(position);
                        // ২. অ্যাডাপ্টারকে জানানো যে ডাটা কমেছে (অ্যানিমেশনের জন্য)
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, bookList.size());

                        Toast.makeText(context, "Book deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id); // ডাটাবেসের আইডি পাঠাচ্ছি
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    private void borrowBookFromServer(String studentId, String bookId) {
        //This methode will borrow the book from the server

        String url = "http://192.168.1.196/library_system/borrow_book.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equals("success")) {
                        Toast.makeText(context, "Book Borrowed Successfully!", Toast.LENGTH_SHORT).show();
                    } else if (response.trim().equals("already_borrowed")) {

                        Toast.makeText(context, "You have already borrowed this book!", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(context, " Error!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("student_id", studentId);
                params.put("book_id", bookId);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    private void returnBook(String studentId, String bookId, int position) {

        //This method perform the delete query on borrowed book table from the server

        String url = "http://192.168.1.196/library_system/return_book.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equals("success")) {
                        Toast.makeText(context, "Book Returned Successfully!", Toast.LENGTH_SHORT).show();

                        bookList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, bookList.size());
                    } else {
                        Toast.makeText(context, "Failed: " + response, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("student_id", studentId); //
                params.put("book_id", bookId);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

}
