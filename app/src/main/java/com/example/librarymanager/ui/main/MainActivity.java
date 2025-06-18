package com.example.librarymanager.ui.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.librarymanager.R;
import com.example.librarymanager.data.Book;
import com.example.librarymanager.ui.addedit.AddEditBookActivity;
import com.example.librarymanager.ui.detail.BookDetailActivity;
import com.example.librarymanager.viewmodel.BookViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private BookViewModel bookViewModel;
    private BookAdapter adapter;
    private SearchView searchView;
    private Spinner spinnerStatus;
    private Spinner spinnerGenre;
    private ImageButton buttonIsbnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Library Manager");

        // Initialize views
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        searchView = findViewById(R.id.search_view);
        spinnerStatus = findViewById(R.id.spinner_status);
        spinnerGenre = findViewById(R.id.spinner_genre);
        buttonIsbnSearch = findViewById(R.id.button_isbn_search);
        FloatingActionButton fab = findViewById(R.id.fab);

        // Setup RecyclerView
        adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ViewModel
        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);

        // Observe books
        bookViewModel.getAllBooks().observe(this, books -> adapter.submitList(books));

        // Setup search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    bookViewModel.getAllBooks().observe(MainActivity.this, books -> adapter.submitList(books));
                }
                return true;
            }
        });

        // Setup ISBN search
        buttonIsbnSearch.setOnClickListener(v -> showIsbnSearchDialog());

        // Setup status spinner
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
            R.array.book_statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String status = parent.getItemAtPosition(position).toString();
                if (status.equals("All")) {
                    bookViewModel.getAllBooks().observe(MainActivity.this, books -> adapter.submitList(books));
                } else {
                    bookViewModel.getBooksByStatus(status).observe(MainActivity.this, books -> adapter.submitList(books));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Setup genre spinner
        bookViewModel.getAllGenres().observe(this, genres -> {
            ArrayList<String> genreList = new ArrayList<>(genres);
            genreList.add(0, "All");
            ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genreList);
            genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGenre.setAdapter(genreAdapter);
        });

        spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String genre = parent.getItemAtPosition(position).toString();
                if (genre.equals("All")) {
                    bookViewModel.getAllBooks().observe(MainActivity.this, books -> adapter.submitList(books));
                } else {
                    bookViewModel.getBooksByGenre(genre).observe(MainActivity.this, books -> adapter.submitList(books));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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

    private void showIsbnSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search by ISBN");

        final EditText input = new EditText(this);
        input.setHint("Enter ISBN (10 or 13 digits)");
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Search", (dialog, which) -> {
            String isbn = input.getText().toString();
            if (!isbn.isEmpty()) {
                searchByIsbn(isbn);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Observe search errors
        bookViewModel.getSearchError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void searchByIsbn(String isbn) {
        bookViewModel.searchBookByIsbn(isbn).observe(this, book -> {
            if (book != null) {
                // Если книга найдена, открываем её детали
                Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
                intent.putExtra("BOOK_ID", book.getId());
                startActivity(intent);
            }
        });
    }
}
