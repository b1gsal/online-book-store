package mate.academy.onlinebookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import mate.academy.onlinebookstore.model.Status;

public record OrderStatusRequestDto(
        @NotNull(message = "Status cannot be null")
        Status status) {
}
