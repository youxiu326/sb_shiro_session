package com.huarui.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by lihui on 2019/1/29.
 * 资源
 */
@Entity
@Table(name="sys_resource")
public class Resource implements Serializable{

    private String id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限URL
     */
    private String url;

    /**
     * 是否需要授权
     */
    private boolean authorization;

    /**
     * 上级权限
     */
    private Resource parent;

    /**
     * 下级权限
     */
    private List<Resource> children;

    @Id
    @GeneratedValue(generator = "sys_uid")
    @GenericGenerator(name = "sys_uid", strategy = "uuid")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isAuthorization() {
        return authorization;
    }

    public void setAuthorization(boolean authorization) {
        this.authorization = authorization;
    }

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    public Resource getParent() {
        return parent;
    }

    public void setParent(Resource parent) {
        this.parent = parent;
    }

    /**
     * 级联删除
     * @return
     */
    @OneToMany(cascade={CascadeType.REMOVE},mappedBy = "parent")
    public List<Resource> getChildren() {
        return children;
    }

    public void setChildren(List<Resource> children) {
        this.children = children;
    }
}
