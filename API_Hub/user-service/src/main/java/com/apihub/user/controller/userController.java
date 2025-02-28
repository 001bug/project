package com.apihub.user.controller;


import com.apihub.common.common.BaseResponse;
import com.apihub.common.common.DeleteRequest;
import com.apihub.common.common.ErrorCode;
import com.apihub.common.common.ResultUtils;
import com.apihub.common.exception.BusinessException;
import com.apihub.common.utils.UserHolder;
import com.apihub.user.annotation.AuthCheck;
import com.apihub.user.model.dto.*;
import com.apihub.user.model.entity.User;
import com.apihub.user.model.vo.UserKeyPairVO;
import com.apihub.user.model.vo.UserLoginVO;
import com.apihub.user.model.vo.UserVO;
import com.apihub.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.apihub.common.common.ErrorCode.*;
import static com.apihub.user.utils.UserConstant.ADMIN_ROLE;

@RestController
@Slf4j
@RequestMapping("/user")
public class userController {
    @Resource
    private UserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @ApiOperation("用户名登录")
    @PostMapping("/login")
    public BaseResponse<UserLoginVO> login(@RequestBody LoginFormDTO loginFormDTO) {
        if (loginFormDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);//抛出PARAMS_ERROR错误码
        }
        String userAccount = loginFormDTO.getUserAccount();
        String userPassword = loginFormDTO.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {//password和Account是否为空值
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserLoginVO userLoginVo = userService.login(loginFormDTO);
        return ResultUtils.success(userLoginVo);
    }

