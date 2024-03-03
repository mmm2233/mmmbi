package com.mmm.springbootinit.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 *@Date：2024/3/2
 *@Author：mmm
 *@return：
 * 用户登录请求
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;
}
