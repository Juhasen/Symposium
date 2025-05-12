package pl.juhas.symposium.dto;

import java.time.LocalDateTime;

public record PresentationDTO(String topic, LocalDateTime startTime) {
}
