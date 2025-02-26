/**
 * The MIT License
 *
 * Copyright (c) 2024 traffic-hunter.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ygo.traffic_hunter.persistence.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.UpdateConditionStep;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.traffichunter.query.jooq.tables.Agent;
import org.traffichunter.query.jooq.tables.Threshold;
import org.traffichunter.query.jooq.tables.records.DeadLetterRecord;
import org.traffichunter.query.jooq.tables.records.ThresholdRecord;
import ygo.traffic_hunter.core.alarm.message.Message;
import ygo.traffic_hunter.core.dto.response.alarm.AlarmResponse;
import ygo.traffic_hunter.core.dto.response.alarm.ThresholdResponse;
import ygo.traffic_hunter.core.repository.AlarmRepository;
import ygo.traffic_hunter.core.send.AlarmSender.AlarmException;
import ygo.traffic_hunter.domain.entity.alarm.Alarm;
import ygo.traffic_hunter.domain.entity.alarm.DeadLetter;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmRepositoryImpl implements AlarmRepository {

    private final DSLContext dsl;

    private final ObjectMapper objectMapper;

    private final Threshold jThreshold = Threshold.THRESHOLD;

    private final org.traffichunter.query.jooq.tables.Alarm jAlarm =
            org.traffichunter.query.jooq.tables.Alarm.ALARM;

    private final Agent jAgent = Agent.AGENT;

    private final org.traffichunter.query.jooq.tables.DeadLetter jDeadLetter =
            org.traffichunter.query.jooq.tables.DeadLetter.DEAD_LETTER;

    @Override
    public ThresholdResponse findThreshold() {

        ThresholdRecord thresholdRecord = dsl.selectFrom(jThreshold)
                .where(jThreshold.ID.eq(1))
                .fetchAny();

        ThresholdRecord result = Optional.ofNullable(thresholdRecord)
                .orElseThrow(() -> new AlarmException("not found threshold"));

        return new ThresholdResponse(
                result.getCpuThreshold(),
                result.getMemoryThreshold(),
                result.getThreadThreshold(),
                result.getWebRequestThreshold(),
                result.getWebThreadThreshold(),
                result.getDbcpThreshold()
        );
    }

    @Override
    @Transactional
    public void updateThreshold(final int cpuThreshold,
                                final int memoryThreshold,
                                final int threadThreshold,
                                final int webRequestThreshold,
                                final int webThreadThreshold,
                                final int dbcpThreshold) {

        int execute = dsl.update(jThreshold)
                .set(jThreshold.CPU_THRESHOLD, cpuThreshold)
                .set(jThreshold.MEMORY_THRESHOLD, memoryThreshold)
                .set(jThreshold.THREAD_THRESHOLD, threadThreshold)
                .set(jThreshold.WEB_REQUEST_THRESHOLD, webRequestThreshold)
                .set(jThreshold.WEB_THREAD_THRESHOLD, webThreadThreshold)
                .set(jThreshold.DBCP_THRESHOLD, dbcpThreshold)
                .where(jThreshold.ID.eq(1))
                .execute();

        if(execute <= 0) {
            throw new AlarmException("not update threshold");
        }
    }

    @Override
    public boolean existDeadLetter() {

        SelectConditionStep<Record1<Integer>> exist = dsl.selectOne()
                .from(jDeadLetter)
                .where(jDeadLetter.IS_DELETE.eq(false));

        return dsl.fetchExists(exist);
    }

    @Override
    @Transactional
    public void save(final Alarm alarm) throws JsonProcessingException {

        int execute = dsl.insertInto(jAlarm,
                jAlarm.TIME,
                jAlarm.ALARM_DATA,
                jAlarm.AGENT_ID
            ).values(
                alarm.time().atOffset(ZoneOffset.UTC),
                JSONB.jsonb(objectMapper.writeValueAsString(alarm.message())),
                alarm.agent_id()
            ).execute();

        if(execute <= 0) {
            throw new AlarmException("not save alarm");
        }
    }

    @Override
    public List<AlarmResponse> findAll(final int limit) {

        Result<Record3<OffsetDateTime, JSONB, String>> result = dsl.select(
                        jAlarm.TIME.as("time"),
                        jAlarm.ALARM_DATA.as("alarm_data"),
                        jAgent.AGENT_NAME.as("agent_name")
                )
                .from(jAlarm)
                .join(jAgent)
                .on(jAlarm.AGENT_ID.eq(jAgent.ID))
                .orderBy(jAlarm.TIME.desc())
                .limit(limit)
                .fetch();

        return result.stream()
                .map(record -> {
                    try {
                        return new AlarmResponse(
                                    record.component1().toInstant(),
                                    objectMapper.readValue(record.component2().data(), Message.class),
                                    record.component3()
                                );
                    } catch (JsonProcessingException e) {
                        throw new AlarmException("alarm json deserialization failed", e);
                    }
                }).toList();
    }

    @Override
    @Transactional
    public void save(final DeadLetter deadLetter) throws JsonProcessingException {

        int execute = dsl.insertInto(jDeadLetter,
                jDeadLetter.DEAD_LETTER_DATA,
                jDeadLetter.IS_DELETE
        ).values(
                JSONB.jsonb(objectMapper.writeValueAsString(deadLetter.getMessage())),
                deadLetter.isDelete()
        ).execute();

        if (execute <= 0) {
            throw new AlarmException("not save dead letter");
        }
    }

    @Override
    public List<DeadLetter> findAllDeadLetter() {

        Result<DeadLetterRecord> result = dsl.selectFrom(jDeadLetter)
                .where(jDeadLetter.IS_DELETE.eq(false))
                .fetch();

        return result.stream()
                .map(rst -> {
                    try {
                        return DeadLetter.builder()
                                .id(rst.getId())
                                .message(objectMapper.readValue(rst.getDeadLetterData().data(), Message.class))
                                .isDelete(rst.getIsDelete())
                                .build();
                    } catch (JsonProcessingException e) {
                        throw new AlarmException("dead letter json deserialization failed", e);
                    }
                })
                .toList();
    }

    @Override
    @Transactional
    public void bulkSoftDeleteDeadLetter(final List<DeadLetter> deadLetters) {

        List<UpdateConditionStep<DeadLetterRecord>> results = deadLetters.stream()
                .map(deadLetter -> dsl.update(jDeadLetter)
                        .set(jDeadLetter.IS_DELETE, true)
                        .where(jDeadLetter.ID.eq(deadLetter.getId()))
                ).toList();

        dsl.batch(results).execute();
    }

    @Override
    @Transactional
    public void clearAlarm() {
        dsl.deleteFrom(jAlarm).execute();
    }

    @Override
    @Transactional
    public void clearDeadLetter() {
        dsl.deleteFrom(jDeadLetter).execute();
    }
}
