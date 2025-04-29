package pl.juhas.symposium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.juhas.symposium.model.Participant;

import java.util.List;


public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    @Query("SELECT p.role, p FROM Participant p")
    List<Object[]> findAllParticipantsWithRoles();


}
