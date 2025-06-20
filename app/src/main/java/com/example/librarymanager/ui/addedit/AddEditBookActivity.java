package com.example.librarymanager.ui.addedit;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.librarymanager.R;
import com.example.librarymanager.data.Book;
import com.example.librarymanager.viewmodel.BookViewModel;

public class AddEditBookActivity extends AppCompatActivity {

    private BookViewModel bookViewModel;
    private EditText editTitle, editAuthor, editNotes, editIsbn;
    private Spinner spinnerGenre, spinnerStatus;
    private Button buttonSave, buttonScanIsbn;
    private int bookId = -1;

    private final ActivityResultLauncher<Intent> barcodeLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String isbn = result.getData().getStringExtra("SCAN_RESULT");
                    if (isbn != null) {
                        editIsbn.setText(isbn);
                        fetchBookFromGoogleBooks(isbn);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_book);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTitle = findViewById(R.id.edit_title);
        editAuthor = findViewById(R.id.edit_author);
        editNotes = findViewById(R.id.edit_notes);
        editIsbn = findViewById(R.id.edit_text_isbn);
        spinnerGenre = findViewById(R.id.spinner_genre);
        spinnerStatus = findViewById(R.id.spinner_status);
        buttonSave = findViewById(R.id.button_save);
        buttonScanIsbn = findViewById(R.id.button_scan_isbn);

        ArrayAdapter<CharSequence> genreAdapter = ArrayAdapter.createFromResource(this,
                R.array.genre_array, android.R.layout.simple_spinner_item);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.book_statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);

        bookId = getIntent().getIntExtra("BOOK_ID", -1);
        if (bookId != -1) {
            getSupportActionBar().setTitle("Edit Book");
            loadBookData();
        } else {
            getSupportActionBar().setTitle("Add Book");
        }

        buttonSave.setOnClickListener(v -> saveBook());
        buttonScanIsbn.setOnClickListener(v -> startIsbnScan());
    }

    private void loadBookData() {
        bookViewModel.getBookById(bookId).observe(this, book -> {
            if (book != null) {
                editTitle.setText(book.getTitle());
                editAuthor.setText(book.getAuthor());
                editNotes.setText(book.getNotes());
                editIsbn.setText(book.getIsbn());

                String[] genres = getResources().getStringArray(R.array.genre_array);
                for (int i = 0; i < genres.length; i++) {
                    if (genres[i].equals(book.getGenre())) {
                        spinnerGenre.setSelection(i);
                        break;
                    }
                }

                String[] statuses = getResources().getStringArray(R.array.book_statuses);
                for (int i = 0; i < statuses.length; i++) {
                    if (statuses[i].equals(book.getStatus())) {
                        spinnerStatus.setSelection(i);
                        break;
                    }
                }
            }
        });
    }

    private void saveBook() {
        String title = editTitle.getText().toString().trim();
        String author = editAuthor.getText().toString().trim();
        String genre = spinnerGenre.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();
        String notes = editNotes.getText().toString().trim();
        String isbn = editIsbn.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Book book = new Book(title, author, genre, status);
        book.setNotes(notes);
        book.setIsbn(isbn);

        if (bookId != -1) {
            book.setId(bookId);
            bookViewModel.update(book);
            Toast.makeText(this, "Book updated", Toast.LENGTH_SHORT).show();
        } else {
            bookViewModel.insert(book);
            Toast.makeText(this, "Book added", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void startIsbnScan() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "ONE_D_MODE");
        try {
            barcodeLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Сканер не найден. Установите Barcode Scanner от ZXing", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchBookFromGoogleBooks(String isbn) {
        bookViewModel.searchBookByIsbn(isbn).observe(this, book -> {
            if (book != null) {
                editTitle.setText(book.getTitle());
                editAuthor.setText(book.getAuthor());
                Toast.makeText(this, "Книга найдена и заполнена", Toast.LENGTH_SHORT).show();
            }
        });

        bookViewModel.getSearchError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
