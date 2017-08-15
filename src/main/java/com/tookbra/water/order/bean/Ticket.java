package com.tookbra.water.order.bean;

import java.io.Serializable;

/**
 * Created by tookbra on 2017/8/7.
 */
public class Ticket implements Serializable {
    private static final long serialVersionUID = -8802365652147256048L;

    private String time;

    private String shipName;

    private String shipType;

    private String topName;
    private Integer topRemaining = 0;

    private String middleName;
    private Integer middleRemaining = 0;

    private String bottomName;
    private Integer bottomRemaining = 0;

    private String [] params;

    public String getTime() {
        return time;
    }

    public Ticket setTime(String time) {
        this.time = time;
        return this;
    }

    public String getShipName() {
        return shipName;
    }

    public Ticket setShipName(String shipName) {
        this.shipName = shipName;
        return this;
    }

    public Integer getTopRemaining() {
        return topRemaining;
    }

    public Ticket setTopRemaining(Integer topRemaining) {
        this.topRemaining = topRemaining;
        return this;
    }

    public Integer getMiddleRemaining() {
        return middleRemaining;
    }

    public Ticket setMiddleRemaining(Integer middleRemaining) {
        this.middleRemaining = middleRemaining;
        return this;
    }

    public Integer getBottomRemaining() {
        return bottomRemaining;
    }

    public Ticket setBottomRemaining(Integer bottomRemaining) {
        this.bottomRemaining = bottomRemaining;
        return this;
    }

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    public String[] getParams() {
        return params;
    }

    public Ticket setParams(String[] params) {
        this.params = params;
        return this;
    }

    public String getTopName() {
        return topName;
    }

    public Ticket setTopName(String topName) {
        this.topName = topName;
        return this;
    }

    public String getMiddleName() {
        return middleName;
    }

    public Ticket setMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public String getBottomName() {
        return bottomName;
    }

    public Ticket setBottomName(String bottomName) {
        this.bottomName = bottomName;
        return this;
    }
}
