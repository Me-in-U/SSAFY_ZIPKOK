package com.ssafy.live.model.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ssafy.live.exception.RecordNotFoundException;
import com.ssafy.live.model.dao.BasicMemberDao;
import com.ssafy.live.model.dao.MemberDao;
import com.ssafy.live.model.dto.Member;
import com.ssafy.live.util.DBUtil;

public class BasicMemberService implements MemberService {
	private MemberDao mDao = BasicMemberDao.getDao();

	private DBUtil util = DBUtil.getUtil();
	private static BasicMemberService service = new BasicMemberService();

	private BasicMemberService() {
	}

	public static BasicMemberService getService() {
		return service;
	}

	@Override
	public int registMember(Member member) throws SQLException {
		Connection con = util.getConnection();
		try {
			con.setAutoCommit(false);
			int result = mDao.insert(con, member);
			con.commit();
			return result;
		} catch (SQLException e) {
			con.rollback();
			throw e;
		} finally {
			util.close(con);
		}
	}

	@Override
	public Member login(String email, String password) throws SQLException {
		try (Connection con = util.getConnection()) {
			Member member = mDao.select(con, email);
			if (member != null && member.getPassword().equals(password)) {
				return member;
			} else {
				throw new RecordNotFoundException("id/pass 확인");
			}
		}
	}

	@Override
	public Member selectDetail(String email) throws SQLException {
		try (Connection con = util.getConnection()) {
			return mDao.selectDetail(con, email);
		}
	}

	public List<Member> search() throws SQLException {
		try (Connection con = util.getConnection()) {
			return mDao.search(con);
		}
	}


	@Override
	public void delete(int mno) throws SQLException {
		// TODO: 06-01. 먼저 Address 정보를 삭제한 후 Member 정보를 삭제하세요.
		// aDao의 deleteByMno와 mDao의 delete를 사용한다.
		Connection connection = util.getConnection();
		try {
			connection.setAutoCommit(false);
			mDao.delete(connection, mno);
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
			throw e;
		} finally {
			util.close(connection);
		}
		// END
	}

	@Override
	public void update(Member member) throws SQLException {
		Connection con = util.getConnection();
		try {
			con.setAutoCommit(false);
			mDao.update(con, member);
			con.commit();
		} catch (SQLException e) {
			con.rollback();
			throw e;
		} finally {
			util.close(con);
		}
	}
}
