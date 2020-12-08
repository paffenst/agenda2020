package agenda.service;

import agenda.service.dto.UserDisplayDto;

import javax.validation.ValidationException;
import java.util.List;

public interface UserService {

  List<UserDisplayDto> getAllUsers();

  List<UserDisplayDto> findeAdmins();

  UserDisplayDto getUserInfo(String login);

  void legeAn(String login, String name, String password, boolean isAdministrator) throws ValidationException;

}
