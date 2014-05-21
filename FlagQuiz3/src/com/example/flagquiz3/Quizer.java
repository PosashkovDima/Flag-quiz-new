package com.example.flagquiz3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
	private static final String DATABASE_NAME = "playersdatabase.db";
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

	/**
	 * Reload game.
	 */
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
		} catch (IOException e) {
			Log.e("TAG", "Error loading" + currentFlagName, e);
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
	public void insertValue(String name, int result) {
		ContentValues newValues = new ContentValues();
		newValues.put(dbHelper.NAME_COLUMN, name);
		newValues.put(dbHelper.RESULT_COLUMN, result);
		newValues.put(dbHelper.DATE_COLUMN,
				(String) DateFormat.format("dd.MM kk:mm", new Date()));
		sdb.insert(TABLE_NAME, null, newValues);
	}

	/**
	 * Return true if result at top-10 else false.
	 * 
	 * @param result
	 * @return
	 */
	public boolean isRecord(int result) {
		Cursor cursor = sdb.rawQuery("SELECT " + dbHelper.NAME_COLUMN + ", "
				+ dbHelper.RESULT_COLUMN + ", " + dbHelper.DATE_COLUMN
				+ " FROM " + TABLE_NAME + " ORDER BY " + dbHelper.RESULT_COLUMN
				+ " DESC LIMIT 10", null);
		int resultOfCurrentPosition = 0;
		for (int i = 0; i < 10; i++) {
			cursor.moveToPosition(i);
			resultOfCurrentPosition = cursor.getInt(cursor
					.getColumnIndex(dbHelper.RESULT_COLUMN));
			if (result >= resultOfCurrentPosition) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Make request to sqlite and get champions then put all in string and
	 * return.
	 * 
	 * @return String
	 */
	public String getTopTen() {
		Cursor cursor = sdb.rawQuery("SELECT " + dbHelper.NAME_COLUMN + ", "
				+ dbHelper.RESULT_COLUMN + ", " + dbHelper.DATE_COLUMN
				+ " FROM " + TABLE_NAME + " ORDER BY " + dbHelper.RESULT_COLUMN
				+ " DESC LIMIT 10", null);

		StringBuilder str = new StringBuilder("Score |  name   |  date");
		str.append(System.getProperty("line.separator"));
		for (int i = 0; i < 10; i++) {
			cursor.moveToPosition(i);
			str.append((cursor.getInt(cursor
					.getColumnIndex(dbHelper.RESULT_COLUMN))));
			str.append("      ");
			str.append(cursor.getString(cursor
					.getColumnIndex(dbHelper.NAME_COLUMN)));
			str.append(' ');
			str.append(cursor.getString(cursor
					.getColumnIndex(dbHelper.DATE_COLUMN)));
			str.append(' ');
			str.append(System.getProperty("line.separator"));
		}
		return str.toString();
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
}
