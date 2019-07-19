package com.finance.core.admin.service;

import com.finance.core.admin.entity.Role;
import com.finance.core.admin.entity.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author rstyro
 * @since 2018-12-14
 */
public interface IUserRoleService extends IService<UserRole> {
    public List<Role> getUserRoles(Integer userId) throws Exception;
}
