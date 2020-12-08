package agenda.service.dto;

/**
 * Transferobjekt für Verwaltungsinformationen von Anwendern. Transferobjekte
 * sind Schnittstellenobjekte der Geschäftslogik; Sie sind nicht Teil des
 * Modells, so dass Änderungen an den Transferobjekten die Überprüfungen der
 * Geschäftslogik nicht umgehen können.
 *
 * @author Pavel St. (paffen)
 */
public class UserManagementDto extends UserDisplayDto {

  private String password = "";

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
