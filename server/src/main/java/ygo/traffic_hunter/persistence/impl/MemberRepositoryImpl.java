/**
 * The MIT License
 * <p>
 * Copyright (c) 2024 traffic-hunter.org
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ygo.traffic_hunter.persistence.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.traffichunter.query.jooq.enums.Roles;
import org.traffichunter.query.jooq.tables.records.MemberRecord;
import ygo.traffic_hunter.config.cache.CacheConfig.CacheType;
import ygo.traffic_hunter.core.repository.MemberRepository;
import ygo.traffic_hunter.domain.entity.user.Member;
import ygo.traffic_hunter.domain.entity.user.Role;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final DSLContext dsl;

    @Override
    @Transactional
    public void save(final Member member) {

        if (isAdmin(member)) {
            throw new IllegalArgumentException("already exists admin!!");
        }

        org.traffichunter.query.jooq.tables.Member jMember = org.traffichunter.query.jooq.tables.Member.MEMBER;

        int execute = dsl.insertInto(jMember,
                jMember.EMAIL,
                jMember.PASSWORD,
                jMember.ISALARM,
                jMember.ROLE
        ).values(
                member.getEmail(),
                member.getPassword(),
                member.isAlarm(),
                Roles.valueOf(member.getRole().name())
        ).execute();

        if (execute <= 0) {
            throw new IllegalArgumentException("Failed to insert member");
        }
    }

    @Override
    @Cacheable(cacheNames = CacheType.MEMBER_CACHE_NAME, key = "#id")
    public Member findById(final Integer id) {

        org.traffichunter.query.jooq.tables.Member jMember = org.traffichunter.query.jooq.tables.Member.MEMBER;

        MemberRecord memberRecord = dsl.selectFrom(jMember)
                .where(jMember.ID.eq(id))
                .fetchAny();

        MemberRecord record = Optional.ofNullable(memberRecord)
                .orElseThrow(() -> new MemberNotFoundException("not found member"));

        return mapToMember(record);
    }

    @Override
    public List<Member> findAll() {

        org.traffichunter.query.jooq.tables.Member jMember = org.traffichunter.query.jooq.tables.Member.MEMBER;

        Result<MemberRecord> records = dsl.selectFrom(jMember).fetch();

        return records.stream()
                .map(this::mapToMember)
                .toList();
    }

    @Override
    public Member findByEmail(final String email) {

        org.traffichunter.query.jooq.tables.Member jMember = org.traffichunter.query.jooq.tables.Member.MEMBER;

        MemberRecord memberRecord = dsl.selectFrom(jMember)
                .where(jMember.EMAIL.eq(email))
                .fetchAny();

        MemberRecord record = Optional.ofNullable(memberRecord)
                .orElseThrow(() -> new MemberNotFoundException("not found member"));

        return mapToMember(record);
    }

    @Override
    public Member findByEmailAndPassword(final String email, final String password) {

        org.traffichunter.query.jooq.tables.Member jMember = org.traffichunter.query.jooq.tables.Member.MEMBER;

        MemberRecord memberRecord = dsl.selectFrom(jMember)
                .where(jMember.EMAIL.eq(email).and(jMember.PASSWORD.eq(password)))
                .fetchAny();

        MemberRecord record = Optional.ofNullable(memberRecord)
                .orElseThrow(() -> new MemberNotFoundException("not found member"));

        return mapToMember(record);
    }

    @Override
    public boolean existsById(final Integer id) {

        org.traffichunter.query.jooq.tables.Member jMember = org.traffichunter.query.jooq.tables.Member.MEMBER;

        SelectConditionStep<Record1<Integer>> subQuery = dsl.selectOne()
                .from(jMember)
                .where(jMember.ID.eq(id));

        return dsl.fetchExists(subQuery);
    }

    @Override
    @Transactional
    public void update(final Member member) {

        org.traffichunter.query.jooq.tables.Member jMember = org.traffichunter.query.jooq.tables.Member.MEMBER;

        int execute = dsl.update(jMember)
                .set(jMember.EMAIL, member.getEmail())
                .set(jMember.PASSWORD, member.getPassword())
                .set(jMember.ISALARM, member.isAlarm())
                .set(jMember.ROLE, Roles.valueOf(member.getRole().name()))
                .where(jMember.EMAIL.eq(member.getEmail()))
                .execute();

        if (execute <= 0) {
            throw new IllegalArgumentException("Failed to update member");
        }
    }

    @Override
    @Transactional
    public void delete(final Member member) {

    }

    @Override
    @Transactional
    public void deleteById(final Integer id) {

        org.traffichunter.query.jooq.tables.Member jMember = org.traffichunter.query.jooq.tables.Member.MEMBER;

        int execute = dsl.delete(jMember)
                .where(jMember.ID.eq(id))
                .execute();

        if (execute <= 0) {
            throw new IllegalArgumentException("Failed to delete member");
        }
    }

    private Member mapToMember(final MemberRecord record) {

        return Member.builder()
                .id(record.getId())
                .email(record.getEmail())
                .password(record.getPassword())
                .isAlarm(record.getIsalarm())
                .role(Role.valueOf(record.getRole().name()))
                .build();
    }

    private boolean isAdmin(final Member member) {
        return Objects.equals(member.getEmail(), "admin") || Objects.equals(member.getRole(), Role.ADMIN);
    }

    public static class MemberNotFoundException extends IllegalArgumentException {

        public MemberNotFoundException() {
            super();
        }

        public MemberNotFoundException(final String s) {
            super(s);
        }

        public MemberNotFoundException(final String message, final Throwable cause) {
            super(message, cause);
        }

        public MemberNotFoundException(final Throwable cause) {
            super(cause);
        }
    }
}
