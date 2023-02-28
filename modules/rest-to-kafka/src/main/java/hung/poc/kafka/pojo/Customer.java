package hung.poc.kafka.pojo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
public class Customer {

    @NotNull
    private UUID id;

    @Size(max = 31)
    private String firstName;

    @Size(max = 30)
    private String lastName;

    @Email
    private String email;

    private String createdBy;
}
