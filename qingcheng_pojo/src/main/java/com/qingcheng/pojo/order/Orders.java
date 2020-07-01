package com.qingcheng.pojo.order;

import java.io.Serializable;
import java.util.List;

/**
 * @author 戴金华
 * @date 2019-10-26 19:55
 */
public class Orders implements Serializable {
    private Order order;
    private List<OrderItem> orderItem;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<OrderItem> orderItem) {
        this.orderItem = orderItem;
    }
}
