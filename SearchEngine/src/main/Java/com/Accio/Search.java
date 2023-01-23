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



    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        ArrayList<SearchResult> results = new ArrayList<SearchResult>();
        try{
            Connection connection = DatabaseConnection.getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery("select pagetitle, pagelink, (length(lower(pageDate))-length(replace(lower(pageDate), '"+keyword+"', '')))/length('"+keyword+"') as countoccurence from pages order by countoccurence desc limit 30;");
            while (resultSet.next()){
                SearchResult searchResult = new SearchResult();
                searchResult.setName(resultSet.getString("pagetitle"));
                searchResult.setLink(resultSet.getString("pagelink"));
                results.add(searchResult);
            }
            PreparedStatement preparedStatement = connection.prepareStatement("Insert into history values (?, ?)");
            preparedStatement.setString(1, keyword);
            preparedStatement.setString(2, "http://localhost:8080/SearchEngine//Search?keyword="+keyword);
            preparedStatement.executeUpdate();
        }catch (SQLException sqlException){
            System.out.println(sqlException);
        }


        request.setAttribute("results", results);
        request.getRequestDispatcher("/search.jsp").forward(request, response);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

    }
}
