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
	private int numberOfCurrentQuestion = 1;
	private int correctAnswersCount;
	private Handler handler;
	private Animation shakeAnimation;
	private int questionsCount = 5;
	private static final int SHOW_ANSWER_DELAY = 500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_game_screen);

		buttonArray = new Button[4];
		random = new Random();
		handler = new Handler();
		flagNamesList = new ArrayList<String>();

		flagImageView = (ImageView) findViewById(R.id.flag_image_view);
		answerTextView = (TextView) findViewById(R.id.answer_text_view);
		questionNumberTextView = (TextView) findViewById(R.id.question_number_text_view);

		buttonArray[0] = (Button) findViewById(R.id.button_up_left);
		buttonArray[1] = (Button) findViewById(R.id.button_up_right);
		buttonArray[2] = (Button) findViewById(R.id.button_down_left);
		buttonArray[3] = (Button) findViewById(R.id.button_down_right);

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
		numberOfCurrentQuestion = 1;
		correctAnswersCount = 0;
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
				+ (numberOfCurrentQuestion)
				+ ' '
				+ getResources().getString(R.string.of) + ' ' + questionsCount);
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

	private void showEndAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.reset_quiz);
		double a = correctAnswersCount * 1.0;
		double b = questionsCount * 1.0;
		double result = a / b;
		builder.setMessage(String.format("%.02f",result * 100) + "%");

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

	private boolean isEnd() {
		return (numberOfCurrentQuestion == questionsCount + 1);
	}

	private void submitGuess(Button guessButton) {
		String guess = guessButton.getText().toString();
		++numberOfCurrentQuestion;
		if (guess.equals(correctAnswer)) {
			++correctAnswersCount;
			answerTextView.setText(correctAnswer + "!");
			answerTextView.setTextColor(getResources().getColor(
					R.color.correct_answer));

			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (!isEnd()) {
						loadNextFlag();
					} else {
						showEndAlert();
					}
				}
			}, SHOW_ANSWER_DELAY);

		} else {
			flagImageView.startAnimation(shakeAnimation);
			answerTextView.setText(R.string.incorrect_answer);
			answerTextView.setTextColor(getResources().getColor(
					R.color.incorrect_answer));
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (!isEnd()) {
						loadNextFlag();
					} else {
						showEndAlert();
					}
				}
			}, SHOW_ANSWER_DELAY);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.five_questions_item:
			questionsCount = 5;
			resetQuiz();
			break;
		case R.id.ten_questions_item:
			questionsCount = 10;
			resetQuiz();
			break;
		case R.id.fifteen_questions_item:
			questionsCount = 15;
			resetQuiz();
			break;

		}
		return super.onOptionsItemSelected(item);
	}
}
