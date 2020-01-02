package com.huarui.config;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 自定义验证码 token 继承 UsernamePasswordToken
 */
public class CaptchaAuthenticationToken extends UsernamePasswordToken {

    /**
     * 服务端保存的验证码
     */
    private String serverCaptcha;

    /**
     *
     * @param code  用户code(一般是手机号码，这儿简单处理)
     * @param clientCaptcha   用户提交的验证码
     * @param serverCaptcha 服务端保存的验证码
     */
    public CaptchaAuthenticationToken(String code, String clientCaptcha,String serverCaptcha) {
        super(code, clientCaptcha);
        this.serverCaptcha = serverCaptcha;
    }

    public String getServerCaptcha() {
        return serverCaptcha;
    }

    public void setServerCaptcha(String serverCaptcha) {
        this.serverCaptcha = serverCaptcha;
    }

} 