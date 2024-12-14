package ygo.testapp2.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TestEntity {

    @Id @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    public TestEntity(final String name, final String email) {
        this.name = name;
        this.email = email;
    }
}
