package pl.juhas.symposium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.juhas.symposium.model.Topic;


public interface TopicRepository extends JpaRepository<Topic, Long> {
}
