package com.example.orders;

import com.example.orders.Courier.CourierService;
import com.example.orders.Courier.CourierServiceImpl;
import com.example.orders.Order.OrderService;
import com.example.orders.Order.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

@Controller
@RequestMapping("Couriers")
public class CourierController {
    @Autowired
    private CourierService courierService;
    @Autowired
    private OrderService orderService;

    @GetMapping("")
    public String Couriers(){
        return "Couriers";
    }


    @RequestMapping(value = "DeliverOrder/{ordernumber}", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView DeliverOrder(@PathVariable  String ordernumber, HttpSession session){
        orderService.ChangeStatus(orderService.getOrderByOrderNumber(ordernumber).getNumber());
        var message = new Message(String.format("Order %s is marked as delivered!"),"successful-order-delivery");
        var mav = new ModelAndView();
        mav.setViewName("CourierActivity/Confirm");
        mav.addObject("message",message);
        return mav;
    }
    @GetMapping("BackToSchedule")
    public ModelAndView BackToSchedule(Model model, HttpSession session){
        var mav = new ModelAndView("CourierActivity/Schedule");
        var courier = courierService.GetCourierById((Integer) session.getAttribute("courier_id"));
        var orders = courier.getOrders().stream().filter(order -> order.getStatus().equals("In process"));
        model.addAttribute("orders",orders);
      return mav;
  }
    @GetMapping("CourierLogIn")
    public ModelAndView CourierHomePage(){
        var mav = new ModelAndView();
        mav.setViewName("CourierActivity/SignIn");
        return mav;
    }
    @RequestMapping(value = "LogInCheckDetails",method = RequestMethod.POST)
    public ModelAndView LogInCheckDetails(Model model,
                                          @RequestParam String phoneNumber,
                                          @RequestParam String loginDetails,
                                          HttpSession session){

        try{
            var mav = new ModelAndView("CourierActivity/Schedule");
            var courier = courierService.LogInWithPhoneNumberAndLoginDetails(phoneNumber,loginDetails);
            var orders = courier.getOrders().stream().filter(order -> order.getStatus().equals("In process")).toList();
            session.setAttribute("courier_id",courier.getId());
            model.addAttribute("orders",orders);
            return mav;
        }catch (Exception e){
            Message message = new Message(e.getMessage(),"error-wrong-details");
            var mav = new ModelAndView("CourierActivity/Confirm");
            mav.addObject("message",message);
            return mav;
        }
    }
    @RequestMapping(value = "Apply", method = RequestMethod.POST)
    public ModelAndView SignedInCourierAccount(@RequestParam String firstName,
                                               @RequestParam String lastName,
                                               @RequestParam String phoneNumber,
                                               @RequestParam String loginDetails){

        var mav = new ModelAndView();
        Message message = null;
        try{
            courierService.RegisterNewCourier(firstName,lastName,phoneNumber,loginDetails);
            message = new Message(String.format("%s %s was registered successfully on our system! Start your shift!",firstName,lastName),"successful-registration");
        }catch(Exception e){
            message = new Message(e.getMessage(),"error-registration");
        }
        mav.setViewName("CourierActivity/Confirm");
        mav.addObject("message",message);
        return mav;
    }
    @GetMapping("SignUp")
    public ModelAndView CourierSignUp(){
        var mav = new ModelAndView();
        mav.setViewName("CourierActivity/CourierSignUp");
        return  mav;
    }
}
