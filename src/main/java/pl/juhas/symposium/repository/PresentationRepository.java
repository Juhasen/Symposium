package pl.juhas.symposium.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.juhas.symposium.dto.SpeakerStatsDTO;
import pl.juhas.symposium.model.ConferenceHall;
import pl.juhas.symposium.model.Participant;
import pl.juhas.symposium.model.Presentation;

import java.util.List;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {

    @Query("""
    SELECT new pl.juhas.symposium.dto.SpeakerStatsDTO(p.firstName, p.lastName, COUNT(pr))
    FROM Presentation pr
    JOIN pr.participants p
    WHERE p.role = pl.juhas.symposium.enums.Role.SPEAKER
    GROUP BY p.id, p.firstName, p.lastName
    ORDER BY COUNT(pr) DESC
    """)
    List<SpeakerStatsDTO> findTopSpeaker(Pageable pageable);


    long countPresentationsByConferenceHall(ConferenceHall conferenceHall);
}
