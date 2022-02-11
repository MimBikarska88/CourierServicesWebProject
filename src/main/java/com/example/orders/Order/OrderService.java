package com.example.orders.Order;

import com.example.orders.Client.Client;

import java.util.List;

public interface OrderService {
    public Order RegisterNewOrder(Order order);
    public List<Order> GetOrdersFromSpecifictSender(Client client);
    public Order getOrderByOrderNumber(String orderNumber);
    public void ChangeStatus(String orderNumber);
    public void GenerateOrderNumber(Integer orderId);
    public String GetOrderNumberById(Integer Id);
}
