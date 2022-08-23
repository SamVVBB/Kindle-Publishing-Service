package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BookPublishRequestManager {

    private ConcurrentLinkedQueue<BookPublishRequest> publishRequestQueue;


    public BookPublishRequestManager(ConcurrentLinkedQueue<BookPublishRequest> publishRequestQueue) {
        this.publishRequestQueue = publishRequestQueue;
    }

    public BookPublishRequestManager() {
        publishRequestQueue = new ConcurrentLinkedQueue<>();
    }

    public void addBookPublishRequest(BookPublishRequest bookPublishRequest) {
        publishRequestQueue.add(bookPublishRequest);
    }

    public BookPublishRequest getBookPublishRequestToProcess() {
        return publishRequestQueue.poll();
    }

}
