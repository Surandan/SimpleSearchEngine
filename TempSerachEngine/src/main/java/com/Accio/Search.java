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
        String keyword = request.getParameter("keyword");
        System.out.println(keyword);
        Connection connection = DatabaseConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("Insert into history values(?,?)");
            preparedStatement.setString(1,keyword);
            preparedStatement.setString(2,"http://localhost:8080/TempSerachEngine/Search?keyword="+keyword);
            preparedStatement.executeUpdate();


            ResultSet resultSet = connection.createStatement().executeQuery("select pageTitle,pageLink,(length(pageText)-length(replace(lower(pageText),'"+keyword+"','')))/length('"+keyword+"') as occurence from pages order by occurence desc  limit 30;");
            ArrayList<SearchResult> results = new ArrayList<SearchResult>();

            while (resultSet.next()){
                SearchResult searchResult = new SearchResult();
                searchResult.setTitle(resultSet.getString("pageTitle"));
                searchResult.setLink((resultSet.getString("pageLink")));
                results.add(searchResult);
            }
            for(SearchResult searchResult:results){
                System.out.println(searchResult.getTitle()+" "+searchResult.getLink()+"/n");
            }

            request.setAttribute("results",results);
            request.getRequestDispatcher("/search.jsp").forward(request,response);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
        }
        catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        catch (ServletException servletException){
            servletException.printStackTrace();
        }
        catch (IOException ioException){
            ioException.printStackTrace();
        }
    }
}
