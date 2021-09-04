package com.dgut.lab3.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GetDataMapper {
    public int confirmedNum(String col);

    public int confirmedAdd(String col1,String col2);

    public List<String> TopFiveConfirmedAddCountry(String col);

    public List<Integer> TopFiveConfirmedAddCountryNum(String col);

}