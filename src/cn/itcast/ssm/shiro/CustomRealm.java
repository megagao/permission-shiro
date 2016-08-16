package cn.itcast.ssm.shiro;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.ssm.po.ActiveUser;
import cn.itcast.ssm.po.SysPermission;
import cn.itcast.ssm.po.SysUser;
import cn.itcast.ssm.service.SysService;

public class CustomRealm extends AuthorizingRealm {

    // 注入service
    @Autowired
    private SysService sysService;

    // 设置realm的名称
    @Override
    public void setName(String name) {
	super.setName("customRealm");
    }

    // 用于认证，未使用数据库进行连接
    // @Override
    // protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken
    // token) throws AuthenticationException {
    //
    // // 第一步，从token中取出用户信息
    // String userCode = (String) token.getPrincipal();
    //
    // // 第二步，根据token中用户输入的账号，从数据库中去查询
    // // ...
    // // 模拟从数据库查到了密码
    // String password = "111111";
    // SysUser sysUser = null;
    // try {
    // sysUser = sysService.findSysUserByUserCode(userCode);
    // } catch (Exception e1) {
    // e1.printStackTrace();
    // }
    //
    // // 如果查询不到，返回null
    // if (sysUser == null) {
    // return null;
    // }
    //
    //
    // // 如果查询到，返回认证信息AuthenticationInfo
    // // SimpleAuthenticationInfo simpleAuthenticationInfo2 = new
    // // SimpleAuthenticationInfo(principal, credentials, realmName);
    // // 身份信息，凭证（从数据库查到的）,realm的名字
    //
    // // activeUser就是用户的身份
    // ActiveUser activeUser = new ActiveUser();
    // activeUser.setUserid("zhangsan");
    // activeUser.setUsercode("zhangsan");
    // activeUser.setUsername("zhangsan");
    //
    // // 根据用户的id来取出菜单，通过service取出菜单
    // List<SysPermission> menus = null;
    // try {
    // menus = sysService.findMenuListByUserId("zhangsan");
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // activeUser.setMenus(menus);
    //
    // // 将用户信息设置到simpleAuthenticationInfo中
    // SimpleAuthenticationInfo simpleAuthenticationInfo = new
    // SimpleAuthenticationInfo(activeUser, password,
    // this.getName());
    // return simpleAuthenticationInfo;
    // }

    // 通过数据库查询完成realm
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
	String userCode = (String) token.getPrincipal();

	SysUser sysUser = null;

	try {
	    sysUser = sysService.findSysUserByUserCode(userCode);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	if (sysUser == null) {
	    return null;
	}

	// activeUser就是用户的身份信息
	ActiveUser activeUser = new ActiveUser();
	String password = sysUser.getPassword();

	// 拿到盐
	String salt = sysUser.getSalt();

	activeUser.setUsercode(sysUser.getUsercode());
	activeUser.setUsername(sysUser.getUsername());
	activeUser.setUserid(sysUser.getId());

	List<SysPermission> menus = null;
	try {
	    menus = sysService.findMenuListByUserId(sysUser.getId());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	activeUser.setMenus(menus);
	
//	new SimpleAuthenticationInfo(principal, hashedCredentials, credentialsSalt, realmName);
	// 身份信息，密码，盐，realm的名字

	SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(activeUser, password,
		ByteSource.Util.bytes(salt), this.getName());
	return authenticationInfo;
    }

    // 用于授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

	ActiveUser activeUser = (ActiveUser) principals.getPrimaryPrincipal();

	// 模拟从数据库获取到权限信息
	// List<String> permissions = new ArrayList<String>();

	// 如果每次都这么写，也是很繁琐的
	// permissions.add("user:create");
	// permissions.add("item:add");
	// permissions.add("item:query");

	List<SysPermission> permissionList = null;
	try {
	    permissionList = sysService.findPermissionListByUserId(activeUser.getUserid());
	} catch (Exception e) {
	    e.printStackTrace();
	}

	List<String> permissions = new ArrayList<String>();

	if (permissionList != null) {
	    for (SysPermission permission : permissionList) {
		// 将数据库的权限标识符号放入集合
		permissions.add(permission.getPercode());
	    }
	}

	SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

	simpleAuthorizationInfo.addStringPermissions(permissions);

	return simpleAuthorizationInfo;
    }

    // 清除缓存
    public void clearCached() {
	PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
	super.clearCache(principals);
    }

}
