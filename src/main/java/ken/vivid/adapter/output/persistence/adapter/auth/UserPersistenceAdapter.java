package ken.vivid.adapter.output.persistence.adapter.auth;

import ken.vivid.adapter.output.persistence.jpaEntities.auth.UserJpaEntity;
import ken.vivid.adapter.output.persistence.jpaRepositories.auth.UserJpaRepository;
import ken.vivid.application.port.output.auth.DeleteUser;
import ken.vivid.application.port.output.auth.LoadUser;
import ken.vivid.application.port.output.auth.SaveUser;
import ken.vivid.domain.entities.User;
import ken.vivid.domain.dto.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements LoadUser, SaveUser, DeleteUser {

    private final UserJpaRepository jpaRepository;

    @Override
    public Optional<User> loadByEmailOrUserName(String identifier) {
        return jpaRepository.findByEmailOrUserName(identifier).map(this::toDomain);
    }

    @Override
    public Optional<User> loadById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> loadByUserName(String userName) {
        return jpaRepository.findByUserName(userName).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return jpaRepository.existsByUserName(userName);
    }

    @Override
    public long countByRole(Role role) {
        return jpaRepository.countByRole(role);
    }

    @Override
    public User save(User user) {
        UserJpaEntity saved = jpaRepository.save(toEntity(user));
        return toDomain(saved);
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }

    private User toDomain(UserJpaEntity entity) {
        return User.createUser(
                entity.getId(),
                entity.getRole(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getUserName(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getHashedPassword()
        );
    }

    private UserJpaEntity toEntity(User user) {
        return UserJpaEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .hashedPassword(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .phone(user.getPhone())
                .role(user.getRole())
                .loyaltyPoints(user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0)
                .enabled(Boolean.TRUE.equals(user.getActive()))
                .build();
    }
}
