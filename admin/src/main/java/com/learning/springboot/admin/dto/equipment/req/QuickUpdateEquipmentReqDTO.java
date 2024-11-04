package com.learning.springboot.admin.dto.equipment.req;

import lombok.Data;

import java.util.List;

@Data
public class QuickUpdateEquipmentReqDTO {
    private List<RowDTO> rows;
    private List<RowDiffDTO> rowsDiff;
    private List<Integer> indexes;
    private List<RowDTO> rowsOrigin;
    private List<Object> unModifiedItems;

    @Data
    public static class RowDTO {
        private String equipmentId;
        private String name;
        private String type;
        private Integer sum;
        private Integer rent;
        private String area;
        private String col;
        private String tier;

    }

    @Data
    public static class RowDiffDTO {
        private Integer sum;
    }
}
