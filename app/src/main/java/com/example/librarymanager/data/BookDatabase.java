package com.example.librarymanager.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Book.class}, version = 2, exportSchema = false)
public abstract class BookDatabase extends RoomDatabase {

    private static BookDatabase instance;

    public abstract BookDao bookDao();

    // Singleton — чтобы база создавалась один раз
    public static synchronized BookDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            BookDatabase.class, "book_database")
                    .fallbackToDestructiveMigration()  // при изменении схемы — удалить старую базу
                    .build();
        }
        return instance;
    }
}
