package com.healthcare.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
@Data
public class KafkaProperties {

    private String bootstrapServers = "localhost:9092";

    private Producer producer = new Producer();
    private Consumer consumer = new Consumer();

    @Data
    public static class Producer {
        private String keySerializer;
        private String valueSerializer;
        private String acks = "all";
        private Integer retries = 3;
        private Integer batchSize = 16384;
        private Integer lingerMs = 10;
        private Long bufferMemory = 33554432L;
    }

    @Data
    public static class Consumer {
        private String keyDeserializer;
        private String valueDeserializer;
        private String groupId;
        private String autoOffsetReset = "earliest";
        private Boolean enableAutoCommit = true;
        private Integer maxPollRecords = 500;
    }
}

