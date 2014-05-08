package com.example.flagquiz3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Quizer {
	private List<String> flagNamesList;
	private String correctAnswer;

	private int numberOfCurrentQuestion = 1;
	private int correctAnswersCount;
	private int questionsCount = 10;
	private Drawable flagDrawable;
	private Context context;

	public Quizer(Context context) {
		this.context = context;
		flagNamesList = new ArrayList<String>();

		// resetQuiz();
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
		correctAnswer = currentFlagName;
		try {
			input = assets.open("Europe/" + currentFlagName + ".png");
			flagDrawable = Drawable.createFromStream(input, currentFlagName);
			Log.e("TAG", "createFromStream");
		} catch (IOException e) {
			Log.e("TAG", "Error loading" + currentFlagName, e);
		}
	}

	public Drawable getFlagDrawable() {
		return flagDrawable;
	}

	public String getFlagName(int i) {
		return flagNamesList.get(i);
	}

	public int getNumberOfCurrentQuestion() {
		return numberOfCurrentQuestion;
	}

	public boolean isAnswerCorrect(String answer) {
		if (answer.equals(correctAnswer)) {
			return true;
		} else {
			return false;
		}
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

	public double getResult() {
		return correctAnswersCount * 1.0 / questionsCount * 1.0;
	}

	public void setQuestionsCount(int questionsCount) {
		this.questionsCount = questionsCount;
	}

	public boolean isEnd() {
		return (numberOfCurrentQuestion == questionsCount + 1);
	}

}
