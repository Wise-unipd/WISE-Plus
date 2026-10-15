package it.unipd.dei.webapp.servlet.test;

import it.unipd.dei.webapp.database.test.CheckDBHealthDAO;
import it.unipd.dei.webapp.resource.test.DBHealthCheck;
import it.unipd.dei.webapp.resource.LogContext;
import it.unipd.dei.webapp.resource.Message;
import it.unipd.dei.webapp.servlet.AbstractDatabaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.message.StringFormattedMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
/**
 * @author Wise Team
 * @version 1.0
 * @since 1.0
 */



@WebServlet(name = "DBCheckServlet", value = "/db_check")
public class DBCheckServlet extends AbstractDatabaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        LogContext.setIPAddress(req.getRemoteAddr());
        LogContext.setResource(req.getRequestURI());


        LOGGER.info("Listing Users with Tracks");
        Message m = null;
        try {


            DBHealthCheck check = new CheckDBHealthDAO(getConnection()).access().getOutputParam();
            LOGGER.info("Reveived: " + check + "!");

            String last_check = "never";
            if (check == null) {
                m = new Message("No db check available", "E0A1", "No db check available");
                LOGGER.error(new StringFormattedMessage("problems with fields:  %s.", m.getMessage()));
            } else {
                last_check = check.getDatetime().toString();
            }


            res.setContentType("text/html; charset=utf-8");
            // get a stream to write the response
            PrintWriter out = res.getWriter();

            // write the HTML page
            out.printf("<!DOCTYPE html>%n");

            out.printf("<html lang=\"en\">%n");
            out.printf("<head>%n");
            out.printf("<meta charset=\"utf-8\">%n");
            out.printf("<title>Users</title>%n");
            out.printf("</head>%n");

            out.printf("<body>%n");

            out.printf(String.format("<pre> LAST CHECK previously made: %s</pre>%n", last_check));

            out.printf("</body>%n");

            out.printf("</html>%n");

            // flush the output stream buffer
            out.flush();

            // close the output stream
            out.close();


        } catch (SQLException | IOException e) {

            m = new Message("An error occurred SQL, SERVLET", "E0A1", e.getMessage());
            LOGGER.error("stacktrace:", e);

        } finally {
            LogContext.removeIPAddress();
            LogContext.removeAction();
            LogContext.removeResource();
        }
    }
}
