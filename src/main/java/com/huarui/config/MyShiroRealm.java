package com.huarui.config;

import com.huarui.entity.Operator;
import com.huarui.entity.Resource;
import com.huarui.service.OperatorService;
import com.huarui.service.ResourceService;
import com.huarui.utils.PasswordUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by lihui on 2019/1/29.
 *
 * 自定义Realm
 */
public class MyShiroRealm extends AuthorizingRealm{

    @Autowired
    private OperatorService operatorService;

    @Autowired
    private ResourceService resourceService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //取出当前登录用户
        Operator operator = (Operator) principalCollection.getPrimaryPrincipal();
        //获得当前登录用户所拥有的所有资源
        List<Resource> resources = resourceService.getResourcesByOperator(operator.getId());
        if(resources!=null && resources.size()>0){
            for(Resource p:resources){
                authorizationInfo.addStringPermission(p.getUrl());//添加权限
            }
        }

        return authorizationInfo;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //获取用户编号 与密码
        UsernamePasswordToken upToken = (UsernamePasswordToken)authenticationToken;
        String usercode = (String)upToken.getPrincipal();
        String password = String.valueOf(upToken.getPassword());
        Operator operator = operatorService.findByCode(usercode);
        if (operator==null){
            throw new AuthenticationException("用户编号不存在");
        }else if(!operator.getPassword().equals(PasswordUtil.saltAndMd5(password,operator.getName(),2))){
            throw new AuthenticationException("密码不正确");
        }
        //返回认证信息由父类 进行认证
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                operator, //用户名
                password, //密码
                ByteSource.Util.bytes(operator.getName()),//盐
                getName()  //realm name
        );
        return authenticationInfo;
    }

    //清空shiro缓存的权限
    public void clearAuthz(){
        this.clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
    }
}
