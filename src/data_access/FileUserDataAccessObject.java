package data_access;

import entity.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import use_case.clear_users.ClearUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class FileUserDataAccessObject implements SignupUserDataAccessInterface, LoginUserDataAccessInterface, ClearUserDataAccessInterface {

    private final File csvFile;

    private final Map<String, Integer> headers = new LinkedHashMap<>();

    private final Map<String, User> accounts = new HashMap<>();

    private UserFactory userFactory;

    public FileUserDataAccessObject(String csvPath, UserFactory userFactory) throws IOException {
        this.userFactory = userFactory;

        csvFile = new File(csvPath);
        headers.put("id", 0);
        headers.put("username", 1);
        headers.put("password", 2);
        headers.put("creation_time", 3);
        headers.put("search_history", 4);
        headers.put("watchlist", 5);
        headers.put("shared_watchlist", 6);

        if (csvFile.length() == 0) {
            save();
        } else {

            try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
                String header = reader.readLine();

                // For later: clean this up by creating a new Exception subclass and handling it in the UI.
//                assert header.equals("id,username,password,creation_time,search_history,watchlist,shared_watchlist");

                String row;
                while ((row = reader.readLine()) != null) {
                    String[] col = row.split(",");
                    String id = String.valueOf(col[headers.get("id")]);
                    String username = String.valueOf(col[headers.get("username")]);
                    String password = String.valueOf(col[headers.get("password")]);
                    String creationTimeText = String.valueOf(col[headers.get("creation_time")]);
                    String search_history = String.valueOf(col[headers.get("search_history")]);
                    String watchlist = String.valueOf(col[headers.get("watchlist")]);
                    String shared_watchlist = String.valueOf(col[headers.get("shared_watchlist")]);

                    LocalDateTime ldt = LocalDateTime.parse(creationTimeText);

                    //create search history from string
                    List<Movie> searchHistoryMovies = trans_to_movie(search_history, "#");
                    SearchHistory searchHistory = new SearchHistory(searchHistoryMovies);

                    //create watchlist from string
                    List<Movie> watchlistMovies = trans_to_movie(watchlist, "#");
                    Watchlist watchList = new Watchlist(watchlistMovies);

                    //crate shared watchlist from string
                    Map<String, Watchlist> sharedWatchlist= trans_to_shared_watchlist(
                            shared_watchlist, "#", ":", "%");

                    //crate a new user object
                    User user = userFactory.create(username, password, ldt, searchHistory, watchList);
                    user.setId(Integer.parseInt(id));
                    user.setCompleteSharedWatchlist(sharedWatchlist);
                    accounts.put(username, user);
                }
            }
        }
    }

    /**
     *
     * @param col string of form "u1:1%2%3#u2:4%5%6..."
     * @param UserSplitter "#"
     * @param EachUserSplitter ":"
     * @param MovieSplitter "%"
     * @return the complete shared watchlist.
     */
    private Map<String, Watchlist> trans_to_shared_watchlist(String col, String UserSplitter,
                                                             String EachUserSplitter, String MovieSplitter) {
        String[] info = col.split(UserSplitter);
        Map<String, Watchlist> user_watchlist_map = new HashMap<>();
        for (String each_info: info) {
            Map<String, List<Movie>> map = trans_to_each_user(each_info, EachUserSplitter, MovieSplitter);
            String userName = map.keySet().toString();
            Watchlist watchlist = new Watchlist(map.get(userName));
            user_watchlist_map.put(userName, watchlist);
        }
        return user_watchlist_map;
    }

    /**
     *
     * @param col string of form "user:1%2%3"
     * @param EachUserSplitter ":"
     * @param MovieSplitter "#"
     * @return a map only have one user key and one list of movie.
     */
    private Map<String, List<Movie>> trans_to_each_user(String col, String EachUserSplitter, String MovieSplitter) {
        String[] user_list = col.split(EachUserSplitter);
        Map<String, List<Movie>> map = new HashMap<>();
        // check precondition
        if (user_list.length == 2) {
            map.put(user_list[0], trans_to_movie(user_list[1], MovieSplitter));}
        return map;
    }


    /**
     *
     * @param col a string of form "1#2#3" where 123 are movie id.
     * @param splitter splitter
     * @return a list of movie with correct id.
     */
    private List<Movie> trans_to_movie(String col, String splitter) {
        String[] movie_list = col.split(splitter);
        List<Movie> movies = new ArrayList<>();
        for (String num : movie_list) {
            try {
                int movie_id = Integer.parseInt(num);
                Movie history_movie = get_movie_from_api(movie_id);
                movies.add(history_movie);
            } catch (NumberFormatException e) {
                System.out.println("file error");
            }
        }
        return movies;}


    private Movie get_movie_from_api(int movieID) {

        //call api get request
        OkHttpClient client = new OkHttpClient();;

        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/movie/"+String.valueOf(movieID)+"?language=en-US")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxYTM1NDRjZTMxNTEyYjhlZGMzOWFlYWQyMTdiZWFlZCIsInN1YiI6IjY1MTZlZjJmYzUwYWQyMDBjOTFhNjYwZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.d5F2KzF4gOHTdMcv3AZzazTgKTGv--FzILbQvLVG9EI")
                .build();

        try {
            Response response = client.newCall(request).execute();
            System.out.println(response.code());
            if (response.code() == 200) {
                System.out.println("success");
                JSONObject responseBody = new JSONObject(response.body().string());
                String name = String.valueOf(responseBody.getString("original_title"));
                return new Movie(name, movieID);
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void save(User user) {
        if (user.getId() == 0) {
            user.setId(getId());
        }
        accounts.put(user.getName(), user);
        this.save();
    }

    @Override
    public User get(String username) {
        return accounts.get(username);
    }

    private void save() {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(csvFile));
            writer.write(String.join(",", headers.keySet()));
            writer.newLine();

            for (User user : accounts.values()) {
                String line = String.format("%s,%s,%s,%s,%s,%s,%s",
                        user.getId(), user.getName(), user.getPassword(), user.getCreationTime(),
                        list_to_movie_string(user.getSearchHistory()),
                        list_to_movie_string(user.getWatchlist()),
                        user.getSharedWatchlist());
                writer.write(line);
                writer.newLine();
            }

            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Return whether a user exists with username identifier.
     * @param identifier the username to check.
     * @return whether a user exists with username identifier
     */
    @Override
    public boolean existsByName(String identifier) {
        return accounts.containsKey(identifier);
    }

    public ArrayList<String> getAllUsers() {
        ArrayList<String> names = new ArrayList<>(accounts.keySet());
        return names;
    }

    @Override
    public void clearAllUsers() {

    }

    private int getId() {
        return accounts.size();
    }

    private String list_to_movie_string(List<Movie> movies) {
        if (movies.isEmpty()) {
            return "";
        }
        String result = "";
        for (Movie movie:movies) {
            int id = movie.getID();
            result = result + id + "#";
        }
        return result.substring(0, result.length() - 1);

    }
}
