package com.example.serial.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.serial.data.entity.Serial;
import com.example.serial.result.SerialResult;
import com.example.serial.result.SerialTotalResult;
import com.example.serial.service.SerialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Api(tags = "Serial API")
@RestController
@RequestMapping("/serials")
@RequiredArgsConstructor
public class SerialController {

    private final SerialService serialService;

    @ApiOperation(value = "Find All Serial")
    @GetMapping()
    public SerialTotalResult findAll() {
        return convertTotalResult(serialService.list());
    }

    @ApiOperation(value = "Find Serial By Id")
    @GetMapping("/{id}")
    public Serial find(
            @ApiParam(value = "Serial Id", example = "1")
            @PathVariable Integer id
    ) {
        return serialService.getById(id);
    }

    @ApiOperation(value = "Get Next Serial Number By Id")
    @PostMapping("/next")
    public Serial next(
            @ApiParam(value = "Serial Id", example = "1")
            @RequestParam Integer id
    ) {
        serialService.next(id);
        return serialService.getById(id);
    }

    @ApiOperation(value = "Get Next Serial Number By Id And Limit Count")
    @PostMapping("/q1/next")
    public Serial nextQ1(
            @ApiParam(value = "Serial Id", example = "1")
            @RequestParam Integer id
            , @ApiParam(value = "Limit Count", example = "5")
            @RequestParam Integer limitCount
    ) {
        serialService.next(id, limitCount);
        return serialService.getById(id);
    }

    @ApiOperation(value = "Get Next Serial Number By Id And Group Count And Limit Count")
    @PostMapping("/q2/next")
    public SerialTotalResult nextQ2(
            @ApiParam(value = "Serial Id", example = "1")
            @RequestParam Integer id
            , @ApiParam(value = "Group Count", example = "3")
            @RequestParam Integer groupCount
            , @ApiParam(value = "Limit Count", example = "5")
            @RequestParam Integer limitCount
    ) {
        serialService.next(id, groupCount, limitCount);
        return convertTotalResult(serialService.list());
    }

    @ApiOperation(value = "Get Next Serial Number By Id And Limit Count")
    @PostMapping("/q1/next/test")
    public String testNextQ1(
            @ApiParam(value = "Serial Id", example = "1")
            @RequestParam Integer id
            , @ApiParam(value = "Request Count", example = "10000")
            @RequestParam Integer requestCount
            , @ApiParam(value = "Limit Count", example = "100000")
            @RequestParam Integer limitCount
            , @ApiParam(value = "Thread Count", example = "10")
            @RequestParam Integer threadCount
    ) {
        serialService.resetNumber();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Callable<Set<Integer>>> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                SerialResult result;
                Set<Integer> uniqueNum = new HashSet<>();

                for (int j=0; j<requestCount; j++) {
                    result = serialService.next(id, limitCount);

                    if (result.getStatus()) {
                        uniqueNum.add(result.getNumber());
                    }
                }

                return uniqueNum;
            });
        }

        long start = System.currentTimeMillis();
        Set<Integer> resultSet = new HashSet<>(limitCount);
        try {
            List<Future<Set<Integer>>> futures = executorService.invokeAll(tasks);
            for (Future<Set<Integer>> future : futures) {
                resultSet.addAll(future.get());
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        long spend = System.currentTimeMillis() - start;

        Serial serial = serialService.getById(id);

        Integer numberCount = (1 + serial.getNumber()) * serial.getNumber() / 2;
        Integer resultNumberCount = 0;
        for (Integer number : resultSet) {
            resultNumberCount += number;
        }

        if(resultSet.size() != serial.getNumber() || !numberCount.equals(resultNumberCount)) {
            return String.format("FAIL!!!!!\nSerial Number:%d,實際要號個數:%d\nSerial Number Count:%d, 實際要號總和:%d"
            , serial.getNumber()
            , resultSet.size()
            , numberCount
            , resultNumberCount
            );
        }

        Integer total = requestCount * threadCount;
        return String.format("要號請求次數:%d\n耗費時間：%d\n每秒要號次數：%d\nSerial Number:%d,實際要號個數:%d\nSerial Number Count:%d, 實際要號總和:%d\n明細:\n%s"
                , total
                , spend
                , total * 1000 / spend
                , serial.getNumber()
                , resultSet.size()
                , numberCount
                , resultNumberCount
                , serial.toString()
        );
    }

    @ApiOperation(value = "Get Next Serial Number By Id And Group Count And Limit Count")
    @PostMapping("/q2/next/test")
    public String testNextQ2(
            @ApiParam(value = "Serial Id", example = "1")
            @RequestParam Integer id
            , @ApiParam(value = "Request Count", example = "10000")
            @RequestParam Integer requestCount
            , @ApiParam(value = "Group Count", example = "10")
            @RequestParam Integer groupCount
            , @ApiParam(value = "Limit Count", example = "10000")
            @RequestParam Integer limitCount
            , @ApiParam(value = "Thread Count", example = "10")
            @RequestParam Integer threadCount
    ) {
        serialService.resetNumber();

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Callable<Map<Integer, Set<Integer>>>> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                SerialResult result;
                Map<Integer, Set<Integer>> groupMap = new HashMap<>(groupCount);

                for (int j=0; j<requestCount; j++) {
                    result = serialService.next(id, groupCount, limitCount);

                    if (result.getStatus()) {
                        Set<Integer> uniqueNum = groupMap.get(result.getId());
                        if(uniqueNum == null) {
                            groupMap.put(result.getId(), uniqueNum = new HashSet<>());
                        }
                        uniqueNum.add(result.getNumber());
                    }
                }

                return groupMap;
            });
        }

        long start = System.currentTimeMillis();
        List<Map<Integer, Set<Integer>>> results = new ArrayList<>();
        try {
            List<Future<Map<Integer, Set<Integer>>>> futures = executorService.invokeAll(tasks);
            for (Future<Map<Integer, Set<Integer>>> future : futures) {
                results.add(future.get());
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        long spend = System.currentTimeMillis() - start;

        Integer serialTotal = 0;
        Integer serialNumberCount = 0;

        List<Serial> serials = serialService.list(new QueryWrapper<Serial>().between("id", 1, groupCount));
        for (Serial serial : serials) {
            serialTotal += serial.getNumber();
            serialNumberCount += (1 + serial.getNumber()) * serial.getNumber() / 2;
        }

        Integer resultTotal = 0;
        Integer resultNumberCount = 0;

        for (Map<Integer, Set<Integer>> map : results) {
            for(Map.Entry<Integer, Set<Integer>> entry : map.entrySet()) {
                resultTotal += entry.getValue().size();
                for(Integer number : entry.getValue()) {
                    resultNumberCount += number;
                }
            }
        }

        if(!serialTotal.equals(resultTotal) || !serialNumberCount.equals(resultNumberCount)) {
            return String.format("FAIL!!!!!\nSerial Number:%d,實際要號個數:%d\nSerial Number Count:%d, 實際要號總和:%d"
                    , serialTotal
                    , resultTotal
                    , serialNumberCount
                    , resultNumberCount
            );
        }

        Integer total = requestCount * threadCount;
        return String.format("要號請求次數:%d\n耗費時間：%d\n每秒要號次數：%d\nSerial Number:%d,實際要號個數:%d\nSerial Number Count:%d, 實際要號總和:%d\n明細:\n%s"
                , total
                , spend
                , total * 1000 / spend
                , serialTotal
                , resultTotal
                , serialNumberCount
                , resultNumberCount
                , serials.toString()
        );
    }

    private SerialTotalResult convertTotalResult(List<Serial> serials) {
        Integer total = 0;
        for (Serial serial : serials) {
            total += serial.getNumber();
        }
        return new SerialTotalResult(serials, total);
    }
}
