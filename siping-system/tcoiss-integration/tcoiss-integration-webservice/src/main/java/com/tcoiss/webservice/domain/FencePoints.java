package com.tcoiss.webservice.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class FencePoints implements Serializable {
    private static final long serialVersionUID=1L;


    private String pointX;

    private String pointY;

    public String getPoints(){
        return pointX +","+pointY;
    }
}
