package com.example.flagquiz3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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

public class GameScreen extends ActionBarActivity {
	private List<String> flagNamesList;
	private List<String> quizCountriesList;
	private String correctAnswer;
	private ImageView flagImageView;
	private Button[] buttonArray;
	private Random random;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_game_screen);
		flagImageView = (ImageView) findViewById(R.id.flagImageView);
		buttonArray = new Button[4];

		buttonArray[0] = (Button) findViewById(R.id.button1);
		buttonArray[1] = (Button) findViewById(R.id.button2);
		buttonArray[2] = (Button) findViewById(R.id.button3);
		buttonArray[3] = (Button) findViewById(R.id.button4);

		random = new Random();
		flagNamesList = new ArrayList<String>();
		quizCountriesList = new ArrayList<String>();

		Animation shakeAnimation = AnimationUtils.loadAnimation(this,
				R.anim.incorrect_shake);
		shakeAnimation.setRepeatCount(3);
		if (savedInstanceState == null) {
			resetQuiz(10);
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
		resetQuiz(10);
	}

	private void resetQuiz(int numberOfQuestions) {
		AssetManager assets = getAssets();
		flagNamesList.clear();
		quizCountriesList.clear();
		try {
			String[] counties = assets.list("Europe");
			for (String country : counties) {
				flagNamesList.add(country.substring(7, country.indexOf('.')));
			}
		} catch (IOException e) {

			Log.e("TAG", "Error loading image file names");
		}
		int randomIndex;
		int numberOfFlags = flagNamesList.size();
		String fileName;
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
		String currentImageName = quizCountriesList.remove(0);
		correctAnswer = currentImageName;
		// text 1 of 10.. correctAnswerTextView too
		
		AssetManager assets = getAssets();
		InputStream input;
		try {
			input = assets.open("Europe/Europe-" + currentImageName + ".png");
			flagImageView.setImageDrawable(Drawable.createFromStream(input,
					currentImageName));
		} catch (IOException e) {
			Log.e("TAG", "Error loading" + currentImageName, e);
		}
		Collections.shuffle(quizCountriesList);
		int randomIndex;
		randomIndex = random.nextInt(4);
		buttonArray[randomIndex].setText(getCountryName(correctAnswer));
		for (int i = 0; i < 4; i++) {
			if (i != randomIndex) {
				buttonArray[i]
						.setText(getCountryName(quizCountriesList.get(i)));
			}
		}
	}

	private String getCountryName(String name) {
		return name.replace('_', ' ');
	}
}
