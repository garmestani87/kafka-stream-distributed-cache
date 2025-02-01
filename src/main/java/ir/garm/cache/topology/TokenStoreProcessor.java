package ir.garm.cache.topology;

import ir.garm.cache.domain.dto.AccessTokenDto;
import org.apache.kafka.streams.processor.api.ContextualProcessor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.stereotype.Component;

@Component
public class TokenStoreProcessor extends ContextualProcessor<String, AccessTokenDto, Void, Void> {

    private KeyValueStore<String, AccessTokenDto> tokenStore;

    @Override
    public void init(ProcessorContext<Void, Void> context) {
        super.init(context);
        tokenStore = context.getStateStore("tokens");
    }

    @Override
    public void process(Record<String, AccessTokenDto> record) {
        tokenStore.put(record.key(), record.value());
//        context().forward(record);
        context().commit();
    }

    @Override
    public void close() {
        super.close();
    }

}