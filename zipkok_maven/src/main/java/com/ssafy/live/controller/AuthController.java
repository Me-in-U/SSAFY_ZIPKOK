package com.ssafy.live.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import com.ssafy.live.model.dto.Member;
import com.ssafy.live.model.dto.SearchCondition;
import com.ssafy.live.model.service.BasicMemberService;
import com.ssafy.live.model.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = "/auth")
@SuppressWarnings("serial")
public class AuthController extends HttpServlet implements ControllerHelper {

	private final MemberService mService = BasicMemberService.getService();

	// TODO: 02-01. 개별적으로 발급받은 키를 등록하세요.
	private final String keyVworld = "572DE249-AC29-3AA1-8697-04BABC9B9043";
	private final String keySgisServiceId = "e0e8921fe6dc4e96bb82"; // 서비스 id
	private final String keySgisSecurity = "53f11235443b4bc0a70c"; // 보안 key
	private final String keyData = "2MyVMUNZ8YSPwbMXOcVkb1xdy5qgnwokKA4CcpQaZ98nJHbOZBmTgzZ0VhLyzlOsRdhiiBVvoqgX1Kj/tjZu5A=="; // data.go.kr
																																// 인증키

	// END

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = preProcessing(request, response);
		System.out.println(action);
		switch (action) {
//		case "member-detail" -> memberDetail(request, response);
//		case "member-list" -> memberList(request, response);
		case "member-delete" -> memberDelete(request, response);
		case "member-modify-form" -> memberModifyForm(request, response);
		case "member-modify" -> memberModify(request, response);
//		case "address-delete" -> addressDelete(request, response);
//		case "address-insert" -> addressInsert(request, response);
		// default -> response.sendError(HttpServletResponse.SC_NOT_FOUND); // WAS에 404
		// 전달
//		default -> forward(request, response, "/error/404.jsp"); // 지정된 페이지로 forward
		}
	}

	private void memberDetail(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = request.getParameter("email");
		try {

			Member member = mService.selectDetail(email);
			request.setAttribute("member", member);
			request.setAttribute("key_vworld", keyVworld);
			request.setAttribute("key_sgis_service_id", keySgisServiceId);
			request.setAttribute("key_sgis_security", keySgisSecurity);
			request.setAttribute("key_data", keyData);
		} catch (SQLException e) {
			e.printStackTrace();
			request.setAttribute("alertMsg", e.getMessage());
		}
		forward(request, response, "/member/member-detail.jsp");
	}

	// TODO: 05-03. 회원 정보를 조회하여 정보 수정 화면으로 이동한다.
	private void memberModifyForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = request.getParameter("email");
		try {
			Member member = mService.selectDetail(email);
			request.setAttribute("member", member);
			forward(request, response, "/member-modify-form.jsp");
		} catch (SQLException e) {
			e.printStackTrace();
			// 다시 member detail 상황으로 이동
			HttpSession session = request.getSession();
			session.setAttribute("alertMsg", e.getMessage());
			redirect(request, response, "/auth?action=member-detail&email=" + email);
		}
	}

	// TODO: 05-04. 회원 정보를 수정 처리한다.
	private void memberModify(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		int mno = Integer.parseInt(request.getParameter("mno"));

		try {
			Member member = new Member(mno, name, email, password);
			System.out.println(member);
			mService.update(member);
			// 세션 사용자의 경우 이름 변경해주기
			HttpSession session = request.getSession();
			if (((Member) session.getAttribute("loginUser")).getEmail().equals(member.getEmail())) {
				session.setAttribute("loginUser", member);
			}
			// post redirect get
			redirect(request, response, "/member?action=mypage");
		} catch (SQLException e) {
			e.printStackTrace();
			// 다시 member detail 상황으로 이동
			HttpSession session = request.getSession();
			session.setAttribute("alertMsg", e.getMessage());
			redirect(request, response, "/member?action=mypage");
		}
	}

	private void memberDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO: 06-02. memberDelete를 구현해보자.
		// 당사자가 삭제된 경우 - logout 처리
		// 다른 회원을 삭제한 경우 - 목록 보기로 redirect
		// 오류 발생 - 다시 detail 페이지로 이동
		int mno = Integer.parseInt(request.getParameter("mno"));
		String email = request.getParameter("email");
		try {
			mService.delete(mno);
			HttpSession session = request.getSession();
			Member m = (Member)session.getAttribute("loginUser");
			System.out.println(m);
			System.out.println(m instanceof Member);
			System.out.println(m.getEmail() + " " + email);
			if (session.getAttribute("loginUser") instanceof Member && m.getEmail().equals(email)) {
				session.setAttribute("alertMsg", "삭제완료");
				session.invalidate();
				String msg = "탈퇴했습니다. 수고용";
				String encodedMsg = java.net.URLEncoder.encode(msg, "UTF-8");
				redirect(request, response, "/?alertMsg=" + encodedMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// END
	}
}
