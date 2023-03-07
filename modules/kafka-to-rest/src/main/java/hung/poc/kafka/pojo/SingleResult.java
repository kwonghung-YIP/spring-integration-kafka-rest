package hung.poc.kafka.pojo;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.net.URL;

@Data
public class SingleResult<T> {

    enum Status {
        OK
    }

    @JsonAlias({"request_id"})
    private String requestId;

    private T results;

    private Status status;
}
