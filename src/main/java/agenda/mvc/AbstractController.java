package agenda.mvc;

import agenda.common.SecurityHelper;
import agenda.service.UserService;
import agenda.service.dto.UserDisplayDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Abstrakte Basisklasse für alle Controller, sorgt dafür, dass einige
 * Verwaltungsattribute immer an die Views übertragen werden.
 * 
 * @author Pavel St. (paffen)
 *
 */
public abstract class AbstractController {

  @Autowired
  private UserService userService;

  @ModelAttribute("administration")
  private boolean isAdministrator(Authentication auth) {
    return SecurityHelper.isAdmin(auth);
  }

  @ModelAttribute("user")
  private UserDisplayDto user(Authentication auth) {
    if (auth != null) {
      UserDisplayDto anwenderInfo = userService.getUserInfo(auth.getName());
      return anwenderInfo;
    }
    return null;
  }

}
