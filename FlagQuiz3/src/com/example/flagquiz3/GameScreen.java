package com.example.flagquiz3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
	private String correctAnswer;
	private ImageView flagImageView;
	private Button[] buttonArray;
	private Random random;
	private TextView answerTextView;
	private TextView questionNumberTextView;
	private int totalAnswers = 1;
	private int correctAnswers;
	private Handler handler;
	private Animation shakeAnimation;
	private int numberOfQuestions = 10;
	private final int FIVE_QUESTIONS = Menu.FIRST;
	private final int TEN_QUESTIONS = Menu.FIRST + 1;
	private final int FIFTEEN_QUESTIONS = Menu.FIRST + 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_game_screen);

		buttonArray = new Button[4];
		random = new Random();
		handler = new Handler();
		flagNamesList = new ArrayList<String>();

		flagImageView = (ImageView) findViewById(R.id.flagImageView);
		answerTextView = (TextView) findViewById(R.id.answerTextView);
		questionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);

		buttonArray[0] = (Button) findViewById(R.id.button1);
		buttonArray[1] = (Button) findViewById(R.id.button2);
		buttonArray[2] = (Button) findViewById(R.id.button3);
		buttonArray[3] = (Button) findViewById(R.id.button4);

		shakeAnimation = AnimationUtils.loadAnimation(this,
				R.anim.incorrect_shake);
		shakeAnimation.setRepeatCount(3);

		if (savedInstanceState == null) {
			resetQuiz();
		}
	}

	public void onClickAnyButton(View v) {
		submitGuess((Button) v);
	}

	private void resetQuiz() {
		flagNamesList.clear();
		totalAnswers = 1;
		correctAnswers = 0;
		AssetManager assets = getAssets();
		try {
			String[] counties = assets.list("Europe");
			for (String country : counties) {
				flagNamesList.add(country.replace(".png", ""));
			}
		} catch (IOException e) {

			Log.e("TAG", "Error loading image file names");
		}
		Collections.shuffle(flagNamesList);
		loadNextFlag();
	}

	private void loadNextFlag() {
		int randomIndex;
		InputStream input;
		String currentFlagName;
		AssetManager assets = getAssets();
		answerTextView.setText("");
		questionNumberTextView.setText(getResources().getString(
				R.string.question)
				+ ' '
				+ (totalAnswers)
				+ ' '
				+ getResources().getString(R.string.of)
				+ ' '
				+ numberOfQuestions);
		currentFlagName = flagNamesList.remove(0);
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
				buttonArray[i].setText(flagNamesList.get(i));
			}
		}
	}

	private void submitGuess(Button guessButton) {
		String guess = guessButton.getText().toString();

		++totalAnswers;
		if (guess.equals(correctAnswer)) {
			++correctAnswers;
			answerTextView.setText(correctAnswer + "!");
			answerTextView.setTextColor(getResources().getColor(
					R.color.correct_answer));

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					loadNextFlag();
				}
			}, 500);

		} else {
			flagImageView.startAnimation(shakeAnimation);
			answerTextView.setText(R.string.incorrect_answer);
			answerTextView.setTextColor(getResources().getColor(
					R.color.incorrect_answer));
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					loadNextFlag();
				}
			}, 500);
		}
		if (totalAnswers == numberOfQuestions) {// it work
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle(R.string.reset_quiz);

			builder.setMessage(String.format("%d %s, %.02f%% %s",
					totalAnswers - 1, getResources()
							.getString(R.string.guesses),
					(1000 / (double) correctAnswers), "congratulations!"));

			builder.setCancelable(false);
			builder.setPositiveButton(R.string.reset_quiz,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							resetQuiz();
						}
					});
			AlertDialog resetDialog = builder.create();
			resetDialog.show();
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
		switch (id) {
		case R.id.fiveQuestionsItem:
			numberOfQuestions = 5;
			resetQuiz();
			break;
		case R.id.tenQuestionsItem:
			numberOfQuestions = 10;
			resetQuiz();
			break;
		case R.id.fifteenQuestionsItem:
			numberOfQuestions = 15;
			resetQuiz();
			break;

		}
		if (id == R.id.fiveQuestionsItem) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
