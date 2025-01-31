package usecase_adaptor.DeleteWatchlist;

import usecase_adaptor.ViewModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class DeleteWatchlistViewModel extends ViewModel {

    private DeleteWatchlistState state = new DeleteWatchlistState();

    public static String DELETE_WATCHLIST_BUTTON_LABEL = "delete from watchlist";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    /**
     * initiate view model.
     */
    public DeleteWatchlistViewModel() {super("DeleteWatchlist");}

    /**
     *
     * @return the state stored in view model.
     */
    public DeleteWatchlistState getState() {
        return state;
    }

    /**
     * announce change in state to listeners.
     */
    @Override
    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this.state);
    }

    /**
     * add listener.
     * @param listener who listens to this view model.
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}