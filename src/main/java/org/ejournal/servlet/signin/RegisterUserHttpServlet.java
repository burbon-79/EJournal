package org.ejournal.servlet.signin;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.ejournal.dao.OrganizationDAO;
import org.ejournal.dao.UserDAO;
import org.ejournal.dto.UserParametersRequest;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Objects;

public class RegisterUserHttpServlet extends HttpServlet {
    private OrganizationDAO organizationDAO;
    private UserDAO userDAO;

    public RegisterUserHttpServlet() {
        this.organizationDAO = new OrganizationDAO();
        this.userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        UserParametersRequest userParametersRequest = new UserParametersRequest(request.getParameter("name"), request.getParameter("role"), request.getParameter("email"), request.getParameter("pass"));

        HashMap<String, String> savedInfo = new HashMap<>();
        savedInfo.put("Email", userParametersRequest.getEmail());

        try {
            savedInfo.put("Name", userParametersRequest.getName());
            if (verifyRequest(request, response, userParametersRequest, savedInfo)) {
                if (Objects.equals(userParametersRequest.getRole(), "principal")) {
                    String code = createNewOrganization(organizationDAO);

                    int organizationID = organizationDAO.getOrganizationIdByName(code);
                    userDAO.createUser(organizationID, userParametersRequest.getRole(), userParametersRequest.getName(), userParametersRequest.getEmail(), userParametersRequest.getPassword());

                    request.setAttribute("Code", code);

                    request.setAttribute("EnterCodePage", true);
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("GetCode.jsp");
                    requestDispatcher.forward(request, response);
                } else {
                    session.setAttribute("Role", userParametersRequest.getRole());
                    session.setAttribute("Name", userParametersRequest.getName());
                    session.setAttribute("Email", userParametersRequest.getEmail());
                    session.setAttribute("Password", userParametersRequest.getPassword());

                    request.setAttribute("EnterCodePage", true);
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("InputYourCode.jsp");
                    requestDispatcher.forward(request, response);
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            request.setAttribute("InvalidData", "���������� �� ����� ������� ���������� ����� ��� ��� �������������");
            returnError(request, response, savedInfo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean verifyRequest(HttpServletRequest request, HttpServletResponse response, UserParametersRequest userParametersRequest, HashMap<String, String> savedInfo) throws ServletException, IOException {
        if (Objects.equals(userParametersRequest.getRole(), "none")) {
            request.setAttribute("InvalidData", "������ ���� ������");
            returnError(request, response, savedInfo);
        } else if (userParametersRequest.getName().isEmpty()) {
            request.setAttribute("InvalidData", "���� � ϲ� �������");
            returnError(request, response, savedInfo);
        } else if (userParametersRequest.getEmail().isEmpty()) {
            request.setAttribute("InvalidData", "���� � ������� ���������� ����� �������");
            returnError(request, response, savedInfo);
        } else if (userParametersRequest.getPassword().isEmpty()) {
            request.setAttribute("InvalidData", "���� � ������� �������");
            returnError(request, response, savedInfo);
        }
        return true;
    }

    private static String createNewOrganization(OrganizationDAO organizationDAO) throws ServletException, IOException, SQLException {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt((int) (Math.random() * 62)));
        }

        organizationDAO.createOrganization(code.toString());

        return code.toString();
    }

    private static void returnError(HttpServletRequest request, HttpServletResponse response, HashMap<String, String> info) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
        request.setAttribute("SavedInfo", info);
        request.setAttribute("Error", true);
        requestDispatcher.forward(request, response);
    }
}