package com.hmdp.interceptor;

import com.hmdp.dto.UserDTO;
import com.hmdp.utils.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.hmdp.controller.ControllerConstants.LOGIN_USER;

/**
 * @Description 登录拦截器
 * @author crocodile pole
 * @since 2023-05-4
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       //1、从session中获取用户信息
        Object user = request.getSession().getAttribute(LOGIN_USER);
        //2、用户不存在
        if(user==null){
            response.setStatus(401);
            return false;
        }
       //3、用户存在，保存到threadLocal
        UserHolder.saveUser((UserDTO) user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
