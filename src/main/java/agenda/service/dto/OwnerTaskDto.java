package agenda.service.dto;

import java.util.List;

/**
 * Transferobjekt für Tasks mit Metadaten, die nur für Verwalter eines Tasks
 * (d.h. Eigentümer des Topics) sichtbar sind. Transferobjekte sind
 * Schnittstellenobjekte der Geschäftslogik; Sie sind nicht Teil des Modells, so
 * dass Änderungen an den Transferobjekten die Überprüfungen der Geschäftslogik
 * nicht umgehen können.
 * 
 * @see TaskDto
 * 
 * @author Pavel St. (paffen)
 */
public class OwnerTaskDto extends TaskDto {

  private int erledigtStatusCount;

  private List<StatusDto> statusComment;

  public OwnerTaskDto(Long id, String title, SubscriberTopicDto topicDto, String shortDescription,
      String longDescription) {
    super(id, title, topicDto, shortDescription, longDescription);
  }

  public int getErledigtStatusCount() {
    return erledigtStatusCount;
  }

  public void setErledigtStatusCount(int erledigtStatusCount) {
    this.erledigtStatusCount = erledigtStatusCount;
  }

  public List<StatusDto> getStatusComment() {
    return statusComment;
  }

  public void setStatusComment(List<StatusDto> statusComment) {
    this.statusComment = statusComment;
  }

}
