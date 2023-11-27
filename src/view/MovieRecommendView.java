package view;
import usecase_adaptor.MovieSearchByKeyword.MovieResultViewModel;
import usecase_adaptor.MovieSearchByKeyword.SearchByNameController;
import usecase_adaptor.MovieSearchByKeyword.SearchByNameViewModel;
import usecase_adaptor.ShareWatchlist.ShareWatchlistController;
import usecase_adaptor.ShareWatchlist.ShareWatchlistViewModel;
import usecase_adaptor.ShareWatchlist.ShareWatchlistState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MovieRecommendView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "movie_recommendation";
    private final SearchByNameViewModel viewModel;

    final JTextField keywordInputField = new JTextField(15);
    private final JLabel errorLabel = new JLabel();


    final JButton searchButton;

    private final SearchByNameController controller;

    private final ShareWatchlistViewModel shareWatchlistViewModel;
    private final JTextField sharedUsernameInputField = new JTextField(15);

    final JButton searchUsernameButton;

    final JLabel searchUsernameLabel = new JLabel(ShareWatchlistViewModel.USER_NAME_LABEL);

    private final ShareWatchlistController shareWatchlistController;

    public MovieRecommendView(SearchByNameViewModel viewModel, SearchByNameController controller,
                              ShareWatchlistController shareWatchlistController,
                              ShareWatchlistViewModel shareWatchlistViewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);
        this.shareWatchlistController = shareWatchlistController;
        this.shareWatchlistViewModel = shareWatchlistViewModel;
        this.shareWatchlistViewModel.addPropertyChangeListener(this);


        JLabel title = new JLabel("Movie Recommendation Screen");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel keywordPanel = new JPanel();
        keywordPanel.add(new JLabel("Enter your keyword here:"));
        keywordPanel.add(keywordInputField);

        JPanel buttons = new JPanel();
        searchButton = new JButton(viewModel.SEARCH_BUTTON_LABEL);
        buttons.add(searchButton);
        searchUsernameButton = new JButton(ShareWatchlistViewModel.SEARCH_USERNAME_LABEL);
        buttons.add(searchUsernameButton);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (evt.getSource().equals(searchButton)) {
                    String keyword = keywordInputField.getText();
                    controller.execute(keyword);
                    System.out.println(keyword);
                    MovieResultViewModel movieResultViewModel = new MovieResultViewModel();
                    showMovieResultView(movieResultViewModel, viewModel);

                }
            }
        });

        searchUsernameButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (e.getSource().equals(searchUsernameButton)) {
                            String currentUser = ""; //TODO
                            String sharedUser = sharedUsernameInputField.getText();
                            shareWatchlistController.execute(currentUser, sharedUser);
                        }
                    }
                });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(title);
        this.add(keywordPanel);
        this.add(errorLabel);
        this.add(buttons);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Click " + e.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("error".equals(evt.getPropertyName())) {
            System.out.println(evt.getPropertyName());
            String error = (String) evt.getNewValue();
            errorLabel.setText(error);
        } else if ("recommendedMovies".equals(evt.getPropertyName())) {
            // Update the recommended movies area when the property changes
//            List<Movie> recommendedMovies = (List<Movie>) evt.getNewValue();
//            updateRecommendedMoviesArea(recommendedMovies);
//            MovieResultViewModel movieResultViewModel = new MovieResultViewModel();
//            showMovieResultView(movieResultViewModel);

//            System.out.println(evt.getPropertyName());
//            MovieResultViewModel movieResultViewModel = (MovieResultViewModel) evt.getNewValue();
//            showMovieResultView(movieResultViewModel);
        } else if ("shareWatchlistState".equals((evt.getPropertyName()))) {
            ShareWatchlistState state = (ShareWatchlistState) evt.getNewValue();
            if (state.getError() != null) {
                JOptionPane.showMessageDialog(this, state.getError());
            } else {
                JOptionPane.showMessageDialog(this, "shared with " + state.getReseiverName());
            }
        }
    }

    private void showMovieResultView(MovieResultViewModel movieResultViewModel, SearchByNameViewModel viewModel) {
        SwingUtilities.invokeLater(() -> {
            MovieResultView resultView = new MovieResultView(movieResultViewModel, viewModel);
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(resultView);
            frame.revalidate();
            frame.repaint();
//            resultView.updateView();
//            frame.setVisible(true);
        });
    }

}
