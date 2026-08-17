package mate.academy.onlinebookstore.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDto(
        @NotBlank
        String email,
        @NotBlank
        String password) {
}
