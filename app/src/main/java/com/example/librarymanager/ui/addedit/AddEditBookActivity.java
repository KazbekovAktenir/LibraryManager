package com.example.librarymanager.ui.addedit;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.example.librarymanager.R;
import com.example.librarymanager.data.Book;
import com.example.librarymanager.viewmodel.BookViewModel;

public class AddEditBookActivity extends AppCompatActivity {
    private BookViewModel bookViewModel;
    private EditText editTitle;
    private EditText editAuthor;
    private EditText editGenre;
    private Spinner spinnerStatus;
    private EditText editNotes;
    private Button buttonSave;
    private int bookId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_book);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        editTitle = findViewById(R.id.edit_title);
        editAuthor = findViewById(R.id.edit_author);
        editGenre = findViewById(R.id.edit_genre);
        spinnerStatus = findViewById(R.id.spinner_status);
        editNotes = findViewById(R.id.edit_notes);
        buttonSave = findViewById(R.id.button_save);

        // Setup status spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.book_statuses, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Initialize ViewModel
        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);

        // Get book ID if editing
        bookId = getIntent().getIntExtra("BOOK_ID", -1);
        if (bookId != -1) {
            getSupportActionBar().setTitle("Edit Book");
            loadBookData();
        } else {
            getSupportActionBar().setTitle("Add Book");
        }

        // Setup save button
        buttonSave.setOnClickListener(v -> saveBook());
    }

    private void loadBookData() {
        bookViewModel.getBookById(bookId).observe(this, book -> {
            if (book != null) {
                editTitle.setText(book.getTitle());
                editAuthor.setText(book.getAuthor());
                editGenre.setText(book.getGenre());
                editNotes.setText(book.getNotes());

                // Set status spinner
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
        String genre = editGenre.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();
        String notes = editNotes.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || genre.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Book book = new Book(title, author, genre, status);
        book.setNotes(notes);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
