package com.springbikeclinic.web.domain.security;

import com.springbikeclinic.web.domain.Bike;
import com.springbikeclinic.web.domain.WorkOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;
    private String password;
    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Bike> bikes = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<WorkOrder> workOrders = new HashSet<>();

    private final static long serialVersionUID = 1L;

    @Singular
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
    private Set<Authority> authorities;

    @Builder.Default
    private Boolean accountNonExpired = true;

    @Builder.Default
    private Boolean accountNonLocked = true;

    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @Builder.Default
    private Boolean enabled = false; // default to false because of email verification requirement

    public User() {
    }

    public User(Long id, String email, String password, String firstName, String lastName,
                Set<Bike> bikes, Set<WorkOrder> workOrders, Set<Authority> authorities,
                Boolean accountNonExpired, Boolean accountNonLocked, Boolean credentialsNonExpired, Boolean enabled) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;

        if (bikes != null) {
            this.bikes = bikes;
        }

        if (workOrders != null) {
            this.workOrders = workOrders;
        }
    }

    public User(Long id, String email, String password, String firstName, String lastName, Set<Authority> authorities) {
        // enabled = false because email verification is required separately
        this(id, email, password, firstName, lastName, null, null, authorities, true, true, true, false);
    }

}
