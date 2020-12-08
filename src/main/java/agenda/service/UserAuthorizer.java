package agenda.service;

import agenda.persistence.Task;
import agenda.persistence.Topic;
import agenda.persistence.User;
import org.springframework.security.access.AccessDeniedException;

/**
 * Gemeinsame Klasse fuer Tasks und Topics zur Authentifizierung des Users fuer
 * besondere Operationen.
 * 
 * @author Pavel St (paffen)
 */
public class UserAuthorizer {

  /**
   * Vergleicht login mit dem Ersteller des Topics.
   * 
   * @return true, falls erfolgreich
   */
  public boolean isUserCreator(Topic topic, User user) {
    if (!user.equals(topic.getCreator())) {
      throw new AccessDeniedException("Unberechtigter Zugriff auf einem Topic.");
    }
    return true;
  }

  /**
   * Vergleicht login mit dem Ersteller des Tasks.
   * 
   * @return true, falls erfolgreich
   */
  public boolean isUserCreator(Task task, User user) {
    if (!user.equals(task.getTopic().getCreator())) {
      throw new AccessDeniedException("Unberechtigter Zugriff eines Tasks.");
    }
    return true;
  }
}
