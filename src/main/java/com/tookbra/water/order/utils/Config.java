package com.tookbra.water.order.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by tookbra on 2017/8/8.
 */
public class Config implements Serializable {
    private static Config instance = new Config();
    private File file;
    private Properties properties;

    private boolean topBerth;
    private boolean middleBerth;
    private boolean bottomBerth;
    private Integer orderNumTotal = 0;
    private Integer orderNum = 0;



    public static Config getInstance() {
        return instance;
    }

    private Config() {
        file = new File("config/config.properties");
        File configDir = new File("config/");
        if (!file.exists()) {
            try {
                configDir.mkdirs();
                file.createNewFile();
                init();
            } catch (IOException e) {
            }
        }
        try {
            properties = new Properties();
            properties.load(new FileInputStream(file.getAbsoluteFile()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        properties = new Properties();
        properties.setProperty("schedule.ship", "");
        properties.setProperty("schedule.orderNumTotal", "0");
        properties.setProperty("schedule.orderShips", "");
        properties.setProperty("schedule.topBerth", "false");
        properties.setProperty("schedule.middleBerth", "false");
        properties.setProperty("schedule.bottomBerth", "true");
        properties.setProperty("schedule.time", "07:30:00");
        properties.setProperty("orderNum", "0");
        save();
    }


    public void save() {
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException e) {
        }
    }

    public void setSecheduleTime(String time) {
        properties.setProperty("schedule.time", time);
    }

    public String getSechedultTime() {
        return properties.getProperty("schedule.time");
    }

    public void setShip(String ship) {
        properties.setProperty("schedule.ship", ship);
    }

    public void setOrderNum(String orderNum) {
        properties.setProperty("orderNum", orderNum);
    }

    public Integer getOrderNum() {
        String orderNum = properties.getProperty("orderNum");
        if(StringUtils.isNotBlank(orderNum)) {
            return Integer.parseInt(orderNum);
        } else {
            return this.orderNum;
        }
    }

    public Integer getOrderNumTotal() {
        String orderNumTotal = properties.getProperty("schedule.orderNumTotal");
        if(StringUtils.isNotBlank(orderNumTotal)) {
            return Integer.parseInt(orderNumTotal);
        } else {
            return this.orderNumTotal;
        }
    }

    public void setOrderNumTotal(String orderNumTotal) {
        properties.setProperty("schedule.orderNumTotal", orderNumTotal);
    }

    public String getShip() {
        return properties.getProperty("schedule.ship");
    }

    public void setOrderShip(List<String> orderShipList) {
        String orderShips = StringUtils.join(orderShipList, ",");
        properties.setProperty("schedule.orderShips", orderShips);
    }

    public List<String> getOrderShip() {
        String orderShips = properties.getProperty("schedule.orderShips");
        if(StringUtils.isNotBlank(orderShips)) {
            List<String> shipList = Arrays.asList(StringUtils.split(orderShips, ","));
            return shipList;
        }
        return new ArrayList<>();
    }

    public boolean getTopBerth() {
        return Boolean.parseBoolean(properties.getProperty("schedule.topBerth"));
    }

    public void setTopBerth(boolean topBerth) {
        properties.setProperty("schedule.topBerth", String.valueOf(topBerth));
    }

    public boolean getMiddleBerth() {
        return Boolean.parseBoolean(properties.getProperty("schedule.middleBerth"));
    }

    public void setMiddleBerth(boolean middleBerth) {
        properties.setProperty("schedule.middleBerth", String.valueOf(middleBerth));
    }


    public boolean getBottomBerth() {
        return Boolean.parseBoolean(properties.getProperty("schedule.bottomBerth"));
    }

    public void setBottomBerth(boolean bottomBerth) {
        properties.setProperty("schedule.bottomBerth", String.valueOf(bottomBerth));
    }
}
