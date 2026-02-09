package com.example.library;

public class Book {
        private String id;
        private String name;
        private String author;
        private String isbn;
        private String imagePath;
        private String borrowDate;


        public Book(String id, String name, String author, String isbn, String imagePath, String borrowDate) {
            this.id = id;
            this.name = name;
            this.author = author;
            this.isbn = isbn;
            this.imagePath = imagePath;
            this.borrowDate = borrowDate;

        }


        public String getName() { return name; }
        public String getAuthor() { return author; }
        public String getIsbn() { return isbn; }
        public String getImagePath() { return imagePath; }
        public String getId(){return id;}

    public String getBorrowDate() {
            return borrowDate;
    }
}

