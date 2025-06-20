// BookAdapter.java — обновлённый с поддержкой imageUrl и карточного дизайна

package com.example.librarymanager.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.librarymanager.R;
import com.example.librarymanager.data.Book;

public class BookAdapter extends ListAdapter<Book, BookAdapter.BookViewHolder> {
    private OnItemClickListener listener;

    public BookAdapter() {
        super(new DiffUtil.ItemCallback<Book>() {
            @Override
            public boolean areItemsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
                return oldItem.getTitle().equals(newItem.getTitle()) &&
                        oldItem.getAuthor().equals(newItem.getAuthor()) &&
                        oldItem.getStatus().equals(newItem.getStatus());
            }
        });
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item, parent, false);
        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book currentBook = getItem(position);
        holder.textViewTitle.setText(currentBook.getTitle());
        holder.textViewAuthor.setText(currentBook.getAuthor());
        holder.textViewStatus.setText(currentBook.getStatus());

        if (currentBook.getGenre() != null) {
            holder.textViewGenre.setText(currentBook.getGenre());
        } else {
            holder.textViewGenre.setText("");
        }

        // Загрузка изображения обложки
        if (currentBook.getImageUrl() != null && !currentBook.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(currentBook.getImageUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(holder.imageViewCover);
        } else {
            holder.imageViewCover.setImageResource(R.drawable.ic_book_placeholder);
        }
    }

    public Book getBookAt(int position) {
        return getItem(position);
    }

    class BookViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewAuthor;
        private final TextView textViewStatus;
        private final TextView textViewGenre;
        private final ImageView imageViewCover;

        private BookViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewAuthor = itemView.findViewById(R.id.text_view_author);
            textViewStatus = itemView.findViewById(R.id.text_view_status);
            textViewGenre = itemView.findViewById(R.id.text_view_genre);
            imageViewCover = itemView.findViewById(R.id.image_view_cover);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
