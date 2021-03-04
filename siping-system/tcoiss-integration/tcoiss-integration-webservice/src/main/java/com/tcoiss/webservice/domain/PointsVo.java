package com.tcoiss.webservice.domain;

import lombok.Data;

import java.util.List;
@Data
public class PointsVo {
    String cacheKey;
    List<FencePoints> drawPoints;
    
}
