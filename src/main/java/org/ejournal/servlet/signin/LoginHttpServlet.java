package org.ejournal.servlet.signin;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.ejournal.dao.UserDAO;
import org.ejournal.dao.entities.UserEntity;
import org.ejournal.dto.UserParametersRequest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

public class LoginHttpServlet extends HttpServlet {
    private UserDAO userDAO;

    public LoginHttpServlet() {
        this.userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        UserParametersRequest userParametersRequest = new UserParametersRequest(request.getParameter("email"), request.getParameter("pass"));

        HashMap<String, String> savedInfo = new HashMap<>();
        savedInfo.put("Email", userParametersRequest.getEmail());

        try {
            UserEntity user = userDAO.getUser(userParametersRequest.getEmail());
            if(user!=null) {
                if (Objects.equals(user.getPassword(), userParametersRequest.getPassword())) {
                    loginUser(request, response, session, userParametersRequest, user);
                } else {
                    request.setAttribute("InvalidData", "����������� �������� ������");
                    returnError(request, response, savedInfo);
                }
            } else {
                request.setAttribute("InvalidData", "���� ����������� � ����� ������� ���������� �����");
                returnError(request, response, savedInfo);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loginUser(HttpServletRequest request, HttpServletResponse response, HttpSession session, UserParametersRequest userParametersRequest, UserEntity user) throws ServletException, IOException {
        session.setAttribute("LoggedIn", true);

        session.setAttribute("Role", user.getRole());

        session.setAttribute("Principal", "principal");
        session.setAttribute("Tutor", "tutor");
        session.setAttribute("Teacher", "teacher");


        int organization = user.getOrganizationId();
        if (organization == -1) {
            request.setAttribute("EnterCodePage", true);
            session.setAttribute("Email", userParametersRequest.getEmail());

            RequestDispatcher requestDispatcher = request.getRequestDispatcher("InputYourCode.jsp");
            requestDispatcher.forward(request, response);
        } else {
            session.setAttribute("Organization", organization);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("UserInSystem.jsp");
            requestDispatcher.forward(request, response);
        }
    }

    private static void returnError(HttpServletRequest request, HttpServletResponse response, HashMap<String, String> info) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
        request.setAttribute("SavedInfo", info);
        request.setAttribute("Error", true);
        requestDispatcher.forward(request, response);
    }
}