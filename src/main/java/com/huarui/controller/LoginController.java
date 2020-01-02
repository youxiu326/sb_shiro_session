package com.huarui.controller;

import com.huarui.config.CaptchaAuthenticationToken;
import com.huarui.entity.Operator;
import com.huarui.service.OperatorService;
import com.huarui.utils.PasswordUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by lihui on 2019/1/29.
 */
@Controller
@RequestMapping("/")
public class LoginController {

    @Autowired
    private OperatorService operatorService;

    @Autowired
    private RedisTemplate redisTemplate;

    private static String keyStart = "CAPTCHA_";

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


    @PostMapping("/ajax")
    public @ResponseBody Map ajax(){
        Map map = new HashMap<>();
        map.put("state","200");
        map.put("msg","成功获取到数据");
        map.put("data","youxiu326@163.com");
        return map;
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
     * 发送验证码
     * @param code
     * @return
     */
    @RequestMapping("/sendCaptcha")
    public @ResponseBody String sendCaptcha(String code){

        ValueOperations vo = redisTemplate.opsForValue();

        String captcha = "4444";

        System.out.println("验证码为 "+ captcha);
        System.out.println("验证码为 "+ captcha);

        vo.set(keyStart+code, captcha, 60, TimeUnit.SECONDS);

        return "验证码发送成功";
    }


    /**
     * 验证码登录
     * @param operator
     * @param captcha 用户获得验证码
     * @return
     */
    @RequestMapping("/captchaLogin")
    public @ResponseBody String captchaLogin(Operator operator,String captcha){
        if (StringUtils.isBlank(operator.getCode())){
            return "编号不能为空";
        }
        if (StringUtils.isBlank(captcha)){
            return "验证码不能为空";
        }
        //判断验证码是否正确
        ValueOperations vo = redisTemplate.opsForValue();
        Object serverCaptcha = vo.get(keyStart+operator.getCode());

        if(serverCaptcha==null || !captcha.equals(serverCaptcha.toString())){
            return "验证码不正确";
        }
        //【注意  这儿使用 CaptchaAuthenticationToken】
        CaptchaAuthenticationToken token = new CaptchaAuthenticationToken(operator.getCode(), captcha,serverCaptcha.toString());
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);

        //删除验证码
        redisTemplate.delete(keyStart+operator.getCode());

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
