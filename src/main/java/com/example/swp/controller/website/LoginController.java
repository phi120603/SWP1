package com.example.swp.controller.website;

import com.example.swp.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller

public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    // Trang đăng nhập
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    // Xử lý đăng nhập
    @PostMapping("/login")
    public String login(
            @ModelAttribute LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Thực hiện xác thực
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Lưu authentication vào Security Context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lưu thông tin người dùng vào session (chỉ lưu username)
            request.getSession().setAttribute("username", authentication.getName());

            // Chuyển hướng về trang chủ hoặc trang đích
            return "redirect:/";

        } catch (BadCredentialsException e) {
            // Xử lý sai thông tin đăng nhập
            redirectAttributes.addFlashAttribute("error", "Email hoặc mật khẩu không chính xác");
            return "redirect:/SWP/login";

        } catch (Exception e) {
            // Xử lý các lỗi khác
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi đăng nhập");
            return "redirect:/SWP/login";
        }
    }
}