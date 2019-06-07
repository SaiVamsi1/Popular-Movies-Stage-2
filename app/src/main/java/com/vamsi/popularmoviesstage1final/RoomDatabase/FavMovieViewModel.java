package com.vamsi.popularmoviesstage1final.RoomDatabase;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.vamsi.popularmoviesstage1final.ModelMovieData;

import java.util.List;

public class FavMovieViewModel extends AndroidViewModel
{
    private FavMoviesRepository mRepository;
    private LiveData<List<ModelMovieData>> mAllResults;

    public FavMovieViewModel (Application application)
    {
        super(application);
        mRepository = new FavMoviesRepository(application);
        mAllResults = mRepository.getAllResults();
    }

     public LiveData<List<ModelMovieData>> getAllResults() { return mAllResults; }
     public ModelMovieData checkMovieInDatabase(String id)
    {
        return mRepository.checkMovieInDatabase(id);

    }
    public void insert(ModelMovieData result) { mRepository.insert(result); }
    public void delete(ModelMovieData result) { mRepository.delete(result); }
}
