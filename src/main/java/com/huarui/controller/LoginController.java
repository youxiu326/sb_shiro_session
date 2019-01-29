package com.huarui.controller;

import com.huarui.entity.Operator;
import com.huarui.service.OperatorService;
import com.huarui.utils.PasswordUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping("/403") //没有权限自动跳至403
    public String to403(){
        return "403";
    }

    /**
     * 跳转至指定页面
     * @param path
     * @return
     */
    @GetMapping("/to/{url}")
    public String path(@PathVariable("url")String path){
        return path;
    }

    /**
     * 登录
     * @param operator
     * @return
     */
    @RequestMapping("/loginAction")
    public @ResponseBody String loginAction(Operator operator){
        if (StringUtils.isBlank(operator.getCode())){
            return "编号不能为空";
        }
        if (StringUtils.isBlank(operator.getPassword())){
            return "密码不能为空";
        }
        UsernamePasswordToken token = new UsernamePasswordToken(operator.getCode(), operator.getPassword());
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);
        return "登录成功";
    }

    /**
     * 注册
     * @param operator
     * @return
     */
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
