package com.huarui.service.impl;

import com.huarui.dao.OperatorDao;
import com.huarui.entity.Operator;
import com.huarui.service.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lihui on 2019/1/29.
 */
@Service
public class OperatorServiceImpl implements OperatorService {

    @Autowired
    private OperatorDao operatorDao;

    @Override
    public Operator findByCode(String code) {
        return operatorDao.findByCode(code);
    }

    @Override
    public void save(Operator operator) {
        operatorDao.save(operator);
    }
}
