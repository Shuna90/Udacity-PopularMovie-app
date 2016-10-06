package app.com.example.android.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import java.util.HashSet;

/**
 * Created by shuna on 8/17/16.
 */

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {

        String tableName = MovieContract.MovieEntry.TABLE_NAME;

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")", null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> MovieHashSet = new HashSet<String>();
        MovieHashSet.add(MovieContract.MovieEntry._ID);
        MovieHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        MovieHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
        MovieHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW);
        MovieHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE);
        MovieHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE);
        MovieHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH);
        MovieHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            MovieHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                MovieHashSet.isEmpty());
        db.close();
        c.close();
    }

    public long testInsertMovie() {

        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = app.com.example.android.popularmovies.data.TestUtilities.createMovieValues();

        long locationRowId;
        locationRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        assertTrue(locationRowId != -1);

        Cursor cursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        app.com.example.android.popularmovies.data.TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        cursor.close();
        db.close();
        return locationRowId;
    }
}
