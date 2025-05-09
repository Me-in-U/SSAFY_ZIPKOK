package com.ssafy.live.controller;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ControllerHelper {
    public default String preProcessing(HttpServletRequest req, HttpServletResponse resp) {

        String action = req.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "index";
        }
        return action;
    }

    public default void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }

    public default void forward(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException {
        RequestDispatcher disp = request.getRequestDispatcher(path);
        disp.forward(request, response);
    }

    public default void toJSON(Object target, HttpServletResponse response) throws ServletException, IOException {
        String json = new ObjectMapper().writeValueAsString(target);
        response.setContentType("application/json?charset=utf-8");
        response.getWriter().write(json);
    }

    public default Cookie setupCookie(String name, String value, int maxAge, String path, HttpServletResponse resp) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        if (path != null) {
            cookie.setPath(path);
        }
        resp.addCookie(cookie);
        return cookie;
    }
}
