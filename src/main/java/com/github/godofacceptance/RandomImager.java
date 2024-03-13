package com.github.godofacceptance;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.customsearch.v1.CustomSearchAPI;
import com.google.api.services.customsearch.v1.model.Result;
import com.google.api.services.customsearch.v1.model.Search;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONObject;


public class RandomImager {
    /* make sure to delete the keys for publishing */
    private static final String API_KEY = "";
    private static final String CSE_ID = "";
    private static final int SAMPLE_SIZE = 10; //number of images to sample from the web.


    /**
     * Searches images using Custom Search JSON API given a query.
     * @Return a list of links for images.
     * */
    public static List<String> search(String query) throws UnsupportedOperationException{
        List<String> links = new ArrayList<>();
        CustomSearchAPI csa = new CustomSearchAPI(new NetHttpTransport(), new GsonFactory(), null);
        long i = 0;
        while(links.size() < SAMPLE_SIZE) {
            try {
                CustomSearchAPI.Cse.List list = csa.cse().list();
                list.setKey(API_KEY);
                list.setCx(CSE_ID);
                list.setQ(query);
                list.setSearchType("image");
                list.setNum(Math.min(SAMPLE_SIZE, 10));
                list.setStart(1 + 10 * i);
                Search results = list.execute();
                List<Result> items = results.getItems();
                links.addAll(items.stream().map(Result::getLink).toList());
                i++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("@RandomImager: search:: Sampled " + links.size() + " images of " + query);
        return links;



//Initial approach: navigating JSON received from the server.
//It turns out that Google provides own library for the api

//        try{
//        URL url = new URL("https://www.googleapis.com/customsearch/v1?key=" + API_KEY
//                + "&cx=" + CSE_ID
//                + "&q=" + query
//                + "&searchType=image"
//                );
//
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("GET");
//
//        //Build a string from the server's input stream
//        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        StringBuilder sb = new StringBuilder();
//
//        String line;
//        while((line = br.readLine())!=null){
//            sb.append(line);
//        }
//
//        //navigates json to locate the link of the image, and then stores in a list
//        JSONObject json = new JSONObject(sb.toString());
//        JSONArray items = json.getJSONArray("items");
//        List<String> srcs = new ArrayList<>();
//        for(int i = 0; i < items.length(); i++){
//            JSONObject item = items.getJSONObject(i);
//            String src = item.getString("link");
//            srcs.add(src);
//        }
//
//        //close everything
//        br.close();
//        connection.disconnect();
//
//        return srcs;
//
//        } catch(MalformedURLException exn) {
//            System.out.println(exn.getMessage());
//        } catch (IOException exn){
//            System.out.println(exn.getMessage() + "\n Check if you reached max number of API calls");
//        }
//        throw new UnsupportedOperationException();
    }
}
