package edu.hm.cs.katz.swt2.agenda.it;

import edu.hm.cs.katz.swt2.agenda.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("none")
@Transactional
public class UserCreationIT {

  @Autowired
  UserService userService;

  @Test
  @WithUserDetails("admin")
  public void createdUserContainsAllInformation() {

    userService.legeAn("maxi", "Maximilian", "max123!M", false);

    var createdUser = userService.getUserInfo("maxi");

    assertEquals("maxi", createdUser.getLogin());
    assertEquals("Maximilian", createdUser.getName());
    assertEquals("Maximilian", createdUser.getDisplayName());
    assertEquals(0, createdUser.getSubscriptionCount());
    assertEquals(0, createdUser.getTopicCount());
  }

  @Test
  @WithUserDetails("admin")
  public void testValidationExceptionWhenCreatingDuplicateUser() {

    userService.legeAn("maxi", "Maximilian", "max123!M", false);

    assertThrows(ValidationException.class, () -> {
      userService.legeAn("maxi", "Maximilian", "max123!M", false);
    });
  }
}
