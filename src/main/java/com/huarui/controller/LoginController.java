package com.huarui.controller;

import com.huarui.entity.Operator;
import com.huarui.service.OperatorService;
import com.huarui.utils.PasswordUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lihui on 2019/1/29.
 */
@Controller
@RequestMapping("/")
public class LoginController {

    @Autowired
    private OperatorService operatorService;

    @RequestMapping("/")
    public String tologin(){
        return "login";
    }

    @RequestMapping("/login") //未登录自动跳至login
    public String login(){
        return "login";
    }

    @RequestMapping("/register")
    public @ResponseBody String register(Operator operator){

        //TODO 编号重复验证

        if (StringUtils.isBlank(operator.getName())){
            return "昵称不能为空";
        }
        if (StringUtils.isBlank(operator.getCode())){
            return "编号不能为空";
        }
        if (StringUtils.isBlank(operator.getPassword())){
            return "密码不能为空";
        }
        operator.setPassword(PasswordUtil.saltAndMd5(operator.getPassword(),operator.getName(),2));
        operatorService.save(operator);
        return "注册成功";
    }
}
