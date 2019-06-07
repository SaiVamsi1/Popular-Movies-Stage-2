package com.vamsi.popularmoviesstage1final.RoomDatabase;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.vamsi.popularmoviesstage1final.ModelMovieData;

import java.util.List;

public class FavMoviesRepository {

    private FavMovieDao mFavMovieDao;
    private LiveData<List<ModelMovieData>> mAllresults;

    FavMoviesRepository(Application application) {
        FavMovieDatabase db = FavMovieDatabase.getDatabase(application);
        mFavMovieDao = db.favMoviedao();
        mAllresults = mFavMovieDao.getlivedataMovies();
    }


    public LiveData<List<ModelMovieData>> getAllResults() {
        return mAllresults;
    }

    public void insert (ModelMovieData result) {
        new insertAsyncTask(mFavMovieDao).execute(result);
    }
    public void delete (ModelMovieData result) {
        new deleteAsyncTask(mFavMovieDao).execute(result);
    }
    public ModelMovieData checkMovieInDatabase(String id)
    {
        ModelMovieData result =  mFavMovieDao.checkMovieInDatabase(id);
        return result;
    }
    private static class insertAsyncTask extends AsyncTask<ModelMovieData, Void, Void> {

        private FavMovieDao mAsyncTaskDao;

        insertAsyncTask(FavMovieDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ModelMovieData... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
    private static class deleteAsyncTask extends AsyncTask<ModelMovieData, Void, Void> {

        private FavMovieDao mAsyncTaskDao;

        deleteAsyncTask(FavMovieDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ModelMovieData... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

}