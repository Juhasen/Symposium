package pl.juhas.symposium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.juhas.symposium.enums.Country;
import pl.juhas.symposium.enums.Role;
import pl.juhas.symposium.model.Participant;

import java.util.List;


public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findAllByRole(Role role);

    List<Participant> findAllByCountry(Country country);

}
