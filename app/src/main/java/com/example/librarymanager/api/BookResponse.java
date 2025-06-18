package com.example.librarymanager.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookResponse {
    @SerializedName("items")
    private List<BookItem> items;

    public List<BookItem> getItems() {
        return items;
    }

    public static class BookItem {
        @SerializedName("volumeInfo")
        private VolumeInfo volumeInfo;

        public VolumeInfo getVolumeInfo() {
            return volumeInfo;
        }
    }

    public static class VolumeInfo {
        @SerializedName("title")
        private String title;

        @SerializedName("authors")
        private List<String> authors;

        @SerializedName("industryIdentifiers")
        private List<IndustryIdentifier> industryIdentifiers;

        public String getTitle() {
            return title;
        }

        public List<String> getAuthors() {
            return authors;
        }

        public List<IndustryIdentifier> getIndustryIdentifiers() {
            return industryIdentifiers;
        }
    }

    public static class IndustryIdentifier {
        @SerializedName("type")
        private String type;

        @SerializedName("identifier")
        private String identifier;

        public String getType() {
            return type;
        }

        public String getIdentifier() {
            return identifier;
        }
    }
} 