package com.huarui.dao;

import com.huarui.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

/**
 * Created by lihui on 2019/1/29.
 */
public interface ResourceDao extends JpaRepository<Resource, String> {

    @Query("select distinct r.resources from Operator as o left join o.roles as r where o.id = ?1")
    public List<Resource> findResourcesByOperator(String operatorId);

}
