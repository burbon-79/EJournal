package org.ejournal.servlet.menu.addinformation.addgrades;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.ejournal.dao.MarksDAO;
import org.ejournal.dao.entities.MarksEntity;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.Objects;

public class SaveGradesHttpServlet extends HttpServlet {
    private MarksDAO marksDAO;

    public SaveGradesHttpServlet() throws SQLException {
        this.marksDAO = new MarksDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        boolean addToDB = true;
        String errorMSG = null;

        String organization = (String) session.getAttribute("Organization");
        String classroom = (String) session.getAttribute("Classroom");
        String subject = (String) session.getAttribute("Subject");
        int page = (int) session.getAttribute("NumberOfPage");
        String dates[] = new String[17];
        String datesList;
        String marks[][] = new String[(int)session.getAttribute("NumberOfStudents")][17];
        String marksList;

        for (int i = 0; i < 17; i++) {
            String temp = request.getParameter("date" + i);
            try{
                double num = Double.parseDouble(temp);
                if(num<0.0){
                    throw new NumberFormatException();
                }
                dates[i] = String.valueOf(num);
            } catch (NumberFormatException e){
                if(Objects.equals(temp, "�") || Objects.equals(temp, "�") || Objects.equals(temp, "�")) {
                    dates[i] = temp;
                } else if(temp.isEmpty()) {
                    dates[i] = null;
                } else {
                    dates[i] = temp;
                    errorMSG = "������ �������� � ����� � ������";
                    addToDB = false;
                }
            }
        }

        datesList = Arrays.toString(dates).replace("[", "").replace("]", "");

        for (int i = 0; i < (int)session.getAttribute("NumberOfStudents"); i++) {
            for (int j = 0; j < 17; j++) {
                String temp = request.getParameter("grade" + i + "" + j);
                try {
                    int num = Integer.parseInt(temp);
                    if (num < 1 || num > 12) {
                        throw new NumberFormatException();
                    }

                    marks[i][j] = String.valueOf(num);
                } catch (NumberFormatException e){
                    if(Objects.equals(temp, "�") || Objects.equals(temp, "�")){
                        marks[i][j] = temp;
                    } else if(temp.isEmpty()) {
                        marks[i][j] = null;
                    } else {
                        marks[i][j] = temp;
                        errorMSG = "������ �������� � ����� � ��������";
                        addToDB = false;
                    }
                }
            }
        }

        marksList = Arrays.deepToString(marks);
        marksList = marksList.substring(1, marksList.length()-1);

        if(addToDB) {
            try {
                boolean infoAlreadyExist = false;

                MarksEntity marksInfo = marksDAO.getMarks(organization, classroom, subject, page, (int)session.getAttribute("NumberOfStudents"));
                if(marksInfo!=null) {
                    if (marksInfo.getDates() != null) {
                        marksDAO.updateDates(organization, classroom, subject, page, datesList);
                        infoAlreadyExist = true;
                    }

                    if (marksInfo.getMarks() != null) {
                        marksDAO.updateMarks(organization, classroom, subject, page, marksList);
                        infoAlreadyExist = true;
                    }
                }

                if(!infoAlreadyExist) {
                    marksDAO.addInformation(organization, classroom, subject, page, datesList, marksList);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            session.removeAttribute("Classroom");
            session.removeAttribute("Subject");
            session.removeAttribute("NumberOfPage");
            session.removeAttribute("NumberOfStudents");

            session.removeAttribute("StudentsFromThisClass");
            session.removeAttribute("NumberOfPage");

            session.removeAttribute("Dates");
            session.removeAttribute("Marks");

            RequestDispatcher requestDispatcher = request.getRequestDispatcher("AddGrades/AddedGrades.html");
            requestDispatcher.forward(request, response);
        } else {
            session.setAttribute("Dates", dates);
            session.setAttribute("Marks", marks);

            request.setAttribute("Error", true);
            request.setAttribute("InvalidData", errorMSG);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("AddGrades/InputGrades.jsp");
            requestDispatcher.forward(request, response);
        }
    }
}
