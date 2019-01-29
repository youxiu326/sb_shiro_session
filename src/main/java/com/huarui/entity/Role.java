package com.huarui.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by lihui on 2019/1/29.
 * 角色
 */
@Entity
@Table(name="sys_role")
public class Role implements Serializable {


    private String id;

    /**
     * 角色
     */
    private String name;

    /**
     * 角色拥有资源
     */
    private List<Resource> resources;

    @Id
    @GeneratedValue(generator = "sys_uid")
    @GenericGenerator(name = "sys_uid", strategy = "uuid")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_role_resource", joinColumns = {
            @JoinColumn(name = "role_id", referencedColumnName = "id") },
            inverseJoinColumns = {@JoinColumn(name = "resource_id", referencedColumnName = "id") })
    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
