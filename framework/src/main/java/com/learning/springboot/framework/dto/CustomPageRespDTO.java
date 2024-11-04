package com.learning.springboot.framework.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CustomPageRespDTO<T> {
    private List<T> rows;
    private long total;
    private long size;
    private long current;
    private long pages;
}
