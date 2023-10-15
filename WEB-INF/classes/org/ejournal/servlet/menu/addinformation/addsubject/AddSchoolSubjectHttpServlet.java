package org.ejournal.servlet.menu.addinformation.addsubject;

import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.ejournal.dao.SubjectDAO;
import org.ejournal.dto.NewSchoolSubjectParametersRequest;

import java.io.IOException;
import java.util.Objects;

public class AddSchoolSubjectHttpServlet extends HttpServlet {
    private SubjectDAO subjectDAO;

    public AddSchoolSubjectHttpServlet() {
        this.subjectDAO = new SubjectDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean addNew = true;

        HttpSession session = request.getSession();
        int organization = (int) session.getAttribute("Organization");

        NewSchoolSubjectParametersRequest subjectParametersRequest = new NewSchoolSubjectParametersRequest(request.getParameter("subject"), request.getParameter("button"));

        if (Objects.equals(subjectParametersRequest.getButtonText(), "����� ��� ���������")) {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("Finish.jsp");
            requestDispatcher.forward(request, response);
        } else {
            if (subjectParametersRequest.getSubjectName().isEmpty()) {
                RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
                request.setAttribute("SomeInfo", true);
                request.setAttribute("Info", "���� � ������ �������� ������");
                requestDispatcher.forward(request, response);
            } else {
                try {
                    String alreadyExistingSubjects[] = subjectDAO.getSubjectNames(organization);
                    for (int i = 0; i < alreadyExistingSubjects.length; i++) {
                        if (Objects.equals(alreadyExistingSubjects[i], subjectParametersRequest.getSubjectName())) {
                            addNew = false;
                            RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
                            request.setAttribute("SomeInfo", true);
                            request.setAttribute("Info", "����� ������� ��� ����");
                            requestDispatcher.forward(request, response);
                            break;
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if (addNew) {
                    try {
                        subjectDAO.addSubject(organization, subjectParametersRequest.getSubjectName());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                RequestDispatcher requestDispatcher = null;
                if (Objects.equals(subjectParametersRequest.getButtonText(), "������")) {
                    requestDispatcher = request.getRequestDispatcher("index.jsp");

                    request.setAttribute("SomeInfo", true);
                    request.setAttribute("Info", "������� ������ ������");
                } else if (Objects.equals(subjectParametersRequest.getButtonText(), "������ � ���������")) {
                    requestDispatcher = request.getRequestDispatcher("Finish.jsp");
                }
                requestDispatcher.forward(request, response);
            }
        }
    }
}
