package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.vo.UserInfoLoginVo;
import com.i000.stock.user.api.entity.vo.UserInfoRegisterVo;
import com.i000.stock.user.api.entity.vo.UserInfoVo;
import com.i000.stock.user.api.service.buiness.UserInfoService;
import com.i000.stock.user.api.service.buiness.UserLoginService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.model.UserInfo;
import com.i000.stock.user.dao.model.UserLogin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:36 2018/5/8
 * @Modified By:
 */
@Slf4j
@RestController
@RequestMapping("/userInfo")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserInfoController {

    @Resource
    private UserLoginService userLoginService;

    /**
     * 127.0.0.1:8082/userInfo/checkName
     * 用户注册前检查用户名是否重复
     *
     * @param name
     * @return
     */
    @GetMapping(value = "checkName")
    public ResultEntity checkUserName(@RequestParam String name) {
        ValidationUtils.validateParameter(name, "用户名不能为空");
        UserLogin byName = userLoginService.getByPhone(name);
        return Results.newNormalResultEntity("result", Objects.isNull(byName) ? 1: 0);
    }

    /**
     * 127.0.0.1:8082/userInfo/register
     * 用户注册
     *
     * @param userInfoRegisterVo
     * @return
     */
    @PostMapping(value = "register")
    public ResultEntity register(@RequestBody UserInfoRegisterVo userInfoRegisterVo) {
        ValidationUtils.validate(userInfoRegisterVo);
        UserLogin userInfo = ConvertUtils.beanConvert(userInfoRegisterVo, new UserLogin());
        UserLogin register = userLoginService.register(userInfo);
        return Results.newSingleResultEntity(ConvertUtils.beanConvert(register, new UserInfoVo()));
    }

    /**
     * 用户登录
     * 127.0.0.1:8082/userInfo/login
     * @param userInfoLoginVo
     * @return
     */
    @PostMapping(value = "login")
    public ResultEntity login(@RequestBody UserInfoLoginVo userInfoLoginVo) {
        ValidationUtils.validate(userInfoLoginVo);
        UserLogin userInfo = ConvertUtils.beanConvert(userInfoLoginVo, new UserLogin());
        UserLogin login = userLoginService.login(userInfo);
        return Results.newSingleResultEntity(ConvertUtils.beanConvert(login, new UserInfoVo()));
    }

}
