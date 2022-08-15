package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BookPublishRequestManager {

    ConcurrentLinkedQueue<BookPublishRequest> publishRequestQueue = new ConcurrentLinkedQueue<>();

    @Inject
    public BookPublishRequestManager() {

    }

    public void addBookPublishRequest(BookPublishRequest bookPublishRequest) {
        publishRequestQueue.add(bookPublishRequest);
    }

    public BookPublishRequest getBookPublishRequestToProcess() {
        return publishRequestQueue.poll();
    }

}
