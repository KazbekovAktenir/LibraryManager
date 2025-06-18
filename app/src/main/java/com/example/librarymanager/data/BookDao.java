package com.example.librarymanager.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

@Dao
public interface BookDao {
    @Insert
    void insert(Book book);

    @Update
    void update(Book book);

    @Delete
    void delete(Book book);

    @Query("SELECT * FROM books ORDER BY title ASC")
    LiveData<List<Book>> getAllBooks();

    @Query("SELECT * FROM books WHERE id = :id")
    LiveData<Book> getBookById(int id);

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' OR genre LIKE '%' || :query || '%'")
    LiveData<List<Book>> searchBooks(String query);

    @Query("SELECT * FROM books WHERE title LIKE '%' || :title || '%'")
    LiveData<List<Book>> searchBooksByTitle(String title);

    @Query("SELECT * FROM books WHERE isbn = :isbn")
    LiveData<Book> getBookByIsbn(String isbn);

    @Query("SELECT * FROM books WHERE status = :status")
    LiveData<List<Book>> getBooksByStatus(String status);

    @Query("SELECT DISTINCT genre FROM books ORDER BY genre ASC")
    LiveData<List<String>> getAllGenres();

    @Query("SELECT * FROM books WHERE genre = :genre")
    LiveData<List<Book>> getBooksByGenre(String genre);
}
