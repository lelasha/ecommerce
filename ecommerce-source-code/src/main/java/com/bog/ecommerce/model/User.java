package com.bog.ecommerce.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Setter
@Getter
//@RequiredArgsConstructor
@Table(name="users")
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String personalId;
    private String email;
    private String password;
    private String IBAN;
    private BigDecimal balance;
    private boolean enabled;




    public User() {
        this.balance = new BigDecimal("0");
        this.enabled=false;
    }

    public User(String firstName, String lastName, String personalId, String email, String password,String IBAN) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.email = email;
        this.password = password;
        this.balance = new BigDecimal("0");
        this.enabled=false;
        this.IBAN=IBAN;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
