package com.example.librarymanager.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.librarymanager.R;
import com.example.librarymanager.data.Book;
import java.util.concurrent.TimeUnit;

public class ReadingReminderWorker extends Worker {
    private static final String CHANNEL_ID = "reading_reminder_channel";
    private static final int NOTIFICATION_ID = 1;

    public ReadingReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    public static void scheduleReadingReminder(WorkManager workManager, Book book, long delayInMinutes) {
        Data inputData = new Data.Builder()
                .putString("book_title", book.getTitle())
                .build();

        OneTimeWorkRequest reminderWork = new OneTimeWorkRequest.Builder(ReadingReminderWorker.class)
                .setInputData(inputData)
                .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
                .build();

        workManager.enqueue(reminderWork);
    }

    @NonNull
    @Override
    public Result doWork() {
        String bookTitle = getInputData().getString("book_title");
        if (bookTitle == null) {
            return Result.failure();
        }

        createNotificationChannel();
        showNotification(bookTitle);
        return Result.success();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Reading Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Reminders for your reading schedule");

            NotificationManager notificationManager = getApplicationContext()
                    .getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String bookTitle) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_book)
                .setContentTitle("Time to Read!")
                .setContentText("Don't forget to read: " + bookTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}