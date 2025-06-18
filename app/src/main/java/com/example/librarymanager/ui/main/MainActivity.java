package com.example.librarymanager.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.librarymanager.R;
import com.example.librarymanager.data.Book;
import com.example.librarymanager.ui.addedit.AddEditBookActivity;
import com.example.librarymanager.ui.detail.BookDetailActivity;
import com.example.librarymanager.viewmodel.BookViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private BookViewModel bookViewModel;
    private BookAdapter adapter;
    private TextInputEditText searchView;
    private AutoCompleteTextView spinnerStatus;
    private AutoCompleteTextView spinnerGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Library Manager");

        // Initialize views
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        searchView = findViewById(R.id.search_view);
        spinnerStatus = findViewById(R.id.spinner_status);
        spinnerGenre = findViewById(R.id.spinner_genre);
        ExtendedFloatingActionButton fab = findViewById(R.id.fab);

        // Setup RecyclerView
        adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ViewModel
        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);

        // Observe books
        bookViewModel.getAllBooks().observe(this, books -> adapter.submitList(books));

        // Setup search
        searchView.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String query = searchView.getText().toString();
                if (!query.isEmpty()) {
                    searchBooks(query);
                } else {
                    bookViewModel.getAllBooks().observe(MainActivity.this, books -> adapter.submitList(books));
                }
            }
        });

        // Setup status dropdown
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
            R.array.book_statuses, android.R.layout.simple_dropdown_item_1line);
        spinnerStatus.setAdapter(statusAdapter);

        spinnerStatus.setOnItemClickListener((parent, view, position, id) -> {
            String status = parent.getItemAtPosition(position).toString();
            if (status.equals("All")) {
                bookViewModel.getAllBooks().observe(MainActivity.this, books -> adapter.submitList(books));
            } else {
                bookViewModel.getBooksByStatus(status).observe(MainActivity.this, books -> adapter.submitList(books));
            }
        });

        // Setup genre dropdown
        bookViewModel.getAllGenres().observe(this, genres -> {
            ArrayList<String> genreList = new ArrayList<>(genres);
            genreList.add(0, "All");
            ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, genreList);
            spinnerGenre.setAdapter(genreAdapter);
        });

        spinnerGenre.setOnItemClickListener((parent, view, position, id) -> {
            String genre = parent.getItemAtPosition(position).toString();
            if (genre.equals("All")) {
                bookViewModel.getAllBooks().observe(MainActivity.this, books -> adapter.submitList(books));
            } else {
                bookViewModel.getBooksByGenre(genre).observe(MainActivity.this, books -> adapter.submitList(books));
            }
        });

        // Setup FAB
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditBookActivity.class);
            startActivity(intent);
        });

        // Setup item click listener
        adapter.setOnItemClickListener(book -> {
            Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
            intent.putExtra("BOOK_ID", book.getId());
            startActivity(intent);
        });
    }

    private void searchBooks(String query) {
        bookViewModel.searchBooks(query).observe(this, books -> adapter.submitList(books));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            // Handle settings
            return true;
        } else if (item.getItemId() == R.id.action_about) {
            // Handle about
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
