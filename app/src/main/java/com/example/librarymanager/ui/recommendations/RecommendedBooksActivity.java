package com.example.librarymanager.ui.recommendations;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librarymanager.R;
import com.example.librarymanager.data.Book;
import com.example.librarymanager.ui.main.BookAdapter;
import com.example.librarymanager.viewmodel.BookViewModel;

import java.util.List;

public class RecommendedBooksActivity extends AppCompatActivity {

    private BookViewModel bookViewModel;
    private BookAdapter bookAdapter;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_books);

        setTitle("Рекомендованные книги");

        RecyclerView recyclerView = findViewById(R.id.recycler_view_recommendations);
        emptyView = findViewById(R.id.empty_view);

        bookAdapter = new BookAdapter();
        recyclerView.setAdapter(bookAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);

        // Получаем прочитанные книги и рекомендуем по жанру
        bookViewModel.getReadBooks().observe(this, readBooks -> {
            if (readBooks != null && !readBooks.isEmpty()) {
                String lastGenre = readBooks.get(readBooks.size() - 1).getGenre(); // Последний прочитанный жанр
                loadRecommendations(lastGenre);
            } else {
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText("Нет прочитанных книг для рекомендаций.");
            }
        });

        // По нажатию на книгу можно будет в будущем открыть BookDetailActivity
        bookAdapter.setOnItemClickListener(book -> {
            // Здесь можно добавить переход к деталям книги
        });
    }

    private void loadRecommendations(String genre) {
        bookViewModel.getRecommendedBooks(genre).observe(this, recommendedBooks -> {
            if (recommendedBooks != null && !recommendedBooks.isEmpty()) {
                bookAdapter.submitList(recommendedBooks);
                emptyView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText("Нет рекомендаций для жанра: " + genre);
            }
        });
    }
}
