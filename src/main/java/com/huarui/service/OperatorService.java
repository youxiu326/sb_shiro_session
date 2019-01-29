package com.huarui.service;

import com.huarui.entity.Operator;

/**
 * Created by lihui on 2019/1/29.
 */
public interface OperatorService {

    Operator findByCode(String code);

    void save(Operator operator);

}
