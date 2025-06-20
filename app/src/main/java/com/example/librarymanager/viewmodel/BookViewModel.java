package com.example.librarymanager.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.librarymanager.api.BookResponse;
import com.example.librarymanager.api.GoogleBooksService;
import com.example.librarymanager.data.Book;
import com.example.librarymanager.data.BookRepository;
import com.example.librarymanager.worker.ReadingReminderWorker;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookViewModel extends AndroidViewModel {

    private final BookRepository repository;
    private final LiveData<List<Book>> allBooks;
    private final WorkManager workManager;
    private final MutableLiveData<Book> isbnSearchedBook = new MutableLiveData<>();
    private final MutableLiveData<String> searchError = new MutableLiveData<>();

    public BookViewModel(Application application) {
        super(application);
        repository = new BookRepository(application);
        allBooks = repository.getAllBooks();
        workManager = WorkManager.getInstance(application);
    }

    public LiveData<List<Book>> getAllBooks() {
        return allBooks;
    }

    public LiveData<List<Book>> getReadBooks() {
        return repository.getBooksByStatus("READ");
    }

    public LiveData<List<Book>> getRecommendedBooks(String genre) {
        return repository.getRecommendedBooks(genre);
    }

    public LiveData<Book> getBookById(int id) {
        return repository.getBookById(id);
    }

    public LiveData<List<Book>> searchBooks(String query) {
        return repository.searchBooks(query);
    }

    public LiveData<List<Book>> searchBooksByTitle(String title) {
        return repository.searchBooksByTitle(title);
    }

    public LiveData<Book> getBookByIsbn(String isbn) {
        return repository.getBookByIsbn(isbn);
    }

    public LiveData<List<Book>> getBooksByStatus(String status) {
        return repository.getBooksByStatus(status);
    }

    public LiveData<List<String>> getAllGenres() {
        return repository.getAllGenres();
    }

    public LiveData<List<Book>> getBooksByGenre(String genre) {
        return repository.getBooksByGenre(genre);
    }

    public void insert(Book book) {
        repository.insert(book);
    }

    public void update(Book book) {
        repository.update(book);
    }

    public void delete(Book book) {
        repository.delete(book);
    }

    public void scheduleReadingReminder(Book book, long delayInMinutes) {
        Data inputData = new Data.Builder()
                .putString("book_title", book.getTitle())
                .build();

        OneTimeWorkRequest reminderWork = new OneTimeWorkRequest.Builder(ReadingReminderWorker.class)
                .setInputData(inputData)
                .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
                .build();

        workManager.enqueue(reminderWork);
    }

    public void cancelAllReminders() {
        workManager.cancelAllWork();
    }

    public LiveData<Book> searchBookByIsbn(String isbn) {
        isbnSearchedBook.setValue(null);
        searchError.setValue(null);

        String normalizedIsbn = normalizeIsbn(isbn);

        if (!isValidIsbn(normalizedIsbn)) {
            searchError.postValue("Invalid ISBN format. Please enter a valid ISBN-10 or ISBN-13.");
            return isbnSearchedBook;
        }

        repository.getBookByIsbn(normalizedIsbn).observeForever(book -> {
            if (book != null) {
                isbnSearchedBook.postValue(book);
            } else {
                searchGoogleBooksByIsbn(normalizedIsbn);
            }
        });

        return isbnSearchedBook;
    }

    public LiveData<String> getSearchError() {
        return searchError;
    }

    private String normalizeIsbn(String isbn) {
        return isbn.replaceAll("[^0-9X]", "").toUpperCase();
    }

    private boolean isValidIsbn(String isbn) {
        if (isbn == null || isbn.isEmpty()) return false;

        if (isbn.length() == 10) {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                int digit = Character.getNumericValue(isbn.charAt(i));
                sum += (digit * (10 - i));
            }
            char lastChar = isbn.charAt(9);
            int lastDigit = (lastChar == 'X' || lastChar == 'x') ? 10 : Character.getNumericValue(lastChar);
            sum += lastDigit;
            return sum % 11 == 0;
        }

        if (isbn.length() == 13) {
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                int digit = Character.getNumericValue(isbn.charAt(i));
                sum += digit * (i % 2 == 0 ? 1 : 3);
            }
            int lastDigit = Character.getNumericValue(isbn.charAt(12));
            int checkDigit = (10 - (sum % 10)) % 10;
            return lastDigit == checkDigit;
        }

        return false;
    }

    private void searchGoogleBooksByIsbn(String isbn) {
        GoogleBooksService.getInstance().getApi().searchByIsbn("isbn:" + isbn)
                .enqueue(new Callback<BookResponse>() {
                    @Override
                    public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                        if (response.isSuccessful() && response.body() != null &&
                                response.body().getItems() != null && !response.body().getItems().isEmpty()) {

                            BookResponse.BookItem bookItem = response.body().getItems().get(0);
                            BookResponse.VolumeInfo volumeInfo = bookItem.getVolumeInfo();

                            Book book = new Book(
                                    volumeInfo.getTitle(),
                                    volumeInfo.getAuthors() != null && !volumeInfo.getAuthors().isEmpty()
                                            ? volumeInfo.getAuthors().get(0)
                                            : "Unknown Author",
                                    (volumeInfo.getCategories() != null && !volumeInfo.getCategories().isEmpty())
                                            ? volumeInfo.getCategories().get(0)
                                            : "Fiction",
                                    "To Read"
                            );

                            book.setIsbn(isbn);
                            if (volumeInfo.getImageLinks() != null && volumeInfo.getImageLinks().getThumbnail() != null) {
                                book.setImageUrl(volumeInfo.getImageLinks().getThumbnail());
                            }

                            isbnSearchedBook.postValue(book);
                        } else {
                            searchError.postValue("Book not found. Please check the ISBN and try again.");
                        }
                    }

                    @Override
                    public void onFailure(Call<BookResponse> call, Throwable t) {
                        searchError.postValue("Error searching for book. Please try again later.");
                    }
                });
    }
}
