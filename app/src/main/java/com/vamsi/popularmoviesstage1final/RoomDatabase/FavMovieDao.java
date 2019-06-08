package com.vamsi.popularmoviesstage1final.RoomDatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.vamsi.popularmoviesstage1final.ModelMovieData;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface FavMovieDao
{
    @Insert(onConflict = REPLACE)
    void insert(ModelMovieData favMovie);

    @Delete
    void delete(ModelMovieData favMovie);

    @Query("SELECT * FROM ModelMovieData")
    LiveData<List<ModelMovieData>> getlivedataMovies();

    @Query("SELECT * FROM ModelMovieData WHERE id = :id LIMIT 1")
    ModelMovieData checkMovieInDatabase(String id);
}