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
import android.widget.ImageView;

public class GameScreen extends ActionBarActivity {
	private List<String> flagNameList;
	private List<String> quizCountriesList;
	private String correctAnswer;
	private ImageView flagImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_game_screen);
		flagImageView = (ImageView) findViewById(R.id.flagImageView);
		flagNameList = new ArrayList<String>();
		quizCountriesList = new ArrayList<String>();
		Animation shakeAnimation = AnimationUtils.loadAnimation(this,
				R.anim.incorrect_shake);
		shakeAnimation.setRepeatCount(3);
		if (savedInstanceState == null) {
			resetQuiz(1);
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
		resetQuiz(1);
	}

	private void resetQuiz(int numberOfQuestions) {
		AssetManager assets = getAssets();
		flagNameList.clear();
		quizCountriesList.clear();
		try {
			Log.e("nnn", "assets.list();");
			String[] counties = assets.list("Europe");

			Log.e("aaa", "assets.list();");
			for (String country : counties) {
				flagNameList.add(country.substring(7, country.indexOf('.')));
			}
		} catch (IOException e) {

			Log.e("TAG", "Error loading image file names");
		}
		Random random = new Random();
		int randomIndex;
		int numberOfFlags = flagNameList.size();
		String fileName;
		for (int i = 0; i < numberOfQuestions; i++) {
			randomIndex = random.nextInt(numberOfFlags);
			fileName = flagNameList.get(randomIndex);
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

	}
}
