package com.dgut.lab3.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UpdateDataMapper {
    /**
     * 增加一列
     * @param col1 新列名
     */
    public void creatNewCol(String col1);

    /**
     * 添加一个数据
     * @param data 新数据
     * @param id 添加的位置
     */
    public void addColNum(String col,int data,int id);
}