    @ApiOperation("更换ak/sk")
    @PostMapping("/changeKeyPair")
    public BaseResponse<UserKeyPairVO> changeKeyPair(@RequestBody LoginFormDTO loginFormDTO) {
        if (loginFormDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取用户填写的密码，检验是否为空
        String userPassword = loginFormDTO.getUserPassword();
        if (StringUtils.isAnyBlank(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UserKeyPairVO keyPairVO = userService.changeKeyPair(loginFormDTO);

        return ResultUtils.success(keyPairVO);
    }

    @ApiOperation("查询ak/sk")
    @GetMapping("/getPair")
    public BaseResponse<UserKeyPairVO> getKeyPair() {
        UserKeyPairVO keyPairVO = userService.getKeyPair();

        return ResultUtils.success(keyPairVO);
    }


    @ApiOperation("获取当前登录用户")
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        UserVO user;
        user = userService.getLoginUser();
        if (user == null) {
            return new BaseResponse<>(NOT_LOGIN_ERROR);
        }
        return ResultUtils.success(user);
    }

    @ApiOperation("添加用户")
    @PostMapping("/add")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userAddRequest.getUserAccount();
        String userPassword = userAddRequest.getUserPassword();
        String checkPassword = userPassword;
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return new BaseResponse(PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @ApiOperation("根据id获取用户")
    @GetMapping("/get")
//    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<UserVO> getUserById(int id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    @ApiOperation("全局搜索用户")
    @GetMapping("/list")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<List<UserVO>> listUser(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request) {
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        List<User> userList = userService.list(queryWrapper);
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    @ApiOperation("分页搜索用户")
    @GetMapping("/list/page")
    public BaseResponse<Page<UserVO>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request) {

        //检查是否登录
        getLoginUser(request);

        long current = 1;
        long size = 10;
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
            current = userQueryRequest.getCurrent();
            size = userQueryRequest.getPageSize();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        Page<User> userPage = userService.page(new Page<>(current, size), queryWrapper);
        Page<UserVO> userVOPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }

    /**
     * 更新用户
     */
    @ApiOperation("管理员更新用户")
    @PostMapping("/update")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
                                            HttpServletRequest request) {

        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        return ResultUtils.success(result);
    }

    @ApiOperation("更新用户（自己）")
    @PostMapping("/updateSelf")
    public BaseResponse<Boolean> updateUserVo(@RequestBody UserUpdateVoRequest UserUpdateVoRequest,
                                              HttpServletRequest request) {

        if (UserUpdateVoRequest == null || UserUpdateVoRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO currentUser = userService.getLoginUser();
        // 只允许改自己的
        if (!currentUser.getId().equals(UserUpdateVoRequest.getId())) {
            throw new BusinessException(NO_AUTH_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(UserUpdateVoRequest, user);
        boolean result = userService.updateById(user);
        return ResultUtils.success(result);
    }

    @ApiOperation("用户更改密码")
    @PostMapping("/updatePassword")
    public BaseResponse<Boolean> updatePassword(@RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest,
                                                HttpServletRequest request) {
        if (userUpdatePasswordRequest == null || StringUtils.isAnyBlank(userUpdatePasswordRequest.getOldPassword(),
                userUpdatePasswordRequest.getNewPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = UserHolder.getUser();
        return ResultUtils.success(userService.updatePassword(userId, userUpdatePasswordRequest));
    }

    /**
     * 删除用户
     */
    @ApiOperation("删除用户")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UserVO currentUser = userService.getLoginUser();
        //只允许自己和管理员更改
        if (!currentUser.getId().equals(deleteRequest.getId())
                && !currentUser.getUserRole().equals(ADMIN_ROLE)) {
            throw new BusinessException(NO_AUTH_ERROR);
        }

        boolean b = userService.removeById(deleteRequest.getId());
        if (b) {
            return ResultUtils.success(true);
        } else {
            return new BaseResponse<>(OPERATION_ERROR);
        }
    }

    /*
     * 使用邮箱登录
     * */
    @ApiOperation("邮箱登录")
    @PostMapping("/login/email")
    public BaseResponse<UserLoginVO> userEmailLogin(@RequestBody UserEmailLoginRequest userEmailLoginRequest, HttpServletRequest request) {
        if (userEmailLoginRequest == null || userEmailLoginRequest.getEmail() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String email = userEmailLoginRequest.getEmail();
        String password = userEmailLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(email, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.userEmailLogin(email, password));
    }

    /*
     * 退出登录
     * */
    @ApiOperation("用户退出登录")
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (UserHolder.getUser() == null) {
            log.error("用户未登录但请求退出登录");
            return ResultUtils.success(true);
        }
        userService.logout(UserHolder.getUser());

        return ResultUtils.success(true);
    }

    /*
     * 绑定邮箱发送验证码
     * */
    @ApiOperation("发送绑定邮箱验证码")
    @PostMapping("/code/bind/email")
    public BaseResponse<Boolean> getCodeForBindEmail(
            @RequestBody GetCodeForBindEmailRequest getCodeForBindEmailRequest, HttpServletRequest request) {
        if (getCodeForBindEmailRequest == null || getCodeForBindEmailRequest.getEmail() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO currentUser = userService.getLoginUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //将userId装入请求中
        GetCodeForBindEmailRequest newGetCodeForBindEmailRequest = new GetCodeForBindEmailRequest();
        newGetCodeForBindEmailRequest.setEmail(getCodeForBindEmailRequest.getEmail());
        newGetCodeForBindEmailRequest.setUserId(currentUser.getId());

        return ResultUtils.success(userService.getCodeForBindEmail(getCodeForBindEmailRequest));
    }

    /*
     * 验证绑定邮箱验证码
     * */
    @ApiOperation("验证绑定邮箱验证码")
    @PostMapping("/code/bind/email/verify")
    public BaseResponse<Boolean> verifyCodeForBindEmail(
            @RequestBody VerifyCodeForBindEmailRequest verifyCodeForBindEmailRequest, HttpServletRequest request) {
        if (verifyCodeForBindEmailRequest == null || verifyCodeForBindEmailRequest.getCode() == null
                || verifyCodeForBindEmailRequest.getEmail() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO currentUser = userService.getLoginUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        VerifyCodeForBindEmailRequest newVerifyCodeForBindEmailRequest = new VerifyCodeForBindEmailRequest();
        newVerifyCodeForBindEmailRequest.setCode(verifyCodeForBindEmailRequest.getCode());
        newVerifyCodeForBindEmailRequest.setEmail(verifyCodeForBindEmailRequest.getEmail());
        newVerifyCodeForBindEmailRequest.setUserId(currentUser.getId());

        return ResultUtils.success(userService.verifyCodeForBindEmail(newVerifyCodeForBindEmailRequest));
    }

    //发送邮箱验证码以重置密码(登录状态下操作)
    @ApiOperation("重置密码(发送邮箱验证码)")
    @PostMapping("/code/reset/password")
    public BaseResponse<Boolean> sendEmailCodeForResetPassword(
            @RequestBody EmailCodeForResetPasswordRequest emailCodeForResetPasswordRequest, HttpServletRequest request) {
        if (emailCodeForResetPasswordRequest == null || emailCodeForResetPasswordRequest.getEmail() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = UserHolder.getUser();
        EmailCodeForResetPasswordRequest newEmailCodeForResetPasswordRequest = new EmailCodeForResetPasswordRequest();
        newEmailCodeForResetPasswordRequest.setEmail(emailCodeForResetPasswordRequest.getEmail());
        newEmailCodeForResetPasswordRequest.setUserId(userId);
        return ResultUtils.success(userService.sendEmailCodeForResetPassword(newEmailCodeForResetPasswordRequest));
    }

    @ApiOperation("重置密码(验证邮箱验证码)")
    @PostMapping("/code/verify/reset/password")
    public BaseResponse<Boolean> verifyEmailCodeForResetPassword(
            @RequestBody VerifyCodeForResetPasswordRequest verifyCodeForResetPasswordRequest, HttpServletRequest request) {
        if (verifyCodeForResetPasswordRequest == null || verifyCodeForResetPasswordRequest.getEmail() == null
                || verifyCodeForResetPasswordRequest.getVerifyCode() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long userId = UserHolder.getUser();
        VerifyCodeForResetPasswordRequest newVerifyCodeForResetPasswordRequest = new VerifyCodeForResetPasswordRequest();
        newVerifyCodeForResetPasswordRequest.setEmail(verifyCodeForResetPasswordRequest.getEmail());
        newVerifyCodeForResetPasswordRequest.setVerifyCode(verifyCodeForResetPasswordRequest.getVerifyCode());
        newVerifyCodeForResetPasswordRequest.setUserId(userId);
        newVerifyCodeForResetPasswordRequest.setNewPassword(verifyCodeForResetPasswordRequest.getNewPassword());
        return ResultUtils.success(userService.verifyEmailCodeForResetPassword(newVerifyCodeForResetPasswordRequest));
    }


    //todo 七牛云头像 查询url并记录
    //todo 七牛云头像 删除
}
