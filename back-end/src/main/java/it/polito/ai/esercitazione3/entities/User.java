package it.polito.ai.esercitazione3.entities;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
  @Id
  String username;

  String password;
  String role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    ArrayList<SimpleGrantedAuthority> roles = new ArrayList<SimpleGrantedAuthority>();
    roles.add(new SimpleGrantedAuthority(role));
    return roles;
  }

  @Override
  public boolean isAccountNonExpired() {
    return isEnabled();
  }

  @Override
  public boolean isAccountNonLocked() {
    return isEnabled();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return isEnabled();
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
