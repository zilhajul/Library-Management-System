package com.example.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Student extends AppCompatActivity {

    RecyclerView recyclerViewStudent;
    BookAdapter adapter;
    List<Book> bookList;
    String role;
    SwipeRefreshLayout swipeRefreshLayoutStudent;
    MaterialToolbar toolbar;
    private SearchView searchView;
    private String email;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewStudent = findViewById(R.id.recyclerViewStudent);

        recyclerViewStudent.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewStudent.setHasFixedSize(true);
        swipeRefreshLayoutStudent = findViewById(R.id.swipeRefreshLayoutStudent);
        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.searchId);

        bookList = new ArrayList<>();

        role = getIntent().getStringExtra("user_role");
        email = getIntent().getStringExtra("email");



        loadBooksFromServer();
       swipeRefreshLayoutStudent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {
               loadBooks();
           }
       });

       toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem item) {

               if (item.getItemId() == R.id.profileId){
                Intent intent = new Intent(Student.this, Account.class);
                intent.putExtra("email", email);
                intent.putExtra("user_role", role);
                startActivity(intent);
               }

               return true;
           }
       });

       searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           @Override
           public boolean onQueryTextChange(String s) {
               return false;
           }

           @Override
           public boolean onQueryTextSubmit(String s) {

               adapter.getFilter().filter(s);

               return true;
           }
       });


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
                        recyclerViewStudent.setAdapter(adapter);

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

                        adapter = new BookAdapter(this, bookList, role, email);
                        recyclerViewStudent.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter.notifyDataSetChanged();

                },
                error -> Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);

        swipeRefreshLayoutStudent.setRefreshing(false);


    }
}