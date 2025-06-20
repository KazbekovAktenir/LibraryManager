package com.example.librarymanager.ui.detail;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.example.librarymanager.R;
import com.example.librarymanager.data.Book;
import com.example.librarymanager.ui.addedit.AddEditBookActivity;
import com.example.librarymanager.viewmodel.BookViewModel;

public class BookDetailActivity extends AppCompatActivity {
    private BookViewModel bookViewModel;
    private int bookId;
    private TextView textTitle;
    private TextView textAuthor;
    private TextView textGenre;
    private TextView textStatus;
    private TextView textNotes;
    private EditText editReminderMinutes;
    private Button buttonSetReminder;
    private Button buttonEdit;
    private Button buttonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Book Details");

        // Initialize views
        textTitle = findViewById(R.id.text_title);
        textAuthor = findViewById(R.id.text_author);
        textGenre = findViewById(R.id.text_genre);
        textStatus = findViewById(R.id.text_status);
        textNotes = findViewById(R.id.text_notes);
        editReminderMinutes = findViewById(R.id.edit_reminder_minutes);
        buttonSetReminder = findViewById(R.id.button_set_reminder);
        buttonEdit = findViewById(R.id.button_edit);
        buttonDelete = findViewById(R.id.button_delete);

        // Get book ID from intent
        bookId = getIntent().getIntExtra("BOOK_ID", -1);
        if (bookId == -1) {
            Toast.makeText(this, "Error: Book not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize ViewModel
        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);

        // Observe book
        bookViewModel.getBookById(bookId).observe(this, this::updateUI);

        // Setup button click listeners
        buttonSetReminder.setOnClickListener(v -> setReminder());
        buttonEdit.setOnClickListener(v -> editBook());
        buttonDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void updateUI(Book book) {
        if (book != null) {
            textTitle.setText(book.getTitle());
            textAuthor.setText(book.getAuthor());
            textGenre.setText(book.getGenre());
            textStatus.setText(book.getStatus());
            textNotes.setText(book.getNotes());
        }
    }

    private void setReminder() {
        String minutesStr = editReminderMinutes.getText().toString();
        if (minutesStr.isEmpty()) {
            Toast.makeText(this, "Please enter minutes", Toast.LENGTH_SHORT).show();
            return;
        }

        long minutes = Long.parseLong(minutesStr);
        bookViewModel.getBookById(bookId).observe(this, book -> {
            if (book != null) {
                bookViewModel.scheduleReadingReminder(book, minutes);
                Toast.makeText(this, "Reminder set for " + minutes + " minutes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editBook() {
        Intent intent = new Intent(this, AddEditBookActivity.class);
        intent.putExtra("BOOK_ID", bookId);
        startActivity(intent);
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Delete Book")
            .setMessage("Are you sure you want to delete this book?")
            .setPositiveButton("Delete", (dialog, which) -> deleteBook())
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteBook() {
        bookViewModel.getBookById(bookId).observe(this, book -> {
            if (book != null) {
                bookViewModel.delete(book);
                Toast.makeText(this, "Book deleted", Toast.LENGTH_SHORT).show();
                finish();
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
