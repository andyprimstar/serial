package com.example.serial;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@MapperScan("com.example.serial.data.mapper")
@SpringBootApplication
public class SerialApplication {

    public static void main(String[] args) {
        SpringApplication.run(SerialApplication.class, args);
    }

}
