package com.example.flagquiz3;

import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GameScreen extends ActionBarActivity {

	private TextView answerTextView;
	private TextView questionNumberTextView;
	private Animation shakeAnimation;
	private ImageView flagImageView;
	private Button[] buttonArray;
	private Quizer quizer;
	private Handler handler;
	private static final int SHOW_ANSWER_DELAY = 500;
	private static final String EXTRA_QUESTIONS_COUNT = "questions_count";
	private static final String EXTRA_NUMBER_OF_CURRENT_QUESTION = "number_of_current_question";
	private static final String EXTRA_CORRECT_QUESTIONS_COUNT = "current_questions_count";
	private static final String EXTRA_CURRENT_FLAG_NAME = "current_flag_name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_game_screen);

		buttonArray = new Button[4];

		handler = new Handler();

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

		quizer = new Quizer(this);
		newFlag();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(EXTRA_QUESTIONS_COUNT, quizer.getQuestionsCount());
		outState.putInt(EXTRA_NUMBER_OF_CURRENT_QUESTION,
				quizer.getNumberOfCurrentQuestion());
		outState.putInt(EXTRA_CORRECT_QUESTIONS_COUNT,
				quizer.getCorrectAnswersCount());

		outState.putString(EXTRA_CURRENT_FLAG_NAME, quizer.getCorrectAnswer());

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		quizer.setQuestionsCount(savedInstanceState
				.getInt(EXTRA_QUESTIONS_COUNT));

		quizer.setCorrectAnswersCount(savedInstanceState
				.getInt(EXTRA_CORRECT_QUESTIONS_COUNT));

		quizer.setNumberOfCurrentQuestion(savedInstanceState
				.getInt(EXTRA_NUMBER_OF_CURRENT_QUESTION));
		quizer.setNewCurrentFlagName(savedInstanceState
				.getString(EXTRA_CURRENT_FLAG_NAME));
		loadNextFlag();
	}

	private void submitGuess(Button guessButton) {
		String guess = guessButton.getText().toString();
		quizer.increaseNumberOfCurrentQuestion();// 1
		if (quizer.isAnswerCorrect(guess)) {// 2
			quizer.increaseCorrectAnswersCount();// 3
			answerTextView.setText(quizer.getCorrectAnswer() + "!");// 4
			answerTextView.setTextColor(getResources().getColor(
					R.color.correct_answer));
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (!quizer.isEnd()) {
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
					if (!quizer.isEnd()) {
						loadNextFlag();
					} else {
						showEndAlert();
					}
				}
			}, SHOW_ANSWER_DELAY);
		}
	}

	public void onClickAnyButton(View v) {
		submitGuess((Button) v);
	}

	private void newFlag() {
		quizer.resetQuiz();
		loadNextFlag();
	}

	private void loadNextFlag() {
		quizer.loadNextFlag();
		flagImageView.setImageDrawable(quizer.getFlagDrawable());
		answerTextView.setText("");
		questionNumberTextView.setText(getResources().getString(
				R.string.question)
				+ ' '
				+ (quizer.getNumberOfCurrentQuestion())
				+ ' '
				+ getResources().getString(R.string.of)
				+ ' '
				+ quizer.getQuestionsCount());
		int randomIndex;
		Random random = new Random();
		randomIndex = random.nextInt(4);
		buttonArray[randomIndex].setText(quizer.getCorrectAnswer());
		for (int i = 0; i < 4; i++) {
			if (i != randomIndex) {
				buttonArray[i].setText(quizer.getFlagName(i));
			}
		}
	}

	private void showEndAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.reset_quiz);
		builder.setMessage(String.format("%.02f", quizer.getResult() * 100)
				+ "%");
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.reset_quiz,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						newFlag();
					}
				});
		AlertDialog resetDialog = builder.create();
		resetDialog.show();
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
			quizer.setQuestionsCount(5);
			newFlag();
			break;
		case R.id.ten_questions_item:
			quizer.setQuestionsCount(10);
			newFlag();
			break;
		case R.id.fifteen_questions_item:
			quizer.setQuestionsCount(15);
			newFlag();
			break;

		}
		return super.onOptionsItemSelected(item);
	}
}
