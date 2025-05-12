package pl.juhas.symposium.dto;

import pl.juhas.symposium.enums.Country;
import pl.juhas.symposium.enums.Role;


public record ParticipantDTO(String firstName, String lastName, String email, Role role, Country country) {
}
