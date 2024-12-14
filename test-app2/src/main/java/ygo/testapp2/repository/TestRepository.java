package ygo.testapp2.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ygo.testapp2.entity.TestEntity;

public interface TestRepository extends JpaRepository<TestEntity, Long> {

    Optional<TestEntity> findByName(String name);
    Optional<TestEntity> findByEmail(String email);
}
