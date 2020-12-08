package agenda.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository zum Zugriff auf gespeicherte Anwenderdaten. Repostory-Interfaces
 * erben eine unglaubliche Menge hilfreicher Methoden. Weitere Methoden kann man
 * einfach durch Benennung definierern. Spring Data ergänzt die
 * Implementierungen zur Laufzeit.
 * 
 * @author Pavel St. (paffen)
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

  /**
   * Ermittelt alle Anwender, die Administrator sind (oder alle anderen).
   * 
   * @param isAdministrator Flag zum Filtern
   * @return
   */
  List<User> findByAdministrator(boolean isAdministrator);

  /**
   * Ermittelt alle Anwender in aufsteigender Reihenfolge nach Login.
   *
   * @param isAdministrator Flag, ob exklusiv nach Administratoren oder
   *                        Nicht-Administratoren zu suchen
   */
  List<User> findByAdministratorOrderByLoginAsc(boolean isAdministrator);
}