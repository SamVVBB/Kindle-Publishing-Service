package com.amazon.ata.kindlepublishingservice.models.response;

import com.amazon.ata.kindlepublishingservice.models.Book;

public class RemoveBookFromCatalogResponse {
    private Book book;


    public RemoveBookFromCatalogResponse(Builder builder) {
        this.book =  builder.book;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public static Builder builder() {return new Builder();}

    public static final class Builder {
        private Book book;

        public Builder withBook(Book bookToUse) {
        this.book = bookToUse;
        return this;
        }

        public RemoveBookFromCatalogResponse build() {return new RemoveBookFromCatalogResponse(this);}

    }
}
