package com.vamsi.popularmoviesstage1final.RoomDatabase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;


import com.vamsi.popularmoviesstage1final.ModelMovieData;

import java.util.List;


@Database(entities = {ModelMovieData.class}, version = 1, exportSchema = false)
public abstract class FavMovieDatabase extends RoomDatabase {

    public abstract FavMovieDao favMoviedao();

    private static FavMovieDatabase INSTANCE;

    public static FavMovieDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FavMovieDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FavMovieDatabase.class, "favourites.db")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}