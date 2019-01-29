blog.youxiu326.com


默认三个账户

    admin   111111
    test    111111
    open    111111

    0. mysql redis 可直接使用

    1.测试session 是否共享 可将项目起两个 同一浏览器打开， 8088 8089
    只要8088登录了admin 则可在8099访问所有页面
    【使用ShiroConfig.java】

    2.测试session不共享 可将项目起两个 同一浏览器打开， 8088 8089
    就算8088登录了admin 在8099也无法访问需要权限的页面
    【使用resources/ShiroConfigAlone.java】

    3.数据库文件可在   【resources/mybatis.sql】



