package com.dgut.lab3.controller;

import com.dgut.lab3.service.GetDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Controller
public class GetDataController {
    @Autowired
    GetDataService getDataService;

    @GetMapping("/getData")
    public String getData(Model model) throws ParseException {
        Date date=new Date();
        GregorianCalendar calendar = new GregorianCalendar();//asdadad
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int k=2;
        if(hour>=15){
            k=1;
        }
        String strCol1=String.format("%tD",new Date(date.getTime()-k*24*60*60*1000));
        String strCol2=String.format("%tD",new Date(date.getTime()-(k+1)*24*60*60*1000));

        //去除日期里不必要的0
        strCol1=getDataService.cutZero(strCol1);
        strCol2=getDataService.cutZero(strCol2);

        //全球确诊人数
        int confirmedNum=getDataService.confirmedNum(strCol1);
        //新增确诊人数
        int confirmedAdd=getDataService.confirmedAdd(strCol2,strCol1);
        //确诊人数最多的五个国家
        List<String> countrys=getDataService.TopFiveConfirmedAddCountry(strCol1);
        //这五个国家的确诊人数
        List<Integer> nums=getDataService.TopFiveConfirmedAddCountryNum(strCol1);
        for (int i = 0; i < countrys.size(); i++) {
            System.out.println(countrys.get(i)+"    "+nums.get(i));
        }
        model.addAttribute("confirmedNum",confirmedNum);
        model.addAttribute("confirmedAdd",confirmedAdd);
        model.addAttribute("countrys",countrys);
        model.addAttribute("nums",nums);
        return "view";
    }

}
