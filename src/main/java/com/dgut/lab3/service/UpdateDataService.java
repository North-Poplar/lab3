package com.dgut.lab3.service;

import com.dgut.lab3.mapper.UpdateDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateDataService {
    @Autowired
    UpdateDataMapper updateDataMapper;

    /**
     * 增加一列
     * @param col1 新列名
     */
    public void creatNewCol(String col1){
        updateDataMapper.creatNewCol(col1);
    }

    /**
     * 添加一个数据
     * @param data 新数据
     * @param id 添加的位置
     */
    public void addColNum(String col,int data,int id){
        updateDataMapper.addColNum(col,data,id);
    }
}
