package agenda.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository zum Zugriff auf gespeicherte Statusinformationen.
 * Repostory-Interfaces erben eine unglaubliche Menge hilfreicher Methoden.
 * Weitere Methoden kann man einfach durch Benennung definierern. Spring Data
 * ergänzt die Implementierungen zur Laufzeit.
 * 
 * @author Pavel St. (paffen)
 */
@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

  /**
   * Findet den Status für einen gegebenen Task und einen gegebenen Anwender.
   * 
   * @param user Anwender
   * @param task Task
   * @return Status, <code>null</code>, wenn noch kein Status existiert.
   */
  Status findByUserAndTask(User user, Task task);
}
