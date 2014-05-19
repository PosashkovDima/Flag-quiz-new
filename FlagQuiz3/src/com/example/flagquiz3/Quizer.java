package com.example.flagquiz3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.util.Log;

public class Quizer {
	private static final String DATABASE_NAME = "database2.db";
	private static final String TABLE_NAME = "champions";
	private List<String> flagNamesList;
	private String correctAnswer;

	private int numberOfCurrentQuestion = 1;
	private int correctAnswersCount;
	private int questionsCount = 10;
	private Drawable flagDrawable;
	private Context context;
	private String newCurrentFlagName;
	private ChampionsDatabase dbHelper;
	private SQLiteDatabase sdb;

	public Quizer(Context context) {
		this.context = context;
		flagNamesList = new ArrayList<>();

		createDatabase();
	}

	public void resetQuiz() {
		flagNamesList.clear();
		numberOfCurrentQuestion = 1;
		correctAnswersCount = 0;
		AssetManager assets = context.getAssets();
		try {
			String[] counties = assets.list("Europe");
			for (String country : counties) {
				flagNamesList.add(country.replace(".png", ""));
			}
		} catch (IOException e) {

			Log.e("TAG", "Error loading image file names");
		}
		Collections.shuffle(flagNamesList);
	}

	public void loadNextFlag() {
		AssetManager assets = context.getAssets();
		InputStream input = null;
		String currentFlagName;
		currentFlagName = flagNamesList.remove(0);
		if (newCurrentFlagName != null) {
			currentFlagName = newCurrentFlagName;
			newCurrentFlagName = null;
		}
		correctAnswer = currentFlagName;
		try {
			input = assets.open("Europe/" + currentFlagName + ".png");
			flagDrawable = Drawable.createFromStream(input, currentFlagName);
			Log.e("TAG", "createFromStream");
		} catch (IOException e) {
			Log.e("TAG", "Error loading" + currentFlagName, e);
		}
	}

	public void setNewCurrentFlagName(String newCurrentFlagName) {
		this.newCurrentFlagName = newCurrentFlagName;
	}

	public boolean isAnswerCorrect(String answer) {
		if (answer.equals(correctAnswer)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Create champion's database.
	 */
	private void createDatabase() {
		dbHelper = new ChampionsDatabase(this.context, DATABASE_NAME, null, 1);
		sdb = dbHelper.getWritableDatabase();
	}

	/**
	 * Insert new values to database.
	 * 
	 * @param name
	 * @param result
	 */
	public void insertValue(String name, double result) {
		ContentValues newValues = new ContentValues();
		newValues.put(dbHelper.NAME_COLUMN, name);
		newValues.put(dbHelper.RESULT_COLUMN, result);
		newValues.put(dbHelper.DATE_COLUMN,
				(String) DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date()));
		sdb.insert(TABLE_NAME, null, newValues);
	}

	/**
	 * Return true if result at top-10 else false.
	 * 
	 * @param result
	 * @return
	 */
	public boolean isRecord(double result) {
		Cursor cursor = sdb.rawQuery(
				"SELECT " + dbHelper.NAME_COLUMN + ", "
						+ dbHelper.RESULT_COLUMN + ", " + dbHelper.DATE_COLUMN
						+ " FROM " + TABLE_NAME + " ORDER BY "
						+ dbHelper.RESULT_COLUMN, null);
		// + " ORDER BY " + dbHelper.RESULT_COLUMN
		// + " DESC LIMIT 10"
		// Cursor cursor = sdb.query(TABLE_NAME, new String[] {
		// dbHelper.NAME_COLUMN, dbHelper.RESULT_COLUMN,
		// dbHelper.DATE_COLUMN }, null, null, null, null,
		// dbHelper.RESULT_COLUMN + " DESC", "10");
		// + " ORDER BY " + dbHelper.RESULT_COLUMN
		// + " DESC LIMIT 10"
		double resultLite;
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			Log.e("tag", i + "");
			resultLite = cursor.getDouble(cursor
					.getColumnIndex(dbHelper.RESULT_COLUMN));
			Log.e("tag", resultLite + " resultLite");
			// if (result >= resultLite || cursor.getCount() < 10) {
			// if (result >= resultLite) {
			// return true;
			// }
		}
		return true;
	}

	public String getChampsArray() {

		Cursor cursor = sdb.rawQuery("SELECT " + dbHelper.NAME_COLUMN + ", "
				+ dbHelper.RESULT_COLUMN + ", " + dbHelper.DATE_COLUMN
				+ " FROM " + TABLE_NAME + " ORDER BY " + dbHelper.RESULT_COLUMN
				+ " DESC LIMIT 10", null);
		cursor.moveToFirst();
		String[] champsArray = new String[cursor.getCount()];
		String a = "";
		int i = 0;
		// while (!cursor.moveToNext()) {
		// champsArray[i++] = cursor.getString(cursor
		// .getColumnIndex(dbHelper.NAME_COLUMN))
		// + cursor.getString(cursor
		// .getColumnIndex(dbHelper.RESULT_COLUMN))
		// + cursor.getString(cursor
		// .getColumnIndex(dbHelper.DATE_COLUMN));
		// }

		while (!cursor.moveToNext()) {
			a = cursor.getString(cursor.getColumnIndex(dbHelper.NAME_COLUMN))
					+ cursor.getString(cursor
							.getColumnIndex(dbHelper.RESULT_COLUMN))
					+ cursor.getString(cursor
							.getColumnIndex(dbHelper.DATE_COLUMN)) + "/n";
		}
		return a;
	}

	public void increaseCorrectAnswersCount() {
		correctAnswersCount++;
	}

	public void increaseNumberOfCurrentQuestion() {
		numberOfCurrentQuestion++;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public double getResult() {
		return correctAnswersCount * 1.0 / questionsCount * 1.0;
	}

	public Drawable getFlagDrawable() {
		return flagDrawable;
	}

	public int getCorrectAnswersCount() {
		return correctAnswersCount;
	}

	public void setCorrectAnswersCount(int correctAnswersCount) {
		this.correctAnswersCount = correctAnswersCount;
	}

	public String getFlagName(int i) {
		return flagNamesList.get(i);
	}

	public int getNumberOfCurrentQuestion() {
		return numberOfCurrentQuestion;
	}

	public void setNumberOfCurrentQuestion(int numberOfCurrentQuestion) {
		this.numberOfCurrentQuestion = numberOfCurrentQuestion;
	}

	public int getQuestionsCount() {
		return questionsCount;
	}

	public void setQuestionsCount(int questionsCount) {
		this.questionsCount = questionsCount;
	}

	public boolean isGameOver() {
		return (numberOfCurrentQuestion == questionsCount + 1);
	}

}
