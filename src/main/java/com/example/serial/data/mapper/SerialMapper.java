package com.example.serial.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.serial.data.entity.Serial;
import com.example.serial.result.SerialResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SerialMapper extends BaseMapper<Serial> {

    @Update("UPDATE serial SET number = 0")
    int resetNumber();

    @Update("UPDATE serial SET number = @next := (number + 1) WHERE id = #{id}")
    int addNumber(@Param("id") Integer id);

    @Select("SELECT @next")
    Integer getNextNumber();
}
