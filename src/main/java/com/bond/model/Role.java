package com.bond.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@Entity
@Table(name = "roles")
@NoArgsConstructor
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    private RoleName name;

    public Role(Long id) {
        this.id = id;
    }

    @Override
    public String getAuthority() {
        return name.name();
    }

    public enum RoleName {
        ROLE_COMPANY_OWNER,
        ROLE_ADMIN;
        private static final int ONE = 1;

        @Override
        public String toString() {
            return name();
        }

        public static RoleName fromString(String value) {
            boolean equalsSubstring;
            for (RoleName role : RoleName.values()) {
                equalsSubstring =
                        role.name()
                                .substring(role.name().indexOf("_") + ONE)
                                .equalsIgnoreCase(value);
                if (role.name().equalsIgnoreCase(value) || equalsSubstring) {
                    return role;
                }
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
    }
}
