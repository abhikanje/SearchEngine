package com.Accio;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/Search")
public class Search extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        //get parameter called keyword from request
        String keyword = request.getParameter("keyword");
        System.out.println(keyword);
        try {
            // Establishment connection to database
            Connection connection = DatabaseConnection.getConnection();
            //Save keyword and link associated into history table
            PreparedStatement preparedStatement = connection.prepareStatement("insert into history values(?, ?)");
            preparedStatement.setString(1, keyword);
            preparedStatement.setString(2, "http://localhost:8080/SearchEngine/Search?keyword="+keyword);
            preparedStatement.executeUpdate();
            // Excecuting a query related to keyword and get result
            ResultSet resultSet = connection.createStatement().executeQuery("select PageTitle,Pagelink,(length(lower(PageText))-length(replace(lower(PageText), '" + keyword + "', '')))/length('" + keyword + "') as countoccurence from pages order by countoccurence desc limit 30;\n");
            ArrayList<SearchResult> results = new ArrayList<SearchResult>();
            //iterate through the resultsetand save all elements in a result arraylist
            while (resultSet.next()) {
                SearchResult searchResult = new SearchResult();
                // get pagetitle
                searchResult.setPageTitle(resultSet.getString("PageTitle"));
                //get pagelink
                searchResult.setPageLink(resultSet.getString("PageLink"));
                results.add(searchResult);
            }
            //display results in console
            for (SearchResult searchResult : results) {
                System.out.println(searchResult.getPageTitle() + " " + searchResult.getPageLink() + "\n");
            }
            //set attributes of request with results arraylist
            request.setAttribute("results", results);
            //forward request to the frontend
            request.getRequestDispatcher("/search.jsp").forward(request, response);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

        }
        catch (SQLException | IOException | ServletException sqlException){
            sqlException.printStackTrace();
        }
    }
}
