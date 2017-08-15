package com.tookbra.water.order.service;

import com.tookbra.water.order.bean.Ticket;
import com.tookbra.water.order.utils.Config;
import com.tookbra.water.order.utils.DateUtil;

import java.util.List;

/**
 * Created by tookbra on 2017/8/8.
 */
public class OrderThread extends Thread {
    @Override
    public void run() {
        List<Ticket> ticketList = TicketService.instance.orderSearch(Config.getInstance().getShip(), DateUtil.getOrderDateStr());
        TicketService.instance.order(ticketList);
    }
}
