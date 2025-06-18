package com.example.librarymanager.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleBooksService {
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/";
    private static GoogleBooksService instance;
    private final GoogleBooksApi api;

    private GoogleBooksService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .build();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        api = retrofit.create(GoogleBooksApi.class);
    }

    public static synchronized GoogleBooksService getInstance() {
        if (instance == null) {
            instance = new GoogleBooksService();
        }
        return instance;
    }

    public GoogleBooksApi getApi() {
        return api;
    }
} 