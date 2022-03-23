package com.jcp.commit.hub;

import com.azure.messaging.eventhubs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.ws.ServiceMode;
import java.util.Arrays;
import java.util.List;

@Service
public class Sender {

/*    @Value("${event.hub.connection.string:}")
    private String connectionString;

    @Value("${event.hub.name:jcp-ideal-node}")
    private String eventHubName;*/

    private static final String connectionString = "Endpoint=sb://jcp-commits.servicebus.windows.net/;" +
            "SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=OsynaUi5n+oCOPO10B4XjdNg5wHKK1eIR8MQ5IZQ5dA=";
    //Endpoint=sb://<NamespaceName>.servicebus.windows.net/;SharedAccessKeyName=<KeyName>;SharedAccessKey=<KeyValue>
    //Endpoint=sb://<NamespaceName>.servicebus.windows.net/;SharedAccessKeyName=<KeyName>;SharedAccessKey=<KeyValue>;EntityPath=<EventHubName>
    private static final String eventHubName = "jcp-ideal-node";
    /**
     * Code sample for publishing events.
     * @throws IllegalArgumentException if the EventData is bigger than the max batch size.
     */
    public void publishEvents(List<EventData> allEvents) {
        // create a producer client
        System.out.println("1111111-----"+allEvents);
        EventHubProducerClient producer = new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .buildProducerClient();

        // sample events in an array

        // create a batch
        EventDataBatch eventDataBatch = producer.createBatch();

        for (EventData eventData : allEvents) {
            // try to add the event from the array to the batch
            if (!eventDataBatch.tryAdd(eventData)) {
                // if the batch is full, send it and then create a new batch
                System.out.println("222222222----"+eventData.getBodyAsString());
                producer.send(eventDataBatch);
                eventDataBatch = producer.createBatch();

                // Try to add that event that couldn't fit before.
                if (!eventDataBatch.tryAdd(eventData)) {
                    throw new IllegalArgumentException("Event is too large for an empty batch. Max size: "
                            + eventDataBatch.getMaxSizeInBytes());
                }
            }
        }
        // send the last batch of remaining events
        if (eventDataBatch.getCount() > 0) {
            producer.send(eventDataBatch);
        }
        producer.close();
    }
}
