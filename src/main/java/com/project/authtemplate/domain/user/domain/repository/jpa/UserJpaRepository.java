package com.project.authtemplate.domain.user.domain.repository.jpa;

import com.project.authtemplate.domain.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByCompanyIdAndEmail(String companyId, String email);

    boolean existsByEmail(String email);

}
