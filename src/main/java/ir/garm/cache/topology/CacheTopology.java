package ir.garm.cache.topology;

import ir.garm.cache.domain.dto.AccessTokenDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import static ir.garm.cache.domain.constant.Constants.*;

@Component
@Slf4j
public class CacheTopology {
    @Autowired
    public void process(StreamsBuilder builder) {

        StoreBuilder<KeyValueStore<String, AccessTokenDto>> storeBuilder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(CACHE_STORE_NAME), Serdes.String(), new JsonSerde<>(AccessTokenDto.class));

        builder.addGlobalStore(storeBuilder, CACHE_TOPIC_NAME,
                        Consumed.with(Serdes.String(), new JsonSerde<>(AccessTokenDto.class)),
                        TokenStoreProcessor::new)
                .table(CACHE_CHANGE_LOG_TOPIC);

    }


}