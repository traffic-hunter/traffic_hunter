package ygo.traffic_hunter.persistence.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.traffichunter.query.jooq.tables.records.ThresholdRecord;
import ygo.traffic_hunter.core.dto.response.alarm.ThresholdResponse;
import ygo.traffic_hunter.core.repository.AlarmRepository;
import ygo.traffic_hunter.core.send.AlarmSender.AlarmException;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmRepositoryImpl implements AlarmRepository {

    private final DSLContext dsl;

    private final org.traffichunter.query.jooq.tables.Threshold jThreshold =
            org.traffichunter.query.jooq.tables.Threshold.THRESHOLD;

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
}
