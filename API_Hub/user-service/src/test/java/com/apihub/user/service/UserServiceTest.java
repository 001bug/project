package com.apihub.user.service;

import com.apihub.user.model.dto.GetCodeForBindEmailRequest;
import com.apihub.user.model.dto.UserQueryRequest;
import com.apihub.user.model.dto.VerifyCodeForBindEmailRequest;
import com.apihub.user.model.entity.User;
import com.apihub.user.model.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户服务测试
 *

 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void userRegister() {
        String userAccount = "yupi";
        String userPassword = "";
        String checkPassword = "123456";
        try {
            long result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
            userAccount = "yu";
            result = userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.assertEquals(-1, result);
        } catch (Exception e) {

        }
    }

//    @Test
//    void getListUser() {
//        User userQuery = new User();
//        UserQueryRequest userQueryRequest = new UserQueryRequest();
//        BeanUtils.copyProperties(userQueryRequest, userQuery);
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
//        List<User> userList = userService.list(queryWrapper);
//        List<UserVO> userVOList = userList.stream().map(user -> {
//            UserVO userVO = new UserVO();
//            BeanUtils.copyProperties(user, userVO);
//            return userVO;
//        }).toList();
//        System.out.println("------------------");
//        for (UserVO userVO : userVOList) {
//            System.out.println(userVO.toString());
//        }
//    }


    @Test
    void getCodeForBindEmailTest() {
        GetCodeForBindEmailRequest request = new GetCodeForBindEmailRequest();
        request.setEmail("1355609295@qq.com");
        request.setUserId(1L);
        Boolean codeForBindEmail = userService.getCodeForBindEmail(request);
        System.out.println(codeForBindEmail);
    }

    @Test
    void verifyCodeForBindEmailTest() {
        VerifyCodeForBindEmailRequest request = new VerifyCodeForBindEmailRequest();
        request.setCode("291087");
        request.setEmail("1355609295@qq.com");
        request.setUserId(1L);
        Boolean bindEmail = userService.verifyCodeForBindEmail(request);
        System.out.println(bindEmail);
    }

    @Test
    void userEmailLoginTest() {
        System.out.println(userService.userEmailLogin("1355609295@qq.com", "123456aa"));
    }
}
