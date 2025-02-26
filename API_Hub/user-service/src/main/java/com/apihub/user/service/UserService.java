package com.apihub.user.service;

import com.apihub.user.model.dto.*;
import com.apihub.user.model.entity.User;
import com.apihub.user.model.vo.UserKeyPairVO;
import com.apihub.user.model.vo.UserLoginVO;
import com.apihub.user.model.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.IService;
import me.zhyd.oauth.model.AuthResponse;

/**
* @author IKUN
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-10-22 23:40:52
*/
public interface UserService extends IService<User> {

    long userRegister(String userAccount, String userPassword, String checkPassword);

    UserLoginVO login(LoginFormDTO loginFormDTO);

    UserVO getLoginUser();

    Boolean checkUserAK(String accessKey, String sign);

    UserKeyPairVO changeKeyPair(LoginFormDTO loginFormDTO);

    UserKeyPairVO getKeyPair();

    Boolean getCodeForBindEmail(GetCodeForBindEmailRequest getCodeForBindEmailRequest);

    Boolean verifyCodeForBindEmail(VerifyCodeForBindEmailRequest newVerifyCodeForBindEmailRequest);

    UserLoginVO userEmailLogin(String email, String password);

    void logout(Long currentUserId);

    UserLoginVO giteeLoginCallback(AuthResponse response);

    Boolean sendEmailCodeForResetPassword(EmailCodeForResetPasswordRequest newEmailCodeForResetPasswordRequest);

    Boolean verifyEmailCodeForResetPassword(VerifyCodeForResetPasswordRequest newVerifyCodeForResetPasswordRequest);

    Boolean updatePassword(Long userId, UserUpdatePasswordRequest userUpdatePasswordRequest);
}
