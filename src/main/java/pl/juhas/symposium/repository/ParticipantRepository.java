package pl.juhas.symposium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.juhas.symposium.dto.ParticipantDTO;
import pl.juhas.symposium.model.Participant;

import java.util.List;


public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    @Query("SELECT new pl.juhas.symposium.dto.ParticipantDTO(p.firstName, p.lastName, p.email, p.role, p.country) " + "FROM Participant p " + "ORDER BY p.role")
    List<ParticipantDTO> findAllParticipantsOrderedByRoles();


    @Query("SELECT new pl.juhas.symposium.dto.ParticipantDTO(p.firstName, p.lastName, p.email, p.role, p.country) " + "FROM Participant p")
    List<ParticipantDTO> findAllAsDto();


}
