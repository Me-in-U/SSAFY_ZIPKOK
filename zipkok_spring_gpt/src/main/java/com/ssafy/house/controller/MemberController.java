package com.ssafy.house.controller;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ssafy.house.model.dto.Member;
import com.ssafy.house.model.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private static Logger logger = LoggerFactory.getLogger(MemberController.class);

    // ! 로그인 페이지 이동
    @GetMapping("/mvLogin")
    public String getLoginPage() {
        return "member/login";
    }

    // ! 로그인 요청 처리
    @PostMapping("/login")
    public String login(
            @ModelAttribute Member member,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        logger.warn("로그인할 멤버 데이터: {}", member);
        try {
            Member userInfo = memberService.login(member);
            logger.warn("로그인한 멤버 데이터: {}", userInfo);
            // *인증 실패 시
            if (userInfo == null) {
                logger.warn("로그인 실패: {}", member.getEmail());
                return "redirect:/";
            }
            logger.warn("로그인 성공한 멤버 데이터: {}", userInfo);
            session.setAttribute("userInfo", userInfo); // * 인증 성공시 세션에 저장
            return "redirect:/";
        } catch (Exception e) {
            logger.error("로그인 중 오류 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("alertMsg", "알 수 없는 오류가 발생했습니다.");
            return "redirect:/member/mvLogin";
        }
    }

    // ! 회원가입 페이지 이동
    @GetMapping("/mvRegist")
    public String getRegistPage() {
        return "member/regist";
    }

    // ! 회원가입 요청 처리
    @PostMapping("/regist")
    public String regist(@ModelAttribute Member member, RedirectAttributes redirectAttributes) {
        logger.warn("회원가입할 멤버 데이터: {}", member);
        try {
            if (member.getName() == null || member.getName().isEmpty()) {
                redirectAttributes.addFlashAttribute("alertMsg", "이름을 입력해주세요.");
                return "redirect:/member/mvRegist";
            }
            memberService.insert(member);
            return "redirect:/";
        } catch (DuplicateKeyException e) {
            logger.warn("회원가입 중 중복된 아이디 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("alertMsg", "이미 존재하는 아이디입니다.");
            return "redirect:/member/mvRegist";
        } catch (Exception e) {
            logger.error("회원가입 중 오류 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("alertMsg", "알 수 없는 오류가 발생했습니다.");
            return "redirect:/member/mvRegist";
        }
    }

    // ! 로그아웃 요청 처리
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ! 마이페이지
    @GetMapping("/mvMyPage")
    public String getMyPage(HttpSession session) {
        Member member = (Member) session.getAttribute("userInfo");
        if (member == null) {
            return "redirect:/member/mvLogin";
        }
        return "member/myPage";
    }
}
