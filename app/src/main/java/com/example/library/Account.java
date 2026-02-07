package com.example.library;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Account extends AppCompatActivity {

    private List<Book> bookList;
    private RecyclerView rvrecyclerView;
    private BookAdapterLibrarian adapter;
    String studentId;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvrecyclerView = findViewById(R.id.rvBorrowedBooks);
        rvrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rvrecyclerView.setHasFixedSize(true);
        bookList = new ArrayList<>();



        studentId = getIntent().getStringExtra("email");
        role = getIntent().getStringExtra("user_role");


        loadBorrowedBooks(studentId);

        rvrecyclerView.setAdapter(adapter);

    }
    // MyBorrowedBooksActivity-তে
    private void loadBorrowedBooks(String studentId) {
        bookList.clear(); // Puron data remove korbe

        String url = "http://192.168.1.196/library_system/get_borrowed_books.php?student_id=" + studentId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Log.d("SERVER_RESPONSE", response); // Logcat-e check korun data ashche kina
                        JSONArray array = new JSONArray(response);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            // Nichey deya serial-ti apnar Book.java constructor-er sathe miliye nin
                            bookList.add(new Book(
                                    obj.getString("id"),
                                    obj.getString("book_name"),
                                    obj.getString("author_name"),
                                    "N/A",
                                    obj.getString("image_path"),
                                    obj.getString("borrow_date")
                            ));
                        }

                        // Adapter initialize ebong set kora
                        if (adapter == null) {
                            adapter = new BookAdapterLibrarian(this, bookList, role, studentId);
                            rvrecyclerView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSON_ERROR", "Parsing error: " + e.getMessage());
                    }
                },
                error -> Log.e("VOLLEY_ERROR", error.toString()));

        Volley.newRequestQueue(this).add(request);
    }
}