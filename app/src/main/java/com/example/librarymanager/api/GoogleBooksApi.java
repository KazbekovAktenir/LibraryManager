package com.example.librarymanager.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleBooksApi {
    @GET("volumes")
    Call<BookResponse> searchByIsbn(@Query("q") String isbn);
} 