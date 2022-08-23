package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import javax.inject.Inject;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class BookPublishTask implements Runnable{

    private final BookPublishRequestManager bookPublishRequestManager;
    private final PublishingStatusDao publishingStatusDao;
    private final CatalogDao catalogDao;



    @Inject
    public BookPublishTask(BookPublishRequestManager bookPublishRequestManager, PublishingStatusDao publishingStatusDao,
                           CatalogDao catalogDao) {
        this.bookPublishRequestManager = bookPublishRequestManager;
        this.publishingStatusDao = publishingStatusDao;
        this.catalogDao = catalogDao;

    }



    @Override
    public void run() {

        BookPublishRequest request = bookPublishRequestManager.getBookPublishRequestToProcess();

        if (request == null) return;

        publishingStatusDao.setPublishingStatus(request.getPublishingRecordId(),
                                                PublishingRecordStatus.IN_PROGRESS,
                                                request.getBookId());

        KindleFormattedBook kindleFormattedBook = KindleFormatConverter.format(request);

        try {
            CatalogItemVersion item = catalogDao.createOrUpdateBook(kindleFormattedBook);
            publishingStatusDao.setPublishingStatus(request.getPublishingRecordId(),
                                                    PublishingRecordStatus.SUCCESSFUL,
                                                    item.getBookId());
        } catch (BookNotFoundException e) {
            publishingStatusDao.setPublishingStatus(request.getPublishingRecordId(),
                                                    PublishingRecordStatus.FAILED,
                                                    request.getBookId(),
                                                    e.getMessage());
        } catch (Exception e) {
            publishingStatusDao.setPublishingStatus(request.getPublishingRecordId(),
                                                    PublishingRecordStatus.FAILED,
                                                    request.getBookId(),
                                                    e.getMessage());
        }


    }

}
