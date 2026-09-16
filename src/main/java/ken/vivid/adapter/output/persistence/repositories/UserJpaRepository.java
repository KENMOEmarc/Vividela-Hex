package ken.vivid.adapter.output.persistence.repositories;

import ken.vivid.adapter.output.persistence.entities.UserJpaEntity;
import ken.vivid.domain.dto.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByUserName(String userName);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.email = :identifier OR u.userName = :identifier")
    Optional<UserJpaEntity> findByEmailOrUserName(@Param("identifier") String identifier);

    boolean existsByUserName(String userName);

    long countByRole(Role role);
}
