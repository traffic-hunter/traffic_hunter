package ygo.traffic_hunter.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ygo.traffic_hunter.core.dto.request.alarm.ThresholdRequest;
import ygo.traffic_hunter.core.dto.response.alarm.ThresholdResponse;
import ygo.traffic_hunter.core.service.AlarmService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/alarms")
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/threshold")
    @ResponseStatus(HttpStatus.OK)
    public ThresholdResponse getThresholdApi() {

        return alarmService.retrieveThreshold();
    }

    @PutMapping("/threshold")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateThresholdApi(@RequestBody @Valid final ThresholdRequest thresholdRequest) {

        alarmService.updateThreshold(
                thresholdRequest.cpuThreshold(),
                thresholdRequest.memoryThreshold(),
                thresholdRequest.threadThreshold(),
                thresholdRequest.webRequestThreshold(),
                thresholdRequest.webThreadThreshold(),
                thresholdRequest.dbcpThreshold()
        );
    }

}
