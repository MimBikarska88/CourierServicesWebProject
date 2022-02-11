package com.example.orders;

import com.example.orders.Client.Client;
import com.example.orders.Client.ClientService;
import com.example.orders.Client.ClientServiceImpl;
import com.example.orders.Courier.Courier;
import com.example.orders.Courier.CourierService;
import com.example.orders.Courier.CourierServiceImpl;
import com.example.orders.Office.Office;
import com.example.orders.Office.OfficeService;
import com.example.orders.Office.OfficeServiceImpl;
import com.example.orders.Order.Order;
import com.example.orders.Order.OrderService;
import com.example.orders.Order.OrderServiceImpl;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("Clients")
@SessionAttributes("sender")
//must invalidate all session attributes in the when the logout button is pressed
public class ClientController {
    @Autowired
    private ClientService clientService;

    @Autowired
    private OfficeService officeService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CourierService courierService;

    @GetMapping("")
    public ModelAndView Clients(){
        var mav = new ModelAndView();
        mav.setViewName("Clients");
        return mav;
    }
    @GetMapping("SignIn")
    public ModelAndView SignIn(){
        var mav = new ModelAndView();
        mav.setViewName("ClientActivity/SignIn");
        return mav;
    }

    /*
    * the url was changed from /Clients/SignedInAccount to
    * /Clients/BackToSignedAccount in the confirm file with order confirmation
    *
    * */
    @GetMapping("BackToSignedAccount")
    public ModelAndView BackToSignedAccount(HttpSession session){
        var mav = new ModelAndView();
        mav.setViewName("ClientActivity/SignedInAccount");
        return mav;
    }

    @GetMapping("LogOut")
    public ModelAndView LogOut(Model model ,HttpSession session){
        session.invalidate();
        var mav = new ModelAndView();
        mav.setViewName("ClientActivity/SignIn");
        return mav;
    }
    @GetMapping("GetOrders/{id}")
    public ModelAndView GetOrders(@PathVariable Integer id, Model model){

        var mav = new ModelAndView();
        return mav;
    }

    @RequestMapping(value = "RegisterNewOrder", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView RegisterNewOrder(Model model, HttpSession session){
        var mav = new ModelAndView();
        var allOffices = officeService.GetAllAvailableBranches();
        mav.setViewName("ClientActivity/RegisterNewOrder");
        mav.addObject("allOffices",allOffices);
        return mav;
    }

    @RequestMapping(value = "ConfirmNewOrder",method = RequestMethod.POST)
    public ModelAndView ConfirmNewOrder(@RequestParam String firstname,
                                        @RequestParam String lastname,
                                        @RequestParam String phonenumber,
                                        @RequestParam Integer id, HttpSession session,Model model){
       Message message = null;
        try{
            var receiver = clientService.CheckIfReceiverExists(phonenumber,firstname,lastname);
            if(receiver==null){
                throw new Exception("receiver is null");
            }
            var sender = clientService.GetClientById((Integer) session.getAttribute("id"));
            if(sender==null){
                throw new Exception("sender is null");
            }
            System.out.println(sender.getFirstName()+"\n"+sender.getLastName()+"\n"+sender.getId());
            System.out.println(receiver.getFirstName()+"\n"+receiver.getLastName()+"\n"+receiver.getId());
            Office office = null;
            try{
                office = officeService.GetOfficeByID(id);
                System.out.println("ID IS "+id);
                System.out.println("office is "+office.getAddress());
            }catch (Exception e){
                System.out.println("error with office");
            }

            var order = new Order(sender,new Courier(),office,receiver);
            Order updatedOrder = null;
            if(order==null){
                System.out.println("order is null");
            }else{
                System.out.println("successfull order - id : "+order.getId());
            }
            try{
                updatedOrder = courierService.AssignOrder(order);
            }catch (Exception e){
                System.out.println("error with the method for assigning an order");
            }

            var orderId = updatedOrder.getId();
            orderService.GenerateOrderNumber(orderId);
            sender.AddOrder(order);
            clientService.RegisterClient(sender);
            receiver.AssignOrder(order);
            clientService.RegisterClient(receiver);
            message = new Message("Order "+order.getNumber()+" is registered successfully on our system!","successful order");
            var mav = new ModelAndView();
            mav.setViewName("ClientActivity/Confirm");
            mav.addObject("message",message);
            return mav;

        }catch (Exception e){
            message = new Message(e.getMessage(),"order registration failed");
            var mav = new ModelAndView();
            System.out.println(e.getStackTrace()+" "+e.getCause());
            mav.setViewName("ClientActivity/Confirm");
            mav.addObject("message",message);
            return mav;
        }
    }
    @RequestMapping(value = "SignedInAccount", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView SignedInAccount(Model model,@RequestParam String info_user, @RequestParam String password, HttpSession session){

        var mav = new ModelAndView();
        Message message = null;
        try{
            var client = clientService.SearchForClient(info_user,password);
            mav.setViewName("ClientActivity/SignedInAccount");
            session.setAttribute("first_name",client.getFirstName());
            session.setAttribute("last_name",client.getLastName());
            session.setAttribute("email",client.getEmail());
            session.setAttribute("phone",client.getPhoneNumber());
            session.setAttribute("id",client.getId());
        }catch(Exception e){
            message = new Message(e.getMessage(),"error with credentials");
            mav.setViewName("ClientActivity/Confirm");
            mav.addObject("message",message);
            return mav;
        }


        return mav;
    }
    @GetMapping("SignUp")
    public ModelAndView SignUp(){
        var mav = new ModelAndView();
        Message message = null;
        mav.setViewName("ClientActivity/SignUp");
        return mav;
    }
    @RequestMapping(value = "Confirm", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView Confirm(@RequestParam String firstname, @RequestParam String lastname, @RequestParam String phonenumber, @RequestParam String email, @RequestParam String password){
        var mav = new ModelAndView();
        Message message = null;
        mav.setViewName("ClientActivity/Confirm");
        try{
            var isClientRegistered  = clientService.IsClientRegistered(phonenumber,email);
            if(isClientRegistered==false){
                clientService.RegisterClient(new Client(firstname,lastname,phonenumber,email,password));
                message = new Message("Client is registered sussessfully!\nPlease, log in with your email and password to your account","success");
                mav.addObject("message",message);
                return mav;
            }else{
                message = new Message("A client with those credentials exists already!","error with registration");
                System.out.println("client is already registered");
                mav.addObject("message",message);
                return mav;
            }
        }catch(Exception e){
            // return view for error 404
        }

        return mav;
    }

    @GetMapping("OrderHistory/{id}")
    public ModelAndView OrderHistory(@PathVariable Integer id){
        var mav = new ModelAndView();
        var ordersByClient = clientService.GetAllOrdersFromClient(id);
        mav.addObject("orders",ordersByClient);
        mav.setViewName("ClientActivity/OrderHistory");
        return mav;
    }

}
