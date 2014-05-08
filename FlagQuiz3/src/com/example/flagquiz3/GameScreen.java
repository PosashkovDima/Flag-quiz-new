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

		if (savedInstanceState == null) {
			quizer = new Quizer(this);
			quizer.resetQuiz();
		}
	}

	public void onClickAnyButton(View v) {
		submitGuess((Button) v);
	}

	private void loadNextFlag() {
		loadNextFlag();
		flagImageView.setImageDrawable(quizer.getFlagDrawable());
		answerTextView.setText("");
		questionNumberTextView.setText(getResources().getString(
				R.string.question)
				+ " "
				+ (quizer.getNumberOfCurrentQuestion())
				+ " "
				+ getResources().getString(R.string.of) + " 10");
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
						quizer.resetQuiz();
					}
				});
		AlertDialog resetDialog = builder.create();
		resetDialog.show();
	}

	private void submitGuess(Button guessButton) {
		String guess = guessButton.getText().toString();
		quizer.increaseNumberOfCurrentQuestion();
		if (quizer.isAnswerCorrect(guess)) {
			quizer.increaseCorrectAnswersCount();
			answerTextView.setText(quizer.getCorrectAnswer() + "!");
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
			quizer.resetQuiz();
			break;
		case R.id.ten_questions_item:
			quizer.setQuestionsCount(10);
			quizer.resetQuiz();
			break;
		case R.id.fifteen_questions_item:
			quizer.setQuestionsCount(15);
			quizer.resetQuiz();
			break;

		}
		return super.onOptionsItemSelected(item);
	}
}
