package com.example.zadanko8;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Book.class}, version = 1, exportSchema = false)
public abstract class BookDatabase extends RoomDatabase {
    public abstract BookDao bookDao();

    private static volatile BookDatabase databaseInstance;
    static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    static BookDatabase getDatabase(final Context context) {
        if (databaseInstance == null)
            databaseInstance = Room.databaseBuilder(context.getApplicationContext(), BookDatabase.class, "book_db")
                    .addCallback(sRoomDatabaseCallback)
                    .build();
        return databaseInstance;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                BookDao dao = databaseInstance.bookDao();

                for(int i = 1;i<=6;i++) {
                    Book book = new Book("Nazwa " + i,"Autor " + i);
                    dao.insert(book);
                }
            });
        }
    };
}