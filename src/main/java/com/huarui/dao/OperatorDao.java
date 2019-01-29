package com.huarui.dao;

import com.huarui.entity.Operator;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by lihui on 2019/1/29.
 */
public interface OperatorDao extends JpaRepository<Operator, String> {

    Operator findByCode(String code);

}
