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
package ygo.traffic_hunter.core.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import ygo.traffic_hunter.core.dto.response.alarm.AlarmResponse;
import ygo.traffic_hunter.core.dto.response.alarm.DeadLetterResponse;
import ygo.traffic_hunter.core.dto.response.alarm.ThresholdResponse;
import ygo.traffic_hunter.domain.entity.alarm.Alarm;
import ygo.traffic_hunter.domain.entity.alarm.DeadLetter;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public interface AlarmRepository {

    ThresholdResponse findThreshold();

    void updateThreshold(final int cpuThreshold,
                         final int memoryThreshold,
                         final int threadThreshold,
                         final int webRequestThreshold,
                         final int webThreadThreshold,
                         final int dbcpThreshold);

    void save(Alarm alarm) throws JsonProcessingException;

    List<AlarmResponse> findAll(int limit);

    void save(DeadLetter deadLetter) throws JsonProcessingException;

    boolean existDeadLetter();

    List<DeadLetterResponse> findAllDeadLetter();

    void bulkSoftDeleteDeadLetter(List<DeadLetterResponse> deadLetters);
}
