package agenda.persistence;

//import agenda.service.dto.SubscriberTaskDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository zum Zugriff auf gespeicherte Tasks. Repostory-Interfaces erben
 * eine unglaubliche Menge hilfreicher Methoden. Weitere Methoden kann man
 * einfach durch Benennung definierern. Spring Data ergänzt die
 * Implementierungen zur Laufzeit.
 * 
 * @author Pavel St. (paffen)
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /*
     * Alle gespeicherten Tasks sortieren.
     */
    List<Task> findAllByOrderByTitleAsc();
}
