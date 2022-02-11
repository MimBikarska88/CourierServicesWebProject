package com.example.orders.Order;

import com.example.orders.Client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Override
    public Order RegisterNewOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public List<Order> GetOrdersFromSpecifictSender(Client client) {
        List<Order>sentOrders = new LinkedList<>();
        orderRepository.findAllBySender(client).forEach(order->sentOrders.add(order));
        return sentOrders;

    }

    @Override
    public Order getOrderByOrderNumber(String orderNumber) {
        if(orderRepository.existsByNumber(orderNumber)){
           return orderRepository.getByNumber(orderNumber);
        }
        return null;
    }

    @Override
    public void ChangeStatus(String orderNumber) {
        var order = orderRepository.getByNumber(orderNumber);
        order.setStatus();
        orderRepository.save(order);

    }

    @Override
    public void GenerateOrderNumber(Integer orderId) {
        var order = orderRepository.findById(orderId).get();
        order.setNumber();
        orderRepository.save(order);
    }

    @Override
    public String GetOrderNumberById(Integer Id) {
        return orderRepository.findById(Id).get().getNumber();
    }
}
