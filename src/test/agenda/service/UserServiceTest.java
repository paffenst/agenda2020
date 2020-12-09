package agenda.service;

import edu.hm.cs.katz.swt2.agenda.persistence.User;
import edu.hm.cs.katz.swt2.agenda.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService = new UserServiceImpl();

    @Test
    public void createUserSuccess() {
        Mockito.when(userRepository.existsById("tiffy")).thenReturn(false);
        userService.legeAn("tiffy", "Tiffy", "Tiffy123!", false);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userCaptor.capture());
        Mockito.verifyNoMoreInteractions(userRepository);
        assertEquals("tiffy", userCaptor.getValue().getLogin());
    }

    @Test
    public void shouldFailIfUserExists() {
        Mockito.when(userRepository.existsById("tiffy")).thenReturn(true);
        assertThrows(ValidationException.class, () -> {
            userService.legeAn("tiffy", "Tiffy", "Tiffy123!", false);
        });
    }

    @Test
    void createUserFailsForEmptyLogin() {
        assertThrows(ValidationException.class, () -> {
            userService.legeAn(" ", "Tiffy", "Tiffy123!", false);
        });

    }
}