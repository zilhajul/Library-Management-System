package com.example.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Librarian extends AppCompatActivity implements View.OnClickListener {

    private Button addBookButton, settingButton;
    RecyclerView recyclerView;
    BookAdapter adapter;
    List<Book> bookList;
    String role;
    SwipeRefreshLayout swipeRefreshLayout;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_librarian);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addBookButton = findViewById(R.id.addBookButtonId);
        settingButton = findViewById(R.id.settingButtonId);
        addBookButton.setOnClickListener(this);
        settingButton.setOnClickListener(this);

        recyclerView = findViewById(R.id.recyclerView);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        bookList = new ArrayList<>();

        role = getIntent().getStringExtra("user_role");

       loadBooksFromServer();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBooks();
            }
        });


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addBookButtonId){
            Intent intent = new Intent(Librarian.this, Book_Add.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.settingButtonId){

        }
    }

    private void loadBooksFromServer() {
        String url = "http://192.168.1.196/library_system/get_books.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            // মডেল ক্লাসে ডাটা ভরা
                            Book book = new Book(
                                    obj.getString("id"),
                                    obj.getString("book_name"),
                                    obj.getString("author_name"),
                                    obj.getString("isbn"),
                                    obj.getString("image_path"),
                                    ""
                            );
                            bookList.add(book);
                        }

                        adapter = new BookAdapter(this, bookList, role, email);
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }

    private void loadBooks() {

        bookList.clear();

        String url = "http://192.168.1.196/library_system/get_books.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {

                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            // মডেল ক্লাসে ডাটা ভরা
                            Book book = new Book(
                                    obj.getString("id"),
                                    obj.getString("book_name"),
                                    obj.getString("author_name"),
                                    obj.getString("isbn"),
                                    obj.getString("image_path"),
                                    ""
                            );
                            bookList.add(book);
                        }
                        // লিস্টে ডাটা আসার পর অ্যাডাপ্টার সেট করা
                        adapter = new BookAdapter(this, bookList, role, email);
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException e) { e.printStackTrace(); }

                    adapter.notifyDataSetChanged();

                },
                error -> Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
        swipeRefreshLayout.setRefreshing(false);


    }

}