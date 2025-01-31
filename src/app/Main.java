package app;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

import data_access.*;
import entity.CommonUserFactory;
import use_case.AddToWatchlist.AddToWatchlistDataAccessInterface;
import use_case.DeleteWatchlist.DeleteWatchlistDataAccessInterface;
import use_case.GetDetailMovie.GetDetailMovieDataAccessInterface;
import use_case.GetWatchList.GetWatchListDataAccessInterface;
import use_case.ShareWatchlist.ShareWatchlistDataAccessInterface;
import usecase_adaptor.AddToWatchlist.AddToWatchlistController;
import usecase_adaptor.AddToWatchlist.AddToWatchlistViewModel;
import usecase_adaptor.DeleteWatchlist.DeleteWatchlistController;
import usecase_adaptor.DeleteWatchlist.DeleteWatchlistViewModel;
import usecase_adaptor.GetDetailOfMovie.GetDetailMovieController;
import usecase_adaptor.GetDetailOfMovie.GetDetailMovieState;
import usecase_adaptor.GetDetailOfMovie.GetDetailMovieViewModel;
import usecase_adaptor.GetWatchlist.GetWatchListViewmodel;
import usecase_adaptor.MainMenu.MainMenuViewModel;
import usecase_adaptor.MovieSearchByKeyword.MovieResultViewModel;
import usecase_adaptor.MovieSearchByKeyword.SearchByNameViewModel;
import data_access.WithoutFilterDAO;
import usecase_adaptor.RecommendMovieWithoutFilter.WithoutFilterResultViewModel;
import usecase_adaptor.RecommendMovieWithoutFilter.WithoutFilterViewModel;
import usecase_adaptor.SearchList.SearchListViewModel;
import usecase_adaptor.ShareWatchlist.ShareWatchlistViewModel;
import usecase_adaptor.ViewManagerModel;
import usecase_adaptor.login.LoginViewModel;
import usecase_adaptor.signup.SignupViewModel;
import view.*;
//import view.MovieResultView;
//import usecase_adaptor.MovieSearchByKeyword.MovieResultViewModel;


public class Main {

    public static void main(String[] args) throws FileNotFoundException, WithoutFilterDAO.NoDataException{

        JFrame application = new JFrame("Movie Recommendations App");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        CardLayout cardLayout = new CardLayout();

        JPanel views = new JPanel(cardLayout);
        application.add(views);

        ViewManagerModel viewManagerModel = new ViewManagerModel();
        new ViewManager(views, cardLayout, viewManagerModel);





        // Create view models and save all information of view in them.
        LoginViewModel loginViewModel = new LoginViewModel();
        SignupViewModel signupViewModel = new SignupViewModel();
        MainMenuViewModel mainMenuViewModel = new MainMenuViewModel();
        ShareWatchlistViewModel shareWatchlistViewModel = new ShareWatchlistViewModel();
        SearchByNameViewModel searchByNameViewModel = new SearchByNameViewModel();
        MovieResultViewModel resultViewModel = new MovieResultViewModel();
        SearchListViewModel searchListViewModel = new SearchListViewModel();
        GetWatchListViewmodel getWatchListViewmodel = new GetWatchListViewmodel();
        GetDetailMovieViewModel getDetailMovieViewModel = new GetDetailMovieViewModel();
        WithoutFilterResultViewModel withoutFilterResultViewModel = new WithoutFilterResultViewModel();
        WithoutFilterViewModel withoutFilterViewModel = new WithoutFilterViewModel();






        // Create data access interface(object).
        MovieDataAccessObject movieDataAccessObject;

        WithoutFilterDAO withoutFilterDAO;


        GetDetailMovieDataAccessInterface getDetailMovieDataAccessInterface = new MovieDetailAccessAPI();


        ShareWatchlistDataAccessInterface shareWatchlistDataAccessObject;
        shareWatchlistDataAccessObject = new ShareWatchlistDataAccessObject(
                "./userInformation.csv",
                new CommonUserFactory());
        AddToWatchlistDataAccessInterface addToWatchlistDataAccessObject;
        addToWatchlistDataAccessObject = new AddToWatchlistDataAccessObject(
                "./userInformation.csv",
                new CommonUserFactory());

        DeleteWatchlistDataAccessInterface deleteWatchlistDataAccessObject;
        deleteWatchlistDataAccessObject = new WatchlistDAO(
                "./userInformation.csv", new CommonUserFactory());

        movieDataAccessObject = new MovieDataAccessObject(searchByNameViewModel.getKeywordInput(), new CommonUserFactory());

        withoutFilterDAO = new WithoutFilterDAO("./userInformation.csv");

        FileUserDataAccessObject userDataAccessObject;
        try {
            userDataAccessObject = new FileUserDataAccessObject("./userInformation.csv", new CommonUserFactory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GetWatchListDataAccessInterface getWatchListDataAccessInterface = new GetWatchListDAO("./userInformation.csv");






        // Create view and add them in views.
        SignupView signupView = SignupUseCaseFactory.create(viewManagerModel, loginViewModel, signupViewModel, userDataAccessObject);
        views.add(signupView, signupView.viewName);

        LoginView loginView = LoginUseCaseFactory.create(viewManagerModel, loginViewModel, mainMenuViewModel, userDataAccessObject, signupViewModel);
        views.add(loginView, loginView.viewName);

        // MainMenuView mainMenuView = new MainMenuView(viewManagerModel, mainMenuViewModel, shareWatchlistViewModel, getWatchListViewmodel);
        // views.add(mainMenuView, mainMenuView.viewName);
        MainMenuView mainMenuView = MainmenuUseCaseFactory.create(viewManagerModel, mainMenuViewModel,
                shareWatchlistViewModel, getWatchListViewmodel, searchByNameViewModel, withoutFilterViewModel, getWatchListDataAccessInterface);
        views.add(mainMenuView, mainMenuView.viewName);

        ShareWatchlistView shareWatchlistView = ShareWatchlistUseCaseFactory.create(
                viewManagerModel, shareWatchlistViewModel, shareWatchlistDataAccessObject, mainMenuViewModel);
        views.add(shareWatchlistView, shareWatchlistView.viewName);

        MovieRecommendView movieRecommendView = MovieSearchUseCaseFactory.create(
                viewManagerModel, searchByNameViewModel, resultViewModel, searchListViewModel, movieDataAccessObject);
        views.add(movieRecommendView, movieRecommendView.viewName);

//        MovieResultView movieResultView = new MovieResultView(resultViewModel, searchByNameViewModel, viewManagerModel);
//        views.add(movieResultView, movieResultView.viewName);

        MovieResultView movieResultView = MovieResultUseCaseFactory.create(viewManagerModel, searchByNameViewModel, resultViewModel, getDetailMovieViewModel, getDetailMovieDataAccessInterface);
        views.add(movieResultView, movieResultView.viewName);

        WithoutFilterView withoutFilterView = WithoutFilterUseCaseFactory.create(viewManagerModel, withoutFilterViewModel, withoutFilterResultViewModel, movieDataAccessObject, withoutFilterDAO);
        views.add(withoutFilterView, withoutFilterView.viewName);

        WithoutFilterResultView withoutFilterResultView = WithoutFilterResultUseCaseFactory.create(viewManagerModel, withoutFilterViewModel, withoutFilterResultViewModel, getDetailMovieViewModel, getDetailMovieDataAccessInterface);
        views.add(withoutFilterResultView, withoutFilterResultView.viewName);

        SearchListView searchListView = new SearchListView(searchListViewModel, viewManagerModel);
        views.add(searchListView, searchListView.viewName);

        GetWatchlistView getWatchlistView = GetWatchlistUseCaseFactory.create(viewManagerModel, getWatchListDataAccessInterface,
                getDetailMovieViewModel, getWatchListViewmodel, getDetailMovieDataAccessInterface, mainMenuViewModel);
        views.add(getWatchlistView, getWatchlistView.viewName);

//        GetDetailMovieView getDetailMovieView = new GetDetailMovieView(getDetailMovieViewModel,
//                addToWatchlistcontroller, addToWatchlistViewmodel, deleteWatchlistController,
//                deleteWatchlistViewModel,viewManagerModel, mainMenuViewModel);
        GetDetailMovieView getDetailMovieView = GetDetailOfMovieUseCaseFactory.create(getDetailMovieViewModel,
                addToWatchlistDataAccessObject, new AddToWatchlistViewModel(), deleteWatchlistDataAccessObject,
                new DeleteWatchlistViewModel(), viewManagerModel, mainMenuViewModel);
        views.add(getDetailMovieView, getDetailMovieView.viewname);




        viewManagerModel.setActiveView(signupView.viewName);
        viewManagerModel.firePropertyChanged();

        application.setSize(600, 400);
        application.pack();
        application.setVisible(true);


    }
}