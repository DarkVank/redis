package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.hmdp.controller.ControllerConstants.LOGIN_CODE;
import static com.hmdp.controller.ControllerConstants.LOGIN_USER;
import static com.hmdp.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Result sendCode(String phone, HttpSession session) {
       //1、校验手机号
        if(RegexUtils.isPhoneInvalid(phone)){
            return Result.fail("手机号格式错误");
        }
       //2、生成验证码
        String code = RandomUtil.randomNumbers(6);
        //3、保存到session
        session.setAttribute(LOGIN_CODE,code);
       //4、调用第三方，发送验证码（当前：日志输出）
        log.debug(code);
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        //1、校验手机号
        String phone = loginForm.getPhone();
        if(RegexUtils.isPhoneInvalid(phone)){
            return Result.fail("手机号格式错误");
        }
        //2、校验验证码
        Object cacheCode = session.getAttribute(LOGIN_CODE);
        String code = loginForm.getCode();
        if(cacheCode == null || !cacheCode.toString().equals(code) ){
            return Result.fail("验证码错误");
        }

        //3、根据手机号查询手机号
        User user = query().eq("phone",phone).one();
        //4、用户不存在
        if(user == null){
            user = createWithPhone(phone);
        }

        //5、保存用户到session
        session.setAttribute(LOGIN_USER, BeanUtil.copyProperties(user,UserDTO.class));
        return Result.ok();
    }

    private User createWithPhone(String phone) {
        //4.1、创建用户
        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX+RandomUtil.randomString(10));
        //4、2保存到db
        save(user);
        return user ;
    }
}
