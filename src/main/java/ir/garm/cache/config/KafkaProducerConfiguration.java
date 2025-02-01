package ir.garm.cache.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaProducerConfiguration {

    @Value("${kafka.bootstrap-servers}")
    private List<String> bootstrapServers;

    @Value("${kafka.producer.ack-config}")
    private String ackConfig;

    @Value("${kafka.producer.retries-config}")
    private Integer retriesConfig;

    @Bean
    public KafkaTemplate<?, ?> kafkaTemplate(final ProducerFactory<?, ?> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<?, ?> producerFactory() {
        return new DefaultKafkaProducerFactory<>(initProducerConfig());
    }

    public Map<String, Object> initProducerConfig() {
        final Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, ackConfig);
        config.put(ProducerConfig.RETRIES_CONFIG, retriesConfig);
        return config;
    }

}