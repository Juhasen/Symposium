package pl.juhas.symposium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.juhas.symposium.model.ConferenceHall;

public interface ConferenceHallRepository extends JpaRepository<ConferenceHall, Long> {

}
