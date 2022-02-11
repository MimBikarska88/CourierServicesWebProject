package com.example.orders.Courier;

import com.example.orders.Office.Office;
import com.example.orders.Order.Order;

import java.util.List;

public interface CourierService {
    public void RegisterNewCourier(String firstName, String lastName,String phoneNumber, String loginDetails) throws Exception;
    public Order AssignOrder(Order order);
    public List<Order> GetAllAssignedOrders(String courierPhone);
    public boolean CheckIfCourierExists(String phoneNumber, String loginDetails);
    public Courier LogInWithPhoneNumberAndLoginDetails(String phoneNumber, String loginDetails) throws Exception;
    public Courier GetCourierById(Integer id);

}
