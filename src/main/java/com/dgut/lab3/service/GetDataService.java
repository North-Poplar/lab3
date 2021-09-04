package com.dgut.lab3.service;

import com.dgut.lab3.mapper.GetDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetDataService {

    @Autowired
    GetDataMapper getDataMapper;

    public int confirmedNum(String col){
        return getDataMapper.confirmedNum(col);
    }

    public int confirmedAdd(String col1,String col2){
        return getDataMapper.confirmedAdd(col1,col2);
    }

    public List<String> TopFiveConfirmedAddCountry(String col){
        return getDataMapper.TopFiveConfirmedAddCountry(col);
    }

    public List<Integer> TopFiveConfirmedAddCountryNum(String col){
        return getDataMapper.TopFiveConfirmedAddCountryNum(col);
    }

    public String cutZero(String str){
        int len=str.length();
        String s="";
        for(int i=0,j=0;i<len;i++){
            if(i==0&&str.charAt(i)=='0'){
                continue;
            }
            if(i==3&&str.charAt(i)=='0'){
                continue;
            }
            s+=str.charAt(i);

        }
        return s;
    }
}
