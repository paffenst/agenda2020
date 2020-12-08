package agenda.initialization;

import agenda.common.SecurityHelper;
import agenda.service.UserService;
import agenda.service.dto.UserDisplayDto;

import java.util.List;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Initializer {

    private static final Logger LOG = LoggerFactory.getLogger(Initializer.class);

    @Autowired
    UserService anwenderService;

    @Value("${agenda.admin.login}")
    String adminLogin;

    @Value("${agenda.admin.password}")
    String adminPassword;

    /**
     * Bootstrapping: Überprüft, dass der in der Konfiguration angegebene
     * Administrator-Account existiert und legt ihn ggf. an.
     */
    @PostConstruct
    public void checkAdminAccount() {
        SecurityHelper.escalate();
        List<UserDisplayDto> adminAccounts = anwenderService.findeAdmins();
        if (adminAccounts.isEmpty()) {
            LOG.debug("No admins found. Creating configured admin account.");

            // TODO: Erzeugung verschieben!
            // TODO: Überprüfung, ob Account schon existiert.
            anwenderService.legeAn(adminLogin, "Administrator", adminPassword, true);
        }
    }

}
