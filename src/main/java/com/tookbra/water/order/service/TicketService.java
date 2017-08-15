package com.tookbra.water.order.service;

import com.tookbra.water.order.bean.Ticket;
import com.tookbra.water.order.enums.HttpMethodEnum;
import com.tookbra.water.order.utils.Config;
import com.tookbra.water.order.utils.DateUtil;
import com.tookbra.water.order.utils.HttpUtil;
import com.tookbra.water.order.view.MainFrame;
import okhttp3.FormBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by tookbra on 2017/8/7.
 */
public class TicketService {
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    public static TicketService instance = new TicketService();

    public List<String []> ticketSearch(String direct, String date) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("lines_direct", direct);
        paramMap.put("date_run", date);
        List<String []> ticketList = new ArrayList<>();
        try {
            Response response = HttpUtil.getInstance().requeset("tickenq/enq", paramMap, HttpMethodEnum.GET);
            if(response.code() == 200) {
                Document document = Jsoup.parse(response.body().string());
                Elements trElements = document.select("tbody > tr");
                for (int i = 0; i < trElements.size(); i++) {
                    Elements tds = trElements.get(i).select("td");
                    String [] ticketArray = new String[6];
                    for (int j=0; j<tds.size(); j++){
                        if(j > 5) break;

                        String txt = tds.get(j).text();
                        if(j >= 3) {
                            int start = txt.indexOf("剩:") + 2;
                            int end = txt.indexOf("至");
                            if(start != -1 && end != -1) {
                                String overStr = txt.substring(start, end);
                                ticketArray[j] = overStr;
                            } else {
                                ticketArray[j] = "0";
                            }
                        } else {
                            ticketArray[j] = txt;
                        }

                    }
                    ticketList.add(ticketArray);
                }
            }
        } catch (IOException e) {
            return Collections.EMPTY_LIST;
        }
        return ticketList;
    }

    public List<Ticket> orderSearch(String direct, String date) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("lines_direct", direct);
        paramMap.put("date_run", date);
        List<Ticket> ticketList = new ArrayList<>();
        logger.info("开始查询船票");
        try {
            Response response = HttpUtil.getInstance().requeset("tbuy/list", null, HttpMethodEnum.GET);
            response = HttpUtil.getInstance().requeset("tbuy/enq", paramMap, HttpMethodEnum.GET);
            if(response.code() == 200) {
                Document document = Jsoup.parse(response.body().string());
                Elements trElements = document.select("tbody > tr");
                List<String> shipList = Config.getInstance().getOrderShip();
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
                    String msg = String.format("匹配到航班: %s,%s(%s:%s;%s:%s;%s:%s)\n",
                            ticket.getTime(), ticket.getShipName(), ticket.getTopName(), ticket.getTopRemaining(),
                            ticket.getMiddleName(), ticket.getMiddleRemaining(),
                            ticket.getBottomName(), ticket.getBottomRemaining());
                    MainFrame.mainFrame.getLogTextArea().append(msg);
                    logger.info(msg);
                    ticketList.add(ticket);
                }
            }
        } catch (IOException e) {
            logger.error("查询船票异常:{}", e.getMessage());
            e.printStackTrace();
        }
        logger.info("结束查询船票");
        return ticketList;
    }

    public void order(List<Ticket> ticketList) {
        for(Ticket ticket : ticketList) {
            logger.info("开始下单，船票时间：{}", ticket.getTime());
            boolean flag = false;
            Integer orderNumTotal = Config.getInstance().getOrderNumTotal();
            Integer orderNum = Config.getInstance().getOrderNum();
            Integer overOrderNum = orderNumTotal - orderNum;

            FormBody.Builder requestBody = new FormBody.Builder();
            String [] lineChrArray = ticket.getParams();
            requestBody.add("xid", lineChrArray[0].substring(1, lineChrArray[0].length()-1));
            requestBody.add("date_run", lineChrArray[3].substring(1, lineChrArray[3].length()-1));
            requestBody.add("week", DateUtil.getOrderWeek().toString());
            requestBody.add("lines_direct", Config.getInstance().getShip());
            requestBody.add("time_run", ticket.getTime());
            requestBody.add("boat_type", ticket.getShipType());
            requestBody.add("boat_name", ticket.getShipName());

            Integer order = 0;

            if(StringUtils.isNotBlank(ticket.getTopName())) {
                requestBody.add(new String("sln"), ticket.getTopName());
                requestBody.add(new String("slc"), ticket.getTopRemaining().toString());
//                if(Config.getInstance().getBottomBerth() && ticket.getTopRemaining() > 0) {
//                    order = ticket.getTopRemaining() >= overOrderNum ? overOrderNum : ticket.getTopRemaining();
//                    requestBody.add(new String("slx00"), order.toString());
//                    flag = true;
//                } else {
                    requestBody.add(new String("slx00"), "0");
//                }
                requestBody.add(new String("slx01"), "0");
            }

            if(StringUtils.isNotBlank(ticket.getMiddleName())) {
                requestBody.add(new String("sln"), ticket.getMiddleName());
                requestBody.add(new String("slc"), ticket.getMiddleRemaining().toString());
//                if(Config.getInstance().getBottomBerth() && ticket.getMiddleRemaining() > 0 && !flag) {
//                    order = ticket.getMiddleRemaining() >= overOrderNum ? overOrderNum : ticket.getMiddleRemaining();
//                    requestBody.add(new String("slx00"), order.toString());
//                    flag = true;
//                } else {
//                    requestBody.add(new String("slx00"), "0");
//                }
                requestBody.add(new String("slx01"), "0");
            }

            if(StringUtils.isNotBlank(ticket.getBottomName())) {
                requestBody.add(new String("sln"), ticket.getBottomName());
                requestBody.add(new String("slc"), ticket.getBottomRemaining().toString());
                if(Config.getInstance().getBottomBerth() && ticket.getBottomRemaining() > 0) {
                    order = ticket.getBottomRemaining() >= overOrderNum ? overOrderNum : ticket.getBottomRemaining();
                    requestBody.add(new String("slx00"), order.toString());
                    flag = true;
                } else {
                    requestBody.add(new String("slx00"), "0");
                }
                requestBody.add(new String("slx01"), "0");
            }
            try {
                Response response = HttpUtil.getInstance().requeset("tbuy/buyC", requestBody.build());
                if(response.body().string().contains("舱位已锁定")) {
                    Config.getInstance().setOrderNum(order.toString());
                    String msg = String.format("预定航班成功: %s,%s\n",
                            ticket.getTime(), ticket.getShipName());
                    MainFrame.mainFrame.getLogTextArea().append(msg);
                    logger.error(msg);
                }
            } catch (IOException e) {
                logger.error("预定船票异常:{}", e.getMessage());
            }
            logger.info("结束下单");
        }
    }
}
