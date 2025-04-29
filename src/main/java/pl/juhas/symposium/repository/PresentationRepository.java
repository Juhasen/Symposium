package pl.juhas.symposium.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.juhas.symposium.model.ConferenceHall;
import pl.juhas.symposium.model.Participant;
import pl.juhas.symposium.model.Presentation;

import java.util.List;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {

    @Query("SELECT p.participant FROM Presentation p GROUP BY p.participant ORDER BY COUNT(p) DESC")
    List<Participant> findTopParticipant(Pageable pageable);

    long countPresentationsByConferenceHall(ConferenceHall conferenceHall);
}
