package com.huarui.dao;

import com.huarui.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by lihui on 2019/1/29.
 */
public interface ResourceDao extends JpaRepository<Resource, String> {
}
