package com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.model.Role;
import com.mindtree.testingpractice.acoe.dtep.authservers.token.auth.server.model.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
