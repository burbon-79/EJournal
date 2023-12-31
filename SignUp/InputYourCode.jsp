<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html lang="uk-UA">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Введіть код закладу</title>

    <link rel="stylesheet" href="../css/login&register.css" />
  </head>

  <body>
    <c:choose>
      <c:when test="${requestScope.EnterCodePage == true}">
        <center>
          <div id="wrapper">
            <div id="input-field">
              <form method="POST" action="CreateAccountNextStep">
                <p class="input-background-wide">
                  <span class="input-text">Введіть код Вашого закладу:</span>
                  <input type="text" name="code" class="input-info"/>
                </p>
    
                <input type="SUBMIT" value="Далі" id="register-button"/>
              </form>
              <c:if test="${Error == true}">
                <span class="new-line"></span>
                <p id="error"><c:out value="${InvalidData}"/></p>
              </c:if>
            </div>
          </div>
        </center>
      </c:when>
      <c:otherwise>
        <c:redirect url="../index.jsp" />
      </c:otherwise>
    </c:choose>
  </body>
</html>
