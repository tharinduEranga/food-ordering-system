package com.foodordering.system.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class KafkaMessageHelper {

    public <T> BiConsumer<SendResult<String, T>, Throwable> getKafkaCallback(
            String requestTopicName, T avroModel, String orderId, String avroModelName) {

        return (stringTSendResult, ex) -> {
            if (ex != null) {
                log.error("Error while sending AvroModel " + avroModelName + " message {} to topic: {}",
                        avroModel.toString(), requestTopicName, ex);

            } else {
                RecordMetadata metadata = stringTSendResult.getRecordMetadata();
                log.info("Received successful response from Kafka for order id: {} Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        orderId,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp());
            }
        };
    }
}
