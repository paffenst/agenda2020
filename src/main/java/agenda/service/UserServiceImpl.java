package agenda.service;

import static agenda.common.SecurityHelper.ADMIN_ROLES;
import static agenda.common.SecurityHelper.STANDARD_ROLES;

import agenda.persistence.User;
import agenda.persistence.UserRepository;
import agenda.service.dto.UserDisplayDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;

/**
 * Service-Klasse zur Verwaltung von Anwendern. Wird auch genutzt, um Logins zu
 * validieren. Servicemethoden sind transaktional und rollen alle Änderungen
 * zurück, wenn eine Exception auftritt. Service-Methoden sollten
 * <ul>
 * <li>keine Modell-Objekte herausreichen, um Veränderungen des Modells
 * außerhalb des transaktionalen Kontextes zu verhindern - Schnittstellenobjekte
 * sind die DTOs (Data Transfer Objects).
 * <li>die Berechtigungen überprüfen, d.h. sich nicht darauf verlassen, dass die
 * Zugriffen über die Webcontroller zulässig sind.</li>
 * </ul>
 *
 * @author Pavel St (paffen)
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserDetailsService, UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository anwenderRepository;

    @Autowired
    private DtoMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> findeMitspieler = anwenderRepository.findById(username);
        if (findeMitspieler.isPresent()) {
            User user = findeMitspieler.get();
            return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(),
                    user.isAdministrator() ? ADMIN_ROLES : STANDARD_ROLES);
        } else {
            throw new UsernameNotFoundException("Anwender konnte nicht gefunden werden.");
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDisplayDto> getAllUsers() {
        List<UserDisplayDto> result = new ArrayList<>();
        for (User anwender : anwenderRepository.findByAdministratorOrderByLoginAsc(false)) {
            result.add(mapper.createDto(anwender));
        }
        return result;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDisplayDto> findeAdmins() {

        // Das Mapping auf DTOs geht eleganter, ist dann aber schwer verständlich.
        List<UserDisplayDto> result = new ArrayList<>();
        for (User anwender : anwenderRepository.findByAdministrator(true)) {
            result.add(mapper.createDto(anwender));
        }
        return result;
    }

    @Override
    @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
    public UserDisplayDto getUserInfo(String login) {
        LOG.info("Lese Daten für Anwender {}.", login);
        User anwender = anwenderRepository.getOne(login);
        return mapper.createDto(anwender);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void legeAn(String login, String name, String password, boolean isAdministrator) throws ValidationException {
        LOG.info("Versuche Erstellung des Anwenders: {}", login);
        LOG.debug("Versuche Erstellung mit Parametern: {}, {}", login, isAdministrator);

        if (anwenderRepository.existsById(login)) {
            throw new ValidationException("Anwender existiert bereits!");
        }
        /* Validierung login */
        if (login.length() < 4) {
            throw new ValidationException("Logins müssen mindestens vier Zeichen haben!");
        }
        if (login.length() > 32) {
            throw new ValidationException("Logins dürfen nicht mehr als 32 Zeichen haben!");
        }
        if (!login.matches("^[a-z]+$")) {
            throw new ValidationException("Accounts dürfen nur aus Kleinbuchstaben bestehen!");
        }

        // Validierung Name
        if (name.length() < 4) {
            LOG.warn("Versuch, Username '{}' mit weniger als 4 Zeichen zu übergeben", name);
            throw new IllegalArgumentException("Usernamen müssen mind. 4 Zeichen enthalten.");
        }
        if (name.length() > 32) {
            LOG.warn("Versuch, Username '{}' mit mehr als 32 Zeichen zu übergeben", name);
            throw new IllegalArgumentException("Usernamen dürfen nicht mehr als 32 Zeichen enthalten.");
        }

        /* Validierung password */
        if (password.length() < 8) {
            throw new ValidationException("Password muss mind. 8 Zeichen lang sein!");
        }
        if (password.length() > 20) {
            throw new ValidationException("Password muss  max. 20 Zeichen lang sein");
        }
        if (!password.matches("^[?=\\S]+$")) {
            throw new ValidationException("Passwörter dürfen keine \"Whitespaces\" enthalten!!");
        }
        if (!password.matches("^(?=.*\\d.*)(?=.*[a-z].*)(?=.*[A-Z].*)(?=.*[!#$%]).{8,20}$")) {
            LOG.debug("Anlegen von Anwender {} fehlgeschlagen: Password erfuellt Formvorschrift" + "nicht", login);
            throw new ValidationException("Passwörter müssen ein Groß-und Kleinbuchstabe, ein"
                    + "Sonderzeichen(! # $ %) und eine Ziffer beinhalten!");
        }
        // Passwörter müssen Hashverfahren benennen.
        // Wir hashen nicht (noop), d.h. wir haben die
        // Passwörter im Klartext in der Datenbank (böse)
        User anwender = new User(login, name, "{noop}" + password, isAdministrator);
        anwenderRepository.save(anwender);
        LOG.info("Neuer Anwender mit Login '{}' erstellt", login);
        LOG.debug("Neuer Anwender erstellt mit Parametern: {}, {}", login, name);
    }
}
