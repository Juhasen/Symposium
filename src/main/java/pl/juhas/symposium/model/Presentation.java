package pl.juhas.symposium.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "presentation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"topic_id", "start_time"}),
        @UniqueConstraint(columnNames = {"conference_hall_id", "start_time"})})
public class Presentation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "id", unique = true) // One topic can be presented only once
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "conference_hall_id", referencedColumnName = "id")
    private ConferenceHall conferenceHall;

    @ManyToMany
    @JoinTable(
            name = "presentation_participants",
            joinColumns = @JoinColumn(name = "presentation_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private List<Participant> participants;


    @JoinColumn(name = "start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;
}
