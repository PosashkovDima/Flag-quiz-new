package com.example.flagquiz3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GameScreen extends ActionBarActivity {
	private List<String> flagNamesList;
	private List<String> quizCountriesList;
	private String correctAnswer;
	private ImageView flagImageView;
	private Button[] buttonArray;
	private Random random;
	private TextView answerTextView;
	private TextView questionNumberTextView;
	private int answersLost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_game_screen);

		buttonArray = new Button[4];
		random = new Random();
		flagNamesList = new ArrayList<String>();
		quizCountriesList = new ArrayList<String>();

		flagImageView = (ImageView) findViewById(R.id.flagImageView);
		answerTextView = (TextView) findViewById(R.id.answerTextView);
		questionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);

		buttonArray[0] = (Button) findViewById(R.id.button1);
		buttonArray[1] = (Button) findViewById(R.id.button2);
		buttonArray[2] = (Button) findViewById(R.id.button3);
		buttonArray[3] = (Button) findViewById(R.id.button4);

		Animation shakeAnimation = AnimationUtils.loadAnimation(this,
				R.anim.incorrect_shake);
		shakeAnimation.setRepeatCount(3);

		if (savedInstanceState == null) {
			makeQuestions(10);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.game_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void onClickAnyButton(View v) {
		makeQuestions(10);
	}

	private void makeQuestions(int numberOfQuestions) {
		flagNamesList.clear();
		quizCountriesList.clear();
		questionNumberTextView.setText(getResources().getString(
				R.string.question)
				+ ' '
				+ (answersLost++ + 1)
				+ ' '
				+ getResources().getString(R.string.of)
				+ ' '
				+ numberOfQuestions);
		loadFlagNames();
		int randomIndex;
		String fileName;
		int numberOfFlags = flagNamesList.size();
		for (int i = 0; i < numberOfQuestions; i++) {
			randomIndex = random.nextInt(numberOfFlags);
			fileName = flagNamesList.get(randomIndex);
			if (!quizCountriesList.contains(fileName)) {
				quizCountriesList.add(fileName);
			}
		}
		loadNextFlag();
	}

	private void loadNextFlag() {
		int randomIndex;
		InputStream input;
		String currentFlagName;
		AssetManager assets = getAssets();
		answerTextView.setText("");

		currentFlagName = quizCountriesList.remove(0);
		correctAnswer = currentFlagName;

		try {
			input = assets.open("Europe/" + currentFlagName + ".png");
			flagImageView.setImageDrawable(Drawable.createFromStream(input,
					currentFlagName));
		} catch (IOException e) {
			Log.e("TAG", "Error loading" + currentFlagName, e);
		}
		randomIndex = random.nextInt(4);
		buttonArray[randomIndex].setText(correctAnswer);
		for (int i = 0; i < 4; i++) {
			if (i != randomIndex) {
				buttonArray[i].setText(quizCountriesList.get(i));
			}
		}
	}

	private List<String> loadFlagNames() {
		AssetManager assets = getAssets();
		try {
			String[] counties = assets.list("Europe");
			for (String country : counties) {
				flagNamesList.add(country.replace(".png", ""));
			}
		} catch (IOException e) {

			Log.e("TAG", "Error loading image file names");
		}
		return flagNamesList;
	}

}
