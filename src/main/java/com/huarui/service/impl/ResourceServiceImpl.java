package com.huarui.service.impl;

import com.huarui.dao.ResourceDao;
import com.huarui.entity.Resource;
import com.huarui.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lihui on 2019/1/29.
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceDao resourceDao;

    @Override
    public List<Resource> getResourcesByOperator(String operatorId) {
        return resourceDao.findResourcesByOperator(operatorId);
    }

    @Override
    public List<Resource> findAllByAutho() {
        return resourceDao.findAllByAutho();
    }
}
