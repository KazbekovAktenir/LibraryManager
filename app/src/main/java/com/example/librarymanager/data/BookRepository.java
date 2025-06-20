package com.example.librarymanager.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookRepository {

    private final BookDao bookDao;
    private final ExecutorService executorService;

    public BookRepository(Application application) {
        BookDatabase db = BookDatabase.getInstance(application);
        bookDao = db.bookDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Book>> getAllBooks() {
        return bookDao.getAllBooks();
    }

    public LiveData<List<Book>> getBooksByStatus(String status) {
        return bookDao.getBooksByStatus(status);
    }

    public LiveData<List<Book>> getReadBooks() {
        return bookDao.getBooksByStatus("READ");
    }

    public LiveData<List<Book>> searchBooks(String query) {
        return bookDao.searchBooks(query);
    }

    public LiveData<List<Book>> searchBooksByTitle(String title) {
        return bookDao.searchBooksByTitle(title);
    }

    public LiveData<Book> getBookByIsbn(String isbn) {
        return bookDao.getBookByIsbn(isbn);
    }

    public LiveData<List<Book>> getBooksByGenre(String genre) {
        return bookDao.getBooksByGenre(genre);
    }

    public LiveData<Book> getBookById(int id) {
        return bookDao.getBookById(id);
    }

    public LiveData<List<String>> getAllGenres() {
        return bookDao.getAllGenres();
    }

    public void insert(Book book) {
        executorService.execute(() -> bookDao.insert(book));
    }

    public void update(Book book) {
        executorService.execute(() -> bookDao.update(book));
    }

    public void delete(Book book) {
        executorService.execute(() -> bookDao.delete(book));
    }

    public LiveData<List<Book>> getRecommendedBooks(String genre) {
        return bookDao.getRecommendedBooks(genre);
    }
}
