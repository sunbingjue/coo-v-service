package com.idunnolol.moviesdemo.ui;

import android.app.Application;
import android.util.JsonReader;

import com.idunnolol.moviesdemo.data.Movie;
import com.idunnolol.moviesdemo.util.BitmapCache;
import com.idunnolol.moviesdemo.util.ResourceUtils;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MoviesApplication extends Application {

	private List<Movie> mDemoData;

	@Override
	public void onCreate() {
		super.onCreate();

		LocalDate date;

		BitmapCache.init(this);

		// Load all demo data on start; normally a horrible practice but I'll allow it for a demo
		mDemoData = new ArrayList<Movie>();
		try {
			InputStream is = null;
			try {
				is = getAssets().open("data.json");
				JsonReader reader = new JsonReader(new InputStreamReader(is));

				reader.beginArray();
				while (reader.hasNext()) {
					reader.beginObject();

					Movie movie = new Movie();
					while (reader.hasNext()) {
						String name = reader.nextName();

						if (name.equals("name")) {
							movie.setTitle(reader.nextString());
						}
						else if (name.equals("poster")) {
							movie.setPosterResId(ResourceUtils.getIdentifier(R.drawable.class, reader.nextString()));
						}
						else if (name.equals("rating")) {
							movie.setFilmRating(reader.nextString());
						}
						else if (name.equals("score")) {
							movie.setScore(reader.nextInt());
						}
						else if (name.equals("showtimes")) {
							reader.beginArray();
							List<LocalTime> localTimes = new ArrayList<LocalTime>();
							while(reader.hasNext()) {
								localTimes.add(LocalTime.parse(reader.nextString()));
							}
							movie.setShowTimes(localTimes);
							reader.endArray();
						}
						else if (name.equals("daysUntilRelease")) {
							movie.setDaysTillRelease(reader.nextInt());
						}
						else {
							reader.skipValue();
						}
					}
					mDemoData.add(movie);

					reader.endObject();
				}
				reader.endArray();
				String str = convertStreamToString(is);
			}
			finally {
				is.close();
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Preload all bitmaps into memory, so the demo runs smoother
		for (Movie movie : mDemoData) {
			BitmapCache.getBitmap(movie.getPosterResId());
		}
	}

	private static String convertStreamToString(InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	public List<Movie> getDemoData() {
		return mDemoData;
	}
}
