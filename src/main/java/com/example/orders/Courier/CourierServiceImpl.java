package com.example.orders.Courier;

import com.example.orders.Message;
import com.example.orders.Order.Order;
import com.example.orders.Order.OrderRepository;
import com.example.orders.RandomNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourierServiceImpl implements CourierService{

    @Autowired
    private CourierRepository courierRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Override
    public void RegisterNewCourier(String firstName, String lastName, String phoneNumber, String loginDetails) throws Exception {
        try{
            if(!courierRepository.existsByPhoneNumber(phoneNumber)){
                courierRepository.save(new Courier(firstName,lastName,phoneNumber,loginDetails));
            }else{
                throw new Exception("Courier with such credentials exists on our system already!");
            }
        }catch (Exception e){
              throw e;
        }
    }

    @Override
    public Order AssignOrder(Order order) {
        int count = (int)courierRepository.count();
        System.out.println(count+" couriers");
        Random random = new Random();
        List<Courier>couriers = new LinkedList<>();
        courierRepository.findAll().forEach(courier -> couriers.add(courier));
        couriers.sort(new Comparator<Courier>() {
            @Override
            public int compare(Courier o1, Courier o2) {
                return Integer.compare((int)o1.getOrders().stream().count(),(int)o2.getOrders().stream().count());
            }
        });
        var courierForOrderAssignment = (couriers.stream().findFirst()).get();
        if(courierForOrderAssignment==null){
            System.out.println("courier is null");
        }else{
            System.out.println(courierForOrderAssignment.getFirstName()+" "+courierForOrderAssignment.getLastName());
        }

        courierForOrderAssignment.AddOrderToList(order);
        var orderNew = orderRepository.save(order);
        courierRepository.save(courierForOrderAssignment);
        return orderNew;
    }

    @Override
    public List<Order> GetAllAssignedOrders(String courierPhone) {
        return null;
    }

    @Override
    public boolean CheckIfCourierExists(String phoneNumber, String loginDetails) {
        return courierRepository.existsByPhoneNumberAndLoginDetails(phoneNumber,loginDetails);
    }

    @Override
    public Courier LogInWithPhoneNumberAndLoginDetails(String phoneNumber, String loginDetails) throws Exception {
        if(courierRepository.existsByPhoneNumberAndLoginDetails(phoneNumber,loginDetails)){
            return courierRepository.findCourierByPhoneNumberAndLoginDetails(phoneNumber,loginDetails);
        }else{
            throw  new Exception("Wrong details!");
        }
    }

    @Override
    public Courier GetCourierById(Integer id) {
        return courierRepository.findById(id).get();
    }
}
