package com.hypergeneric.repository;

import com.hypergeneric.model.DatasourceUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DatasourceUserRepository extends JpaRepository<DatasourceUser, Long> {
    Optional<DatasourceUser> findByUsernameAndDatasourceNameAndIsActiveTrue(String username, String datasourceName);
} 