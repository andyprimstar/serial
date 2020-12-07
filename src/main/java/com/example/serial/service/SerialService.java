package com.example.serial.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.serial.data.entity.Serial;
import com.example.serial.data.mapper.SerialMapper;
import com.example.serial.result.SerialResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class SerialService extends ServiceImpl<SerialMapper, Serial> {

    private final SerialMapper serialMapper;

    public void resetNumber() {
        serialMapper.resetNumber();
    }

    @Transactional
    public Integer next(Integer id) {
        if(serialMapper.addNumber(id) == 1) {
            return serialMapper.getNextNumber();
        }
        return null;
    }

    /*
    Question 1
     */
    public SerialResult next(Integer id, Integer limitCount) {
        return null;
    }

    /*
    Question 2
     */
    public SerialResult next(Integer id, Integer groupCount, Integer limitCount) {
        return null;
    }
}
