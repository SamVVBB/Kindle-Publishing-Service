package com.amazon.ata.kindlepublishingservice.converters;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;

import java.util.ArrayList;
import java.util.List;

public class PublishingStatusItemConverter {

    public PublishingStatusItemConverter() {}

    public static List<PublishingStatusRecord> toRecord(List<PublishingStatusItem> publishingStatusItems) {

        List<PublishingStatusRecord> publishingStatusRecords = new ArrayList<>();

        for (PublishingStatusItem publishingStatusItem: publishingStatusItems) {
            publishingStatusRecords.add(toRecord(publishingStatusItem));
        }

        return publishingStatusRecords;
    }

    public static PublishingStatusRecord toRecord(PublishingStatusItem publishingStatusItem) {

        PublishingStatusRecord publishingStatusRecord = PublishingStatusRecord.builder()
                .withStatus(String.valueOf(publishingStatusItem.getStatus()))
                .withStatusMessage(publishingStatusItem.getStatusMessage())
                .withBookId(publishingStatusItem.getBookId())
                .build();

        return publishingStatusRecord;
    }

}
