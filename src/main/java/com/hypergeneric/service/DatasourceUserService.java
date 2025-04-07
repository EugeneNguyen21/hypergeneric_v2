package com.hypergeneric.service;

import com.hypergeneric.model.DatasourceUser;
import com.hypergeneric.repository.DatasourceUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DatasourceUserService {
    private final DatasourceUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<DatasourceUser> authenticateUserByName(String username, String password, String datasourceName) {
        return userRepository.findByUsernameAndDatasourceNameAndIsActiveTrue(username, datasourceName)
                .filter(user -> passwordEncoder.matches(password, user.getPasswordHash()));
    }

    public DatasourceUser createUser(String username, String password, String datasourceName) {
        DatasourceUser user = new DatasourceUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setDatasourceName(datasourceName);
        user.setActive(true);
        return userRepository.save(user);
    }
} 