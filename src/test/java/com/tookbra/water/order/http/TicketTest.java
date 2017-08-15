package com.tookbra.water.order.http;

import com.tookbra.water.order.bean.Ticket;
import com.tookbra.water.order.enums.HttpMethodEnum;
import com.tookbra.water.order.utils.Config;
import com.tookbra.water.order.utils.HttpUtil;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tookbra on 2017/8/7.
 */
public class TicketTest {


    @Test
    @Ignore
    public void tickerSearchTest() throws IOException {
        Map<String, Object> loginMap = new HashMap<>();
        loginMap.put("name", "");
        loginMap.put("password", "");
        Response response = HttpUtil.getInstance().requeset("login", loginMap, HttpMethodEnum.POST);
        Assert.assertEquals(200, response.code());

        response = HttpUtil.getInstance().requeset("tickenq/enq?lines_direct=(进嵊)沈家湾--泗礁&date_run=2017-08-12", null, HttpMethodEnum.GET);
        Assert.assertEquals(200, response.code());
    }

    @Test
//    @Ignore
    public void orderTickerSearchTest() throws IOException {
        Map<String, Object> loginMap = new HashMap<>();
        loginMap.put("name", "");
        loginMap.put("password", "");
        Response response = HttpUtil.getInstance().requeset("login", loginMap, HttpMethodEnum.POST);
        Assert.assertEquals(200, response.code());

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("lines_direct", "(进嵊)沈家湾--泗礁");
        paramMap.put("date_run", "2017-08-13");
        try {
            Map<String, Ticket> ticketMap = new HashMap<>();
            response = HttpUtil.getInstance().requeset("tbuy/list", null, HttpMethodEnum.GET);
            response = HttpUtil.getInstance().requeset("tbuy/enq", paramMap, HttpMethodEnum.GET);
            if(response.code() == 200) {
                Document document = Jsoup.parse(response.body().string());
                Elements trElements = document.select("tbody > tr");
                List<String> shipList = Config.getInstance().getOrderShip();
                shipList.add("17:00");
                for (int i = 0; i < trElements.size(); i++) {
                    Ticket ticket = new Ticket();
                    Elements tds = trElements.get(i).select("td");
                    String time = tds.get(0).text();
                    if(!shipList.contains(time)) {
                        continue;
                    }
                    Elements a = tds.get(9).select("a.btn-primary");
                    if(a.size() > 0) {
                        String jsLineChk = a.get(0).attr("onclick");
//                        jsLineChk('5984','07:50','20170809-15:14:29','2017-08-11')
                        ticket.setTime(time);
                        ticket.setShipName(tds.get(1).text());
                        ticket.setShipType(tds.get(2).text());
                        String top = tds.get(3).text();
                        if(top.indexOf("剩:") > 0) {
                            String topRemaining = top.substring(top.indexOf("剩:") + 2);
                            if(StringUtils.isNotBlank(topRemaining)) {
                                ticket.setTopRemaining(Integer.parseInt(topRemaining));
                            }
                            ticket.setTopName(top.substring(0,2));
                        }

                        String middle = tds.get(4).text();
                        if(middle.indexOf("剩:") > 0) {
                            String middleRemaining = middle.substring(top.indexOf("剩:") + 2);
                            if(StringUtils.isNotBlank(middleRemaining)) {
                                ticket.setMiddleRemaining(Integer.parseInt(middleRemaining));
                            }
                            ticket.setMiddleName(middle.substring(0,2));
                        }

                        String bottom = tds.get(5).text();
                        if(bottom.indexOf("剩:") > 0) {
                            String bottomRemaining = bottom.substring(bottom.indexOf("剩:") + 2);
                            if(StringUtils.isNotBlank(bottomRemaining)) {
                                ticket.setBottomRemaining(Integer.parseInt(bottomRemaining));
                            }
                            ticket.setBottomName(bottom.substring(0,2));
                        }


                        String lineChr = jsLineChk.substring(jsLineChk.indexOf("jsLineChk(") + 10, jsLineChk.length() - 1);
                        String [] lineChrArray = StringUtils.split(lineChr, ",");
                        ticket.setParams(lineChrArray);
                    } else {
                        continue;
                    }
                    ticketMap.put(time, ticket);
                    orderTicketTest(ticket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void orderTicketTest(Ticket ticket) throws IOException {
        Map<String, Object> map = new IdentityHashMap<>();
        String [] lineChrArray = ticket.getParams();
        map.put("xid", lineChrArray[0].substring(1, lineChrArray[0].length()-1));
        map.put("date_run", lineChrArray[3].substring(1, lineChrArray[3].length()-1));
        map.put("week", "0");
        map.put("lines_direct","(进嵊)沈家湾--泗礁");
        map.put("time_run", ticket.getTime());
        map.put("boat_type", ticket.getShipType());
        map.put("boat_name", ticket.getShipName());

        if(StringUtils.isNotBlank(ticket.getTopName())) {
            map.put(new String("sln"), ticket.getTopName());
            map.put(new String("slc"), ticket.getTopRemaining());
            map.put(new String("slx00"), 0);
            map.put(new String("slx01"), 0);
        }

        if(StringUtils.isNotBlank(ticket.getMiddleName())) {
            map.put(new String("sln"), ticket.getMiddleName());
            map.put(new String("slc"), ticket.getMiddleRemaining());
            map.put(new String("slx00"), 0);
            map.put(new String("slx01"), 0);
        }

        if(StringUtils.isNotBlank(ticket.getBottomName())) {
            map.put(new String("sln"), ticket.getBottomName());
            map.put(new String("slc"), ticket.getBottomRemaining());
            map.put(new String("slx00"), 1);
            map.put(new String("slx01"), 0);
        }

        Response response = HttpUtil.getInstance().requeset("tbuy/buyC", map, HttpMethodEnum.POST);
        System.out.println(1);

    }
}
