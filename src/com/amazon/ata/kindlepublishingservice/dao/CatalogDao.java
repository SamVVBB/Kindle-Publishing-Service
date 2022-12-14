package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.kindlepublishingservice.activity.RemoveBookFromCatalogActivity;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.Book;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.publishing.KindleFormattedBook;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.xspec.B;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import javax.inject.Inject;

public class CatalogDao {

    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates a new CatalogDao object.
     *
     * @param dynamoDbMapper The {@link DynamoDBMapper} used to interact with the catalog table.
     */
    @Inject
    public CatalogDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Returns the latest version of the book from the catalog corresponding to the specified book id.
     * Throws a BookNotFoundException if the latest version is not active or no version is found.
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table.
     */
    public CatalogItemVersion getBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null || book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

        return book;
    }

    // Returns null if no version exists for the provided bookId
    private CatalogItemVersion getLatestVersionOfBook(String bookId) {
        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(bookId);

        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression()
            .withHashKeyValues(book)
            .withScanIndexForward(false)
            .withLimit(1);

        List<CatalogItemVersion> results = dynamoDbMapper.query(CatalogItemVersion.class, queryExpression);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    public CatalogItemVersion softDeleteBookFromCatalog(String bookId) {

        // Instantiate a CatalogItemVersion object to interact with DynamoDB
//        CatalogItemVersion catalogItemVersion = dynamoDbMapper.load(CatalogItemVersion.class, bookId);

        CatalogItemVersion catalogItemVersion = this.getLatestVersionOfBook(bookId);

        if (catalogItemVersion == null || catalogItemVersion.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

        // Set the bookId in the object to bookId to be softDeleted
//        catalogItemVersion.setBookId(bookId);
        // And set Inactive to TRUE. This performs the soft delete
        catalogItemVersion.setInactive(true);

        // Save the updated book in DynamoDB
        dynamoDbMapper.save(catalogItemVersion);

        return catalogItemVersion;
    }

    public void validateBookExists(String bookId) {

        CatalogItemVersion catalogItemVersion;

        catalogItemVersion = getLatestVersionOfBook(bookId);

        if (catalogItemVersion == null) {
            throw new BookNotFoundException(String.format("could not find book with id: %s", bookId));
        }

    }

    public CatalogItemVersion createOrUpdateBook(KindleFormattedBook kindleFormattedBook) {

//        CatalogItemVersion item = getLatestVersionOfBook(kindleFormattedBook.getBookId());
//
//        if (item == null) {
//            item.setBookId(KindlePublishingUtils.generateBookId());
//            item.setAuthor(kindleFormattedBook.getAuthor());
//            item.setGenre(kindleFormattedBook.getGenre());
//            item.setText(kindleFormattedBook.getText());
//            item.setTitle(kindleFormattedBook.getTitle());
//            item.setVersion(1);
//            dynamoDbMapper.save(item);
//            return item;
//        }
//
//        this.validateBookExists(item.getBookId());
//
//        return this.getLatestVersionOfBook(item.getBookId());

        String bookId = kindleFormattedBook.getBookId();
        CatalogItemVersion newItem = new CatalogItemVersion();
        newItem.setInactive(false);
        newItem.setAuthor( kindleFormattedBook.getAuthor() );
        newItem.setGenre(  kindleFormattedBook.getGenre()  );
        newItem.setText(   kindleFormattedBook.getText()   );
        newItem.setTitle(  kindleFormattedBook.getTitle()  );

        if (bookId == null) {
            newItem.setVersion(1);
            newItem.setBookId(KindlePublishingUtils.generateBookId());
            dynamoDbMapper.save(newItem);
        } else {
            validateBookExists(bookId);
            CatalogItemVersion exisingItem = getLatestVersionOfBook(bookId);

            newItem.setBookId(  bookId );
            newItem.setVersion( exisingItem.getVersion() + 1 );

            softDeleteBookFromCatalog(exisingItem.getBookId());
            dynamoDbMapper.save(newItem);
        }
        return newItem;

    }

}
