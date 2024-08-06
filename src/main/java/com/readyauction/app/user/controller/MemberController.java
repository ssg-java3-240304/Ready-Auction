package com.readyauction.app.user.controller;

import com.readyauction.app.user.dto.MemberDTO;
import com.readyauction.app.user.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequestMapping(value = "/member")
public class MemberController {
    // 생성자 주입
    @Autowired
    private MemberService memberService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 회원가입 페이지 출력 요청
    @GetMapping("/save")
    public String saveForm() {
        return "/member/register";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute MemberDTO memberDTO) {
//        System.out.println("MemberController.save");
//        System.out.println("memberDTO = " + memberDTO);
        // 비밀번호 암호화해서 저장
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        memberDTO.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        memberService.save(memberDTO);
        return "/member/login";
    }

    @GetMapping("/login")
    public void loginForm() {
    }

    @PostMapping("/login-post")
    public String loginPost(@ModelAttribute MemberDTO memberDTO, HttpSession session, Model model) {
        System.out.println("로그인중");
        MemberDTO loginResult = memberService.login(memberDTO);
        if (loginResult != null) {
            // login 성공
            session.setAttribute("email", loginResult.getEmail());
            session.setAttribute("name", loginResult.getName());
            return "redirect:/auction/auction";
        } else {
            // login 실패
            System.out.println("실패함");
            return "redirect:/member/login";
        }
    }

    @GetMapping("/")
    public String findAll(Model model) {
        List<MemberDTO> memberDTOList = memberService.findAll();
        // 어떠한 html로 가져갈 데이터가 있다면 model사용
        model.addAttribute("memberList", memberDTOList);
        return "list";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model) {
        MemberDTO memberDTO = memberService.findById(id);
        model.addAttribute("member", memberDTO);
        return "detail";
    }

//    @GetMapping("/update")
//    public String updateForm(HttpSession session, Model model) {
//        String myEmail = (String) session.getAttribute("loginEmail");
//        MemberDTO memberDTO = memberService.updateForm(myEmail);
//        model.addAttribute("updateMember", memberDTO);
//        return "update";
//    }

//    @PostMapping("/update")
//    public String update(@ModelAttribute MemberDTO memberDTO) {
//        memberService.update(memberDTO);
//        return "redirect:/member/" + memberDTO.getId();
//    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id) {
        memberService.deleteById(id);
        return "redirect:/member/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "index";
    }

    @PostMapping("/email-check")
    public @ResponseBody String emailCheck(@RequestParam("email") String email) {
        System.out.println("memberEmail = " + email);
        String checkResult = memberService.emailCheck(email);
        return checkResult;
//        if (checkResult != null) {
//            return "ok";
//        } else {
//            return "no";
//        }
    }
}
