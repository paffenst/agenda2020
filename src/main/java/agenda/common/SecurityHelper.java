package agenda.common;

import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Hilfsklasse für Security-Aspekte.
 * 
 * @author Pavel St. (paffen)
 */
public final class SecurityHelper {

  public static final String ADMIN_ROLE = "ROLE_ADMIN";
  public static final GrantedAuthority ADMIN_AUTHORITY = new SimpleGrantedAuthority(ADMIN_ROLE);
  public static final Collection<GrantedAuthority> ADMIN_ROLES = AuthorityUtils.createAuthorityList(ADMIN_ROLE);
  public static final Collection<GrantedAuthority> STANDARD_ROLES = AuthorityUtils.createAuthorityList();

  /**
   * Ändert die aktive Authentifizierung auf einen anonymen Administrator, um z.B.
   * die Initialisierung des Datenmodells zuzulassen. Nicht möglich, wenn es eine
   * aktive Authentifizierung gibt.
   */
  public static void escalate() {
    SecurityContext sc = SecurityContextHolder.getContext();
    if (sc.getAuthentication() == null) {

      UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken("", "", ADMIN_ROLES);
      sc.setAuthentication(authReq);
    }
  }

  /**
   * Ermittelt, ob die aktive Authentifizierung Administrator-Rechte hat.
   */
  public static boolean isAdmin(Authentication auth) {
    if (auth == null) {
      return false;
    }
    return auth.getAuthorities().contains(ADMIN_AUTHORITY);
  }

}
