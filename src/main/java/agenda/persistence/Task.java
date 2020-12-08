package agenda.persistence;

import java.util.Collection;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * Modellklasse für die Speicherung der Aufgaben. Enthält die Abbildung auf eine
 * Datenbanktabelle in Form von JPA-Annotation.
 * 
 * @author Pavel St. (paffen)
 */
@Entity
public class Task {

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @NotNull
  @Length(min = 8, max = 32)
  @Column(length = 32)
  private String title;

  @NotNull
  @ManyToOne
  private Topic topic;

  @NotNull
  @Length(min = 8, max = 50)
  private String shortDescription;

  @NotNull
  @Length(min = 16, max = 300)
  private String longDescription;

  // Dient dazu, Status bei Task-Loeschung mit zu loeschen.
  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
  private Collection<Status> status;

  /**
   * JPA-kompatibler Kostruktor. Wird nur von JPA verwendet und darf private sein.
   */
  public Task() {
    // JPA benötigt einen Default-Konstruktor!
  }

  /**
   * Konstruktor zum Erstellen eines neuen Tasks.
   * 
   * @param topic Topic, darf nicht null sein.
   * @param title Titel, darf nicht null sein.
   */
  public Task(final Topic topic, final String title, final String shortDescription, final String longDescription) {
    this.topic = topic;
    topic.addTask(this);
    this.title = title;
    this.shortDescription = shortDescription;
    this.longDescription = longDescription;
  }

  @Override
  public String toString() {
    return "Task \"" + title + "\"";
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public Topic getTopic() {
    return topic;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public String getLongDescription() {
    return longDescription;
  }

  public void setShortDescription(String param) {
    this.shortDescription = param;
  }

  public void setLongDescription(String param) {
    this.longDescription = param;
  }

  /*
   * Standard-Methoden. Es ist sinnvoll, hier auf die Auswertung der Assoziationen
   * zu verzichten, nur die Primärschlüssel zu vergleichen und insbesonderen
   * Getter zu verwenden, um auch mit den generierten Hibernate-Proxys kompatibel
   * zu bleiben.
   */

  @Override
  public int hashCode() {
    return Objects.hash(id, topic);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Task)) {
      return false;
    }
    Task other = (Task) obj;
    return Objects.equals(getId(), other.getId());
  }

  public Collection<Status> getStatus() {
    return status;
  }
}
