package com.example.flagquiz3;

import java.util.Random;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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
	private static final String EXTRA_BUTTONS_TEXT = "buttons_text";
	private static final String EXTRA_IS_GAME_OVER = "game_over";
	private static final String EXTRA_RESULT = "result";
	private static final String EXTRA_IS_RECORD = "is_record";

	private int result;
	private boolean isGameOver = false;
	private boolean isRecord = false;
	private OnClickListener guessButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			submitGuess((Button) v);
			setButtonsEnable(false);
		}
	};

	private void saveSharedPreferences() {
		SharedPreferences sharedPerferences = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPerferences.edit();

		editor.putBoolean(EXTRA_IS_GAME_OVER, isGameOver);
		if (isGameOver) {
			editor.putInt(EXTRA_RESULT, quizer.getResult());
		}
		editor.putInt(EXTRA_QUESTIONS_COUNT, quizer.getQuestionsCount());
		editor.putInt(EXTRA_NUMBER_OF_CURRENT_QUESTION,
				quizer.getNumberOfCurrentQuestion());
		editor.putInt(EXTRA_CORRECT_QUESTIONS_COUNT,
				quizer.getCorrectAnswersCount());
		editor.putString(EXTRA_CURRENT_FLAG_NAME, quizer.getCorrectAnswer());
		String[] buttonsText = new String[4];
		for (int i = 0; i < 4; i++) {
			buttonsText[i] = (String) buttonArray[i].getText();
		}
		editor.commit();
	}

	// @Override
	// protected void onDestroy() {
	// saveSharedPreferences();
	// };

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

		buttonArray[0].setOnClickListener(guessButtonListener);
		buttonArray[1].setOnClickListener(guessButtonListener);
		buttonArray[2].setOnClickListener(guessButtonListener);
		buttonArray[3].setOnClickListener(guessButtonListener);

		shakeAnimation = AnimationUtils.loadAnimation(this,
				R.anim.incorrect_shake);
		shakeAnimation.setRepeatCount(3);

		quizer = new Quizer(this);

		newGame();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(EXTRA_IS_GAME_OVER, isGameOver);
		outState.putBoolean(EXTRA_IS_RECORD, isRecord);
		outState.putInt(EXTRA_QUESTIONS_COUNT, quizer.getQuestionsCount());
		outState.putInt(EXTRA_NUMBER_OF_CURRENT_QUESTION,
				quizer.getNumberOfCurrentQuestion());
		outState.putInt(EXTRA_CORRECT_QUESTIONS_COUNT,
				quizer.getCorrectAnswersCount());
		outState.putString(EXTRA_CURRENT_FLAG_NAME, quizer.getCorrectAnswer());
		String[] buttonsText = new String[4];
		for (int i = 0; i < 4; i++) {
			buttonsText[i] = (String) buttonArray[i].getText();
		}
		outState.putStringArray(EXTRA_BUTTONS_TEXT, buttonsText);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		isGameOver = savedInstanceState.getBoolean(EXTRA_IS_GAME_OVER);
		isRecord = savedInstanceState.getBoolean(EXTRA_IS_RECORD);
		quizer.setQuestionsCount(savedInstanceState
				.getInt(EXTRA_QUESTIONS_COUNT));

		quizer.setCorrectAnswersCount(savedInstanceState
				.getInt(EXTRA_CORRECT_QUESTIONS_COUNT));

		quizer.setNumberOfCurrentQuestion(savedInstanceState
				.getInt(EXTRA_NUMBER_OF_CURRENT_QUESTION));
		quizer.setNewCurrentFlagName(savedInstanceState
				.getString(EXTRA_CURRENT_FLAG_NAME));

		newQuiz();
		String[] buttonsText = new String[4];
		buttonsText = savedInstanceState.getStringArray(EXTRA_BUTTONS_TEXT);
		for (int i = 0; i < 4; i++) {
			buttonArray[i].setText(buttonsText[i]);
		}
		if (isGameOver) {
			showEndAlert();
		}
	}

	/**
	 * This method is called when user click on button.
	 * 
	 * @param guessButton
	 */
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
					if (!quizer.isGameOver()) {
						newQuiz();
					} else {
						result = quizer.getResult();
						isRecord = quizer.isRecord(result);
						showEndAlert();
					}
				}
			}, SHOW_ANSWER_DELAY);
		} else {
			flagImageView.startAnimation(shakeAnimation);
			answerTextView.setText(quizer.getCorrectAnswer() + "!");
			answerTextView.setTextColor(getResources().getColor(
					R.color.incorrect_answer));
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (!quizer.isGameOver()) {
						newQuiz();
					} else {
						result = quizer.getResult();
						isRecord = quizer.isRecord(result);
						showEndAlert();
					}
				}
			}, SHOW_ANSWER_DELAY);
		}
	}

	/**
	 * Load new game.
	 */
	private void newGame() {
		quizer.resetQuiz();
		isGameOver = false;
		newQuiz();
	}

	/**
	 * Load next quiz.
	 */
	private void newQuiz() {
		loadNextFlag();
		loadNextAnswers();
	}

	/**
	 * Load and set next flag image.
	 */
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
	}

	/**
	 * Load and set correct answer to random button and random text to other
	 * buttons, next enable buttons.
	 */
	private void loadNextAnswers() {
		int randomIndex;
		Random random = new Random();
		randomIndex = random.nextInt(4);
		buttonArray[randomIndex].setText(quizer.getCorrectAnswer());
		for (int i = 0; i < 4; i++) {
			if (i != randomIndex) {
				buttonArray[i].setText(quizer.getFlagName(i));
			}
		}
		setButtonsEnable(true);
	}

	/**
	 * Enable or disable buttons.
	 * 
	 * @param enabled
	 */
	private void setButtonsEnable(boolean enabled) {
		for (int i = 0; i < 4; i++) {
			buttonArray[i].setEnabled(enabled);
		}
	}

	/**
	 * Show record table.
	 */
	private void showTopTenDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		String topTen = quizer.getTopTen();
		builder.setMessage(topTen);

		builder.setPositiveButton(R.string.reset_quiz,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						newGame();
					}
				});
		builder.setCancelable(false);
		AlertDialog resetDialog = builder.create();
		resetDialog.show();
	}

	/**
	 * Show AlertDialog when the game is over.
	 */
	private void showEndAlert() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (isRecord) {
			builder.setTitle(R.string.enter_name_request);
			builder.setMessage("Your score: " + result);
			final EditText enterNameEdixText = new EditText(this);
			builder.setView(enterNameEdixText);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							quizer.insertValue(enterNameEdixText.getText()
									.toString(), result);
							showTopTenDialog();
						}
					});
		} else {
			builder.setTitle(R.string.reset_quiz);
			builder.setMessage("Your score: " + result
					+ " You must try again!");
			builder.setPositiveButton(R.string.reset_quiz,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							newGame();
						}
					});
		}
		builder.setCancelable(false);
		AlertDialog resetDialog = builder.create();
		resetDialog.show();
		isGameOver = true;
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
			newGame();
			break;
		case R.id.ten_questions_item:
			quizer.setQuestionsCount(10);
			newGame();
			break;
		case R.id.fifteen_questions_item:
			quizer.setQuestionsCount(15);
			newGame();
			break;

		}
		return super.onOptionsItemSelected(item);
	}
}
