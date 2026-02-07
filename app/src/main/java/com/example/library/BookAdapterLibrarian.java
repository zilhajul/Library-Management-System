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

public class BookAdapterLibrarian extends RecyclerView.Adapter<BookAdapterLibrarian.BookViewHolder> implements Filterable {

    private Context context;
    private List<Book> bookList;
    private String userRole;
    private String email;



    // কনস্ট্রাক্টর
    public BookAdapterLibrarian(Context context, List<Book> bookList, String userRole, String email) {
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

    // --- এইখানে মেথডটি বসবে ---
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        // ১. লিস্ট থেকে বর্তমান পজিশনের বইটিকে নেওয়া
        Book book = bookList.get(position);


        if (book.getBorrowDate() != null && !book.getBorrowDate().isEmpty()) {
            holder.tvBorrowDate.setVisibility(View.VISIBLE);
            holder.tvBorrowDate.setText("Borrowed on: " + book.getBorrowDate());
        } else {
            holder.tvBorrowDate.setVisibility(View.GONE);
        }


        if (userRole.equalsIgnoreCase("Librarian")) {
            // অ্যাডমিন হলে ডিলিট এবং ইস্যু দেখবে, কিন্তু বরো (Borrow) বাটন হাইড থাকবে
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnIssue.setVisibility(View.VISIBLE);
            holder.btnBorrow.setVisibility(View.GONE);
            holder.btnReturn.setVisibility(View.GONE);
        } else {
            // স্টুডেন্ট হলে ডিলিট এবং ইস্যু দেখবে না, শুধু বরো এবং ডিটেইলস দেখবে
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnIssue.setVisibility(View.GONE);
            holder.btnBorrow.setVisibility(View.VISIBLE);
            holder.btnReturn.setVisibility(View.VISIBLE);
        }

        // ২. টেক্সট ডাটা সেট করা
        holder.name.setText(book.getName());
        holder.author.setText(book.getAuthor());

        // ৩. ছবির লিঙ্ক তৈরি করা
        String imageUrl = "http://192.168.1.196/library_system/uploads/" + book.getImagePath();

        // ৪. Glide দিয়ে ছবি সেট করা
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.loading_icon) // লোড হওয়ার সময় যা দেখাবে
                .error(R.drawable.error_icon)
                .into(holder.image);

        Log.d("IMAGE_URL", "Full Path: " + imageUrl);

        holder.btnDelete.setOnClickListener(v -> {
            // ইউজারের কাছে কনফার্মেশন চাওয়া
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

        if (book.getBorrowDate() != null && !book.getBorrowDate().isEmpty()) {
            holder.btnBorrow.setVisibility(View.GONE);
        }



    }


    @Override
    public int getItemCount() {
        return bookList.size();
    }


    // ViewHolder ক্লাস (যা ID গুলো ধরে রাখে)
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
            name = itemView.findViewById(R.id.bookName); // item_book.xml এর ID
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
        String url = "http://192.168.1.196/library_system/borrow_book.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equals("success")) {
                        Toast.makeText(context, "Book Borrowed Successfully!", Toast.LENGTH_SHORT).show();
                    } else if (response.trim().equals("already_borrowed")) {
                        // ইউজারকে সতর্ক করা
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
}
