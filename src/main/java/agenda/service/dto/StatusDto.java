package agenda.service.dto;

import agenda.common.StatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transferobjekt für Statusinformationen zu Tasks, die spezifisch für
 * Abonnenten des Topics sind. Transferobjekte sind Schnittstellenobjekte der
 * Geschäftslogik; Sie sind nicht Teil des Modells, so dass Änderungen an den
 * Transferobjekten die Überprüfungen der Geschäftslogik nicht umgehen können.
 * 
 * @see TaskDto
 * 
 * @author Pavel St. (paffen)
 */
public class StatusDto {

  private static final Logger LOG = LoggerFactory.getLogger(StatusDto.class);

  private StatusEnum status;
  private String comment;
  private UserDisplayDto user;

  public StatusDto(StatusEnum status, UserDisplayDto user, String comment) {
    this.status = status;
    this.user = user;
    this.comment = comment;
  }

  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public String getComment() {
    LOG.info("StatusDto Kommentar-Update {}", comment);
    LOG.debug("StatusDto Kommentar-Update mit Parametern: {}, {}", status, comment);
    return comment;
  }

  public UserDisplayDto getUser() {
    return user;
  }

  public void setUser(UserDisplayDto user) {
    this.user = user;
  }
}
