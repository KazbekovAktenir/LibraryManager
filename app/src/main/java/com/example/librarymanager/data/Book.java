package com.example.librarymanager.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "books")
public class Book {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String title;

    @NonNull
    private String author;

    private String genre;
    private String isbn;
    private String status; // "READING", "READ", "PLANNED"
    private String notes;

    private long addedDate;      // дата добавления
    private long lastReadDate;   // дата последнего чтения

    public Book(@NonNull String title, @NonNull String author, String genre, String status) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.status = status;
        this.addedDate = System.currentTimeMillis();
    }

    // --- Геттеры и Сеттеры ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull
    public String getTitle() { return title; }
    public void setTitle(@NonNull String title) { this.title = title; }

    @NonNull
    public String getAuthor() { return author; }
    public void setAuthor(@NonNull String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getAddedDate() { return addedDate; }
    public void setAddedDate(long addedDate) { this.addedDate = addedDate; }

    public long getLastReadDate() { return lastReadDate; }
    public void setLastReadDate(long lastReadDate) { this.lastReadDate = lastReadDate; }
}
