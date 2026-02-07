package com.example.library;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Book_Add extends AppCompatActivity implements View.OnClickListener {

    private EditText bookNameEditText;
    private EditText authorNameEditText;
    private EditText isbnNumberEditText;
    private Button submitButton;
    private Bitmap bitmap;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bookNameEditText = findViewById(R.id.bookNameEditTextId);
        authorNameEditText = findViewById(R.id.authorNameEditTextId);
        isbnNumberEditText = findViewById(R.id.isbnNumberEditTextId);
        submitButton = findViewById(R.id.submitButtonId);

        submitButton.setOnClickListener(this);
        imageView = findViewById(R.id.selectImageButtonId);
        imageView.setOnClickListener(this);


    }

    public String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos); // কোয়ালিটি ৭০% যাতে আপলোড ফাস্ট হয়
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.submitButtonId){

            String name = bookNameEditText.getText().toString();
            String author = authorNameEditText.getText().toString();
            String isbn = isbnNumberEditText.getText().toString();

            if (bitmap != null && !name.isEmpty()) {
                // ৩. এখানে ভলি মেথডটি কল হবে
                uploadBookToServer(name, author, isbn);
            } else {
                Toast.makeText(this, "সব তথ্য এবং ছবি দিন", Toast.LENGTH_SHORT).show();
            }



        }
        else if (view.getId() == R.id.selectImageButtonId){

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*"); // শুধু ইমেজ ফাইল দেখাবে
            startActivityForResult(intent, 100); // ১০০ হলো একটি রিকোয়েস্ট কোড


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                // সিলেক্ট করা ছবিটিকে Bitmap এ রূপান্তর
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap); // স্ক্রিনে প্রিভিউ দেখাবে
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private void uploadBookToServer(String name, String author, String isbn) {
        String url = "http://192.168.1.196/library_system/add_book.php";

        // ছবিটিকে String বানিয়ে ফেলা
        final String imageString = bitmapToString(bitmap);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if(response.equals("Success")) {
                        bookNameEditText.setText("");
                        authorNameEditText.setText("");
                        isbnNumberEditText.setText("");

                        Toast.makeText(this, "Book Added!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("book_name", name);
                params.put("author_name", author);
                params.put("isbn", isbn);
                params.put("image", imageString); // ইমেজ ডাটা পাঠানো
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}