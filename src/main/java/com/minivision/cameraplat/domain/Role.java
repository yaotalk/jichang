package com.minivision.cameraplat.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity(name = "roles")
public class Role extends IdEntity  {

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    public RoleName getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleName roleName) {
        this.roleName = roleName;
    }

    public static enum RoleName {
        ROLE_ADMIN,ROLE_USER
    }
}
