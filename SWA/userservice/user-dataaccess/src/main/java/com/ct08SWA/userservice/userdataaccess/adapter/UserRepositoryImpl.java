package com.ct08SWA.userservice.userdataaccess.adapter;

import com.ct08SWA.userservice.userapplicationservice.ports.outputports.UserRepository;
import com.ct08SWA.userservice.userdataaccess.entity.UserEntity;
import com.ct08SWA.userservice.userdataaccess.mapper.UserDataAccessMapper;
import com.ct08SWA.userservice.userdataaccess.repository.UserJpaRepository;
import com.ct08SWA.userservice.userdomaincore.entity.User;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter (Secondary Adapter): Triển khai UserRepository.
 */
@Component
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserDataAccessMapper userDataAccessMapper;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository,
                              UserDataAccessMapper userDataAccessMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userDataAccessMapper = userDataAccessMapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = userDataAccessMapper.userToUserEntity(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return userDataAccessMapper.userEntityToUser(savedEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(userDataAccessMapper::userEntityToUser);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findById(UUID id){
        return userJpaRepository.findById(id)
                .map(userDataAccessMapper::userEntityToUser);
    }
}