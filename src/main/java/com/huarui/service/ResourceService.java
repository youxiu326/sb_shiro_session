package com.huarui.service;

import com.huarui.entity.Resource;

import java.util.List;

/**
 * Created by lihui on 2019/1/29.
 */
public interface ResourceService {

    List<Resource> getResourcesByOperator(String operatorId);

    List<Resource> findAllByAutho();

}
