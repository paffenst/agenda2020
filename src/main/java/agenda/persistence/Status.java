package agenda.persistence;

import agenda.common.StatusEnum;
//import org.hibernate.validator.constraints.Length;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Modellklasse für die Speicherung der Beziehung zwischen Anwender und einem
 * konkreten Task. Enthält die Abbildung auf eine Datenbanktabelle in Form von
 * JPA-Annotation.
 * 
 * @author Pavel St. (paffen)
 */
@Entity
public class Status {

  // Die Verwendung einer generierten ID ist deutlich einfacher zu verstehen als
  // die
  // sauberere Lösung, aus Anwender und Task einen zusammengesetzten Schlüssel zu
  // erstellen.
  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne
  @NotNull
  private User user;

  @ManyToOne
  @NotNull
  private Task task;

  @Enumerated(EnumType.STRING)
  @NotNull
  private StatusEnum status = StatusEnum.NEU;

  @NotNull
  // @Length(min = 5, max = 500)
  private String comment = "";

  /**
   * JPA-kompatibler Kostruktor. Wird nur von JPA verwendet und darf private sein.
   */
  public Status() {
    // JPA benötigt einen Default-Konstruktor!
  }

  /**
   * Konstruktor zum Erstellen eines neuen Status.
   * 
   * @param task Task, darf nicht <code>null</code> sein
   * @param user Anwender, darf nicht <code>null</code> sein
   */
  public Status(final Task task, final User user) {
    this.task = task;
    this.user = user;
  }

  @Override
  public String toString() {
    return "Status " + status;
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(final StatusEnum status) {
    this.status = status;
  }

  public Task getTask() {
    return task;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  /*
   * Standard-Methoden. Es ist sinnvoll, hier auf die Auswertung der Assoziationen
   * zu verzichten, nur die Primärschlüssel zu vergleichen und insbesonderen
   * Getter zu verwenden, um auch mit den generierten Hibernate-Proxys kompatibel
   * zu bleiben.
   */

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Status)) {
      return false;
    }
    Status other = (Status) obj;
    return Objects.equals(getId(), other.getId());
  }

}
