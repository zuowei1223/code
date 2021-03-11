package com.tcoiss.webservice.domain;

import lombok.Data;

import java.util.List;
@Data
public class PointsVo {
    String fenceName;
    String pointsName;
    String pointsGroupId;
    String fenceType;
    String adcode;
    String adcodeName;
    List<FencePoints> drawPoints;
    
}
