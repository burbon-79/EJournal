package org.ejournal.servlet.menu;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class SignOutHttpServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.invalidate();

        request.setAttribute("ShowSignOutPage", true);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("SignOut.jsp");
        requestDispatcher.forward(request, response);
    }
}
