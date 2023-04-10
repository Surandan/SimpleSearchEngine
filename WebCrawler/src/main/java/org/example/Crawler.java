package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.IIOException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;

public class Crawler {
    private HashSet<String> urlSet;
    private int MAX_DEPTH = 2;
    private static Connection connection = null;
    public Crawler(){
        urlSet = new HashSet<String>();
        connection = DataBaseConnection.getConnection();
    }
    public void getPageTextAndLinks(String url,int depth){
        if(urlSet.contains(url)) return;
        if(depth>=MAX_DEPTH) return;
        if(urlSet.add(url)){
            System.out.println(url);
        }
        depth++;
        try {
            // Connect to Web pages and get Document object
            Document document = Jsoup.connect(url).timeout(5000).get(); // if page doesn't load in 5 sec it will skip to next
            String title = document.title();
            System.out.println(title);
            String text = document.text().length()<500? document.text() : document.text().substring(0,499);
            String link = url;
            // Save above data to DataBase
            PreparedStatement preparedStatement = connection.prepareStatement("insert into pages values(?,?,?);");
            preparedStatement.setString(1,title);
            preparedStatement.setString(2,link);
            preparedStatement.setString(3,text);
            preparedStatement.executeUpdate();

            // call recursively for every link in curr page
            Elements availableLinks = document.select("a[href]");
            for (Element currLink : availableLinks) {
                getPageTextAndLinks(currLink.attr("abs:href"), depth);
            }
        }
        catch (IOException | SQLException ioException){
            ioException.printStackTrace();
        }

    }



    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        crawler.getPageTextAndLinks("https://www.javatpoint.com",0);
    }
}