package cn.itcast.ssm.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.itcast.ssm.exception.CustomException;
import cn.itcast.ssm.service.SysService;

/**
 * 
 * <p>
 * Title: LoginController
 * </p>
 * <p>
 * Description: 登陆和退出
 * </p>
 * <p>
 * Company: www.itcast.com
 * </p>
 * 
 * @author 传智.燕青
 * @date 2015-3-22下午4:43:26
 * @version 1.0
 */
@Controller
public class LoginController {

    @Autowired
    private SysService sysService;

    // 用户登陆提交方法
    /**
     * 
     * <p>
     * Title: login
     * </p>
     * <p>
     * Description:
     * </p>
     * 
     * @param session
     * @param randomcode
     *            输入的验证码
     * @param usercode
     *            用户账号
     * @param password
     *            用户密码
     * @return
     * @throws Exception
     */

    /*
     * @RequestMapping("/login") public String login(HttpSession session, String
     * randomcode, String usercode, String password) throws Exception {
     * 
     * // 校验验证码，防止恶性攻击 // 从session获取正确验证码 String validateCode = (String)
     * session.getAttribute("validateCode");
     * 
     * // 输入的验证和session中的验证进行对比 if (!randomcode.equals(validateCode)) { // 抛出异常
     * throw new CustomException("验证码输入错误"); }
     * 
     * // 调用service校验用户账号和密码的正确性 ActiveUser activeUser =
     * sysService.authenticat(usercode, password);
     * 
     * // 如果service校验通过，将用户身份记录到session session.setAttribute("activeUser",
     * activeUser); // 重定向到商品查询页面 return "redirect:/first.action"; }
     */

    // 登陆提交的地址，和applicationContext-shiro中配置的loginurl一致
    @RequestMapping("/login")
    public String login(HttpServletRequest request) throws Exception {

	// 如果登陆失败，要从Request中拿到认证异常信息
	String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");

	// 根据shiro返回的异常信息路径去判断，返回指定异常信息
	if (exceptionClassName != null) {
	    if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
		throw new CustomException("账号不存在");
	    } else if (IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
		throw new CustomException("用户名/密码错误");
	    } else if ("randomcodeError".equals(exceptionClassName)) {
		throw new CustomException("验证码错误");
	    } else {
		throw new Exception();// 最终在异常处理器生成未知错误
	    }
	}

	// 此方法不处理登陆成功的跳转页面，shiro会自动认证，如果认证通过，就会自动跳转到上一个请求路径

	// 登陆失败，回到login页面继续登陆
	return "login";
    }

    // 用户退出
    /*
     * @RequestMapping("/logout") public String logout(HttpSession session)
     * throws Exception {
     * 
     * // session失效 session.invalidate(); // 重定向到商品查询页面 return
     * "redirect:/first.action";
     * 
     * }
     */

}
