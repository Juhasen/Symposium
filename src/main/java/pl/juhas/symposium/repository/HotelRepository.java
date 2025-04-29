package pl.juhas.symposium.repository;

import org.springframework.data.repository.CrudRepository;
import pl.juhas.symposium.model.Hotel;

public interface HotelRepository extends CrudRepository<Hotel, Long> {
}
