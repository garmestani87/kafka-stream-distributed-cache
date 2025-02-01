package ir.garm.cache.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData<R> {
    private R response;
    private HttpStatus status;

    public R getData() {
        return this.status == HttpStatus.OK ? this.response : null;
    }

    public boolean exceptionHappened() {
        return this.status != HttpStatus.OK;
    }

    public boolean handleStatus() {
        return exceptionHappened();
    }
}
