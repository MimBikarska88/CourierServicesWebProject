package com.example.orders;

import com.example.orders.City.City;
import com.example.orders.City.CityService;
import com.example.orders.City.CityServiceImpl;
import com.example.orders.Office.Office;
import com.example.orders.Office.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("CourierAdministration")
public class AdministrationController {

    @Autowired
    private CityService cityService;
    @Autowired
    private OfficeService officeService;


    @GetMapping("")
    public String CourierAdministration(){
        return "CourierAdministration";
    }

    @GetMapping("ShowCity")
    public ModelAndView ShowCity(){
           var mav = new ModelAndView();
           mav.setViewName("CourierAdministrationActivity/ShowCity");
           return mav;
    }

    @GetMapping("NewOffice")
    public ModelAndView NewAddress(){
        var addressRegister = new ModelAndView();
        addressRegister.setViewName("CourierAdministrationActivity/NewOffice");
        var cities = cityService.GetAllCities();
        addressRegister.addObject("allCities",cities);
        return addressRegister;
    }

    @RequestMapping(value = "RegisterOffice",method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView RegisterOffice(@RequestParam String address, @RequestParam Integer id){
        var city = cityService.findCityById(id);
        var office = new Office(address);
        var registeredOffice = officeService.RegisterNewOffice(office);
        city.AddOffice(registeredOffice);
        cityService.saveCity(city);
        ModelAndView officeInfo = new ModelAndView();
        officeInfo.setViewName("CourierAdministrationActivity/RegisterOffice");
        officeInfo.addObject("info","Office registered successfully!");
        return officeInfo;
    }
    @RequestMapping(value = "GetInfoCity", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView GetInfoCity(@RequestParam String cityCode, @RequestParam String cityName)
    {
            City city = cityService.FindCity(cityCode);
            ModelAndView cityInfo = new ModelAndView();
            cityInfo.setViewName("CourierAdministrationActivity/GetInfoCity");
            var offices = city.getOfficeList();
            cityInfo.addObject("offices",offices);
            return cityInfo;
    }

}
