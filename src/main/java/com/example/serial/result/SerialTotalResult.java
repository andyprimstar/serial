package com.example.serial.result;

import com.example.serial.data.entity.Serial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SerialTotalResult {

    private List<Serial> serials;

    private Integer total;
}
