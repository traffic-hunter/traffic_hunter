package ygo.testapp.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ygo.testapp.dto.TestDto;
import ygo.testapp.entity.TestEntity;
import ygo.testapp.repository.TestRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestService {

    private final TestRepository testRepository;

    @Transactional
    public TestDto join(final String name, final String email) {

        TestEntity test = new TestEntity(name, email);

        TestEntity testEntity = testRepository.save(test);

        return new TestDto(testEntity.getName(), testEntity.getEmail());
    }

    public TestDto findById(final Long id) {
        TestEntity test = testRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);

        return new TestDto(test.getName(), test.getEmail());
    }

    public TestDto findByName(final String name) {
        TestEntity test = testRepository.findByName(name)
                .orElseThrow(IllegalArgumentException::new);

        return new TestDto(test.getName(), test.getEmail());
    }

    public TestDto findByEmail(final String email) {
        TestEntity test = testRepository.findByEmail(email)
                .orElseThrow(IllegalArgumentException::new);

        return new TestDto(test.getName(), test.getEmail());
    }

    public List<TestDto> findAll() {
        return testRepository.findAll().stream()
                .map(testEntity -> new TestDto(testEntity.getName(), testEntity.getEmail()))
                .toList();
    }
}
