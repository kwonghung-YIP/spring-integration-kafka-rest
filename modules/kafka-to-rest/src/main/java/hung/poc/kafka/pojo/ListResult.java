package hung.poc.kafka.pojo;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.net.URL;

@Data
public class ListResult<T> {

    enum Status {
        OK
    }
    private long count;

    @JsonAlias({"next_url"})
    private URL nextUrl;

    @JsonAlias({"request_id"})
    private String requestId;

    private T[] results;

    private Status status;
}
