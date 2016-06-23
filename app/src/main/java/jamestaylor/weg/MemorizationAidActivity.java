package jamestaylor.weg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import jamestaylor.weg.Equipment.Equipment;
import jamestaylor.weg.Equipment.EquipmentContent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import jamestaylor.weg.R;

/**
 * Created by James on 5/27/2015.
 * Activity intended to allow users to memorize facts from the WEG
 */
/*
* TODO : Switch from Equipment ArrayList to String ArrayList for notSeenList and quizWeaponsList
* TODO : Something is terribly wrong with MemorizationAid after implementing the OOP classes
 * Trying to keep an ArrayList variable of weapons is disastrous. Instead switch to simple
 */
public class MemorizationAidActivity extends ActionBarActivity {
    // TODO : Eventually create a category filter for quiz subjects
    //private Map<String, Boolean> quizCategoryMap; // Which category is enabled
    private List<String> quizWeaponsList; // names of the weapons in the quiz
    private List<String> notSeenList; // names of the weapons in the quiz
    // TODO : Eventually create a category filter for quiz subjects
    //private ArrayList<String> quizCategories = new ArrayList<String>(); // names of the categories for the quiz

    private String correctAnswerPicture; // Variable containing the correct answer
    private int correctButtonRow; // Variable containing the row of the correct answer
    private int correctButtonCol; // Variable containing the column of the correct answer
    private int totalGuesses; // Number of guesses made
    private int correctAnswers; // Number of correct guesses
    private int guessRows; // Number of rows displaying choices
    private Random random; // Random number generator
    private Handler handler; // Used to delay loading the next weapon
    private Animation shakeAnimation; // Animation for incorrect guess

    private TextView answerTextView; // displays Correct! or Incorrect!
    private TextView questionNumberTextView; // Shows current question #
    private ImageView weaponImageView; // displays a weapon
    private TableLayout buttonTableLayout; // table of answer buttons

    //The below is for restoring the Activity fields after being restarted, i.e. after an orientation change
    private static final String CORRECT_ANSWERS = "correctAnswers";
    private static final String TOTAL_GUESSES = "totalGuesses";
    private static final String GUESS_ROWS = "guessRows";
    private static final String QUIZ_LIST = "quizList";
    private static final String NOT_SEEN_LIST = "seenList";
    private static final String CORRECT_BUTTON_ROW = "correctButtonRow";
    private static final String CORRECT_BUTTON_COL = "correctButtonCol";
    //private static final String CATEGORY_LIST = "categoryList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memorization_aid_activity);

        // TODO : Eventually create a category filter for quiz subjects
        //quizCategoryMap = new HashMap<>(); // Hashmap of equipment characteristics categories that are boolean values paired with the categories
        quizWeaponsList = new ArrayList<>();
        notSeenList = new ArrayList<>();
        guessRows = 1; // Default to one row of choices
        random = new Random(); // Initialize the random number generator
        handler = new Handler();

        // load the shake animation that's used for incorrect answers
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3); // Repeat the shake 3 times

        // TODO : Eventually create a category filter for quiz subjects
        // get array of equipment characteristics from strings.xml
        //String[] categories = getResources().getStringArray(R.array.categories);


        // by default, users are quizzed on all characteristics
        /*for (String characteristics : categories)
            quizCategoryMap.put(characteristics, true);*/

        // get references to the GUI components
        questionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);
        weaponImageView = (ImageView) findViewById(R.id.weaponImageView);
        buttonTableLayout = (TableLayout) findViewById(R.id.buttonTableLayout);
        answerTextView = (TextView) findViewById(R.id.answerTextView);

        // set questionNumberTextView's text
        questionNumberTextView.setText(getResources().getString(R.string.question) + " 1 " +
                getResources().getString(R.string.of) + " 10");

        if (savedInstanceState == null) {
            resetQuiz(); // start a new quiz
        } else {
            reloadQuiz(savedInstanceState);
        }
    }

    // Reloads the quiz settings after the activity has been killed and restarted (i.e. after an orientation change)
    private void reloadQuiz(Bundle past) {
        int savedCorrectAnswers = past.getInt(CORRECT_ANSWERS);
        int savedTotalGuesses = past.getInt(TOTAL_GUESSES);
        int savedGuessRows = past.getInt(GUESS_ROWS);
        int savedCorrectButtonRow = past.getInt(CORRECT_BUTTON_ROW);
        int savedCorrectButtonCol = past.getInt(CORRECT_BUTTON_COL);
        ArrayList  savedQuizList = past.getStringArrayList(QUIZ_LIST);
        ArrayList savedNotSeenList = past.getStringArrayList(NOT_SEEN_LIST);
        //ArrayList savedQuizCategories = past.getStringArrayList(CATEGORY_LIST);
        correctAnswers = savedCorrectAnswers;
        totalGuesses = savedTotalGuesses;
        guessRows = savedGuessRows;
        // TODO : Eventually create a category filter for quiz subjects
        //quizCategories = savedQuizCategories;
        correctButtonRow = savedCorrectButtonRow;
        correctButtonCol = savedCorrectButtonCol;
        quizWeaponsList = List.class.cast(savedQuizList);
        notSeenList = List.class.cast(savedNotSeenList);

        // get file name of the next weapon
        String nextWeapon = quizWeaponsList.get(quizWeaponsList.size()-1);
        correctAnswerPicture = getWeapon(nextWeapon).picture; // update the correct answer to the first item
        // in the quizWeaponsList

        answerTextView.setText(""); // clear the answerTextView

        // display the number of the current quiz question number
        questionNumberTextView.setText(getResources().getString(R.string.question) + " " +
                (correctAnswers + 1) + " " + getResources().getString(R.string.of) + " 10");

        // use the AssetManager to load next image from assets folder
        // use the AssetManager to get the weapon image
        // file names for only the enabled weapons
        AssetManager assets = getAssets();
        InputStream pictureStream;
        try
        {
            // get an InputStream to the asset representing the next picture
            pictureStream = assets.open("Pictures/" + correctAnswerPicture + ".png");

            // load the asset as a Drawable and display on the ImageView
            Drawable picture = Drawable.createFromStream(pictureStream, correctAnswerPicture);
            weaponImageView.setImageDrawable(picture);
        }
        catch (IOException e)
        {
            Log.e("MainFragment", "Error loading " + correctAnswerPicture + ".png", e);
        }

        // get a reference to the LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // add 3, 6, or 9 answer Buttons based on the value of guessRows
        for (int row = 0; row < guessRows; row++) {
            TableRow currentTableRow = getTableRow(row);
            for (int column = 0; column < 3; column++){
                // inflate guess_button.xml to create new Button
                Button newGuessButton = (Button) inflater.inflate(R.layout.guess_button, null);
                // get the weapon characteristic and set it as newGuessButton's text
                String choice = quizWeaponsList.get((row * 3) + column);
                newGuessButton.setText(choice);

                // register answerButtonListener to respond to button clicks
                newGuessButton.setOnClickListener(guessButtonListener);
                currentTableRow.addView(newGuessButton);
            }
        }

        // replace one Button with the correct answer
        TableRow correctTableRow = getTableRow(correctButtonRow); // get the TableRow
        ((Button)correctTableRow.getChildAt(correctButtonCol)).setText(nextWeapon);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CORRECT_ANSWERS, correctAnswers);
        outState.putInt(TOTAL_GUESSES, totalGuesses);
        outState.putInt(GUESS_ROWS, guessRows);
        outState.putInt(CORRECT_BUTTON_ROW, correctButtonRow);
        outState.putInt(CORRECT_BUTTON_COL, correctButtonCol);
        // TODO : Eventually create a category filter for quiz subjects
        //outState.putStringArrayList(CATEGORY_LIST, quizCategories);
        ArrayList quizWeaponsStringArrayList = ArrayList.class.cast(quizWeaponsList);
        ArrayList notSeenStringArrayList = ArrayList.class.cast(notSeenList);

        outState.putStringArrayList(NOT_SEEN_LIST, notSeenStringArrayList);
        outState.putStringArrayList(QUIZ_LIST, quizWeaponsStringArrayList);
    }

    // set up and start the next quiz
    private void resetQuiz() {
        String nextWeapon;
        correctAnswers = 0; // reset the number of correct answers made
        totalGuesses = 0; // reset the total number of guesses the user made

        // add 10 random file names to the quizWeaponsList
        int weaponCounter = 1;
        int numberOfWeapons = EquipmentContent.EQUIPMENT_LIST.size();

        quizWeaponsList.clear();
        while (weaponCounter <= 10) {
            int randomIndex = random.nextInt(numberOfWeapons); // random index

            // get the random file name
            Equipment equipment = EquipmentContent.EQUIPMENT_LIST.get(randomIndex);

            if (!quizWeaponsList.contains(equipment.toString())){
                quizWeaponsList.add(equipment.toString());
                ++weaponCounter;
            }
        }
        // this creates a deep copy of the quizWeaponsList
        notSeenList.clear();
        for (String w : quizWeaponsList) {
            notSeenList.add(w);
        }
        // notSeenList = quizWeaponsList;  // this creates a shallow copy

        loadNextWeapon();
    }

    // A helper method to take the string returned by toString and shorten it
    private String shorten(String longName) {
        Integer descriptionStart = longName.indexOf(";");
        if (descriptionStart > 0) {
            return longName.substring(0, descriptionStart);
        } else {
            return longName;
        }
    }

    // A helper method to take the firer or target string and return a weapon object
    private Equipment getWeapon(String weapon) {
        return EquipmentContent.EQUIPMENT_MAP.get(weapon);
    }

    // After the user guesses the correct answer, load the next weapon system
    private void loadNextWeapon(){
        /* TODO : Eventually create a category filter for quiz subjects MAIN
        * By replacing the nextImageName with a Equipment instead of the string representing
        * the picture you can randomly select the category as well as the picture here!
        * Will need a switch statement to implement.
         */

        // get file name of the next weapon
        String nextWeapon = notSeenList.get(0);
        notSeenList.remove(0);
        correctAnswerPicture = getWeapon(nextWeapon).picture;

        answerTextView.setText(""); // clear the answerTextView

        // display the number of the current quiz question number
        // (you can use correctAnswers + 1 b/c you don't progress until you have the correct answer)
        questionNumberTextView.setText(getResources().getString(R.string.question) + " " +
                (correctAnswers + 1) + " " + getResources().getString(R.string.of) + " 10");

        // use the AssetManager to load next image from assets folder
        // use the AssetManager to get the weapon image
        // file names for only the enabled weapons
        AssetManager assets = getAssets();
        InputStream pictureStream;
        try
        {
            // get an InputStream to the asset representing the next picture
            pictureStream = assets.open("Pictures/" + correctAnswerPicture + ".png");

            // load the asset as a Drawable and display on the ImageView
            Drawable picture = Drawable.createFromStream(pictureStream, correctAnswerPicture);
            weaponImageView.setImageDrawable(picture);
        }
        catch (IOException e)
        {
            Log.e("MainFragment", "Error loading " + correctAnswerPicture + ".png", e);
        }

        // clear prior answer Buttons from TableRows
        for (int row = 0; row < buttonTableLayout.getChildCount(); ++row)
            ((TableRow) buttonTableLayout.getChildAt(row)).removeAllViews();

        // shuffles the file names
        Collections.shuffle(quizWeaponsList);

        // put the correct answer at the end of the file name list
        int correctIndex = quizWeaponsList.indexOf(nextWeapon);

        quizWeaponsList.add(nextWeapon); // creates a copy of the correct answer at the back of the list
        quizWeaponsList.remove(correctIndex); // removes the now unnecessary original weapon

        // get a reference to the LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // add 3, 6, or 9 answer Buttons based on the value of guessRows
        for (int row = 0; row < guessRows; row++) {
            TableRow currentTableRow = getTableRow(row);

            //
            for (int column = 0; column < 3; column++){
                // inflate guess_button.xml to create new Button
                Button newGuessButton = (Button) inflater.inflate(R.layout.guess_button, null);

                // get the weapon characteristic and set it as newGuessButton's text
                String choice = quizWeaponsList.get((row * 3) + column);
                newGuessButton.setText(shorten(choice));

                // register answerButtonListener to respond to button clicks
                newGuessButton.setOnClickListener(guessButtonListener);
                currentTableRow.addView(newGuessButton);
            }
        }

        // randomly replace one Button with the correct answer
        int row = random.nextInt(guessRows); // pick random row
        int column = random.nextInt(3); // pick random column
        correctButtonRow = row; // saves the row for later use
        correctButtonCol = column; // saves the column for later use
        TableRow randomTableRow = getTableRow(row); // get the TableRow
        ((Button)randomTableRow.getChildAt(column)).setText(shorten(nextWeapon));
    }

    // called when a guess Button is touched
    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            submitGuess((Button) v); // pass selected Button to submitGuess
        } // end onClick
    }; //end answerButtonListener

    // returns the specified TableRow
    private TableRow getTableRow(int row) {
        return (TableRow) buttonTableLayout.getChildAt(row);
    }

        // called when the user selects an answer
    private void submitGuess(Button guessButton) {
        String guess = guessButton.getText().toString();
        String answer = shorten(quizWeaponsList.get(quizWeaponsList.size()-1));
        ++totalGuesses;

        // if the guess is correct
        if (guess.equals(answer)) {
            ++correctAnswers; // increment the number of correct answers

            // display "Correct!" in green text
            answerTextView.setText(getResources().getString(R.string.correct));
            answerTextView.setTextColor(getResources().getColor(R.color.correct_answer));

            disableButtons(); // disable all answer Buttons

            // if the user has correctly answered all the questions
            if (correctAnswers == 10) {
                // create a new AlertDialog Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.reset_quiz); // title bar string

                // set the AlertDialog's message to display the game results
                builder.setMessage(String.format("%d %s, %.02f%% %s", totalGuesses,
                        getResources().getString(R.string.guesses), (1000 / (double) totalGuesses),
                        getResources().getString(R.string.correct)));
                builder.setCancelable(false);

                // add "Reset Quiz" Button
                builder.setPositiveButton(R.string.reset_quiz, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resetQuiz();
                    }
                });
                // create AlertDialog from the Builder
                AlertDialog resetDialog = builder.create();
                resetDialog.show(); // display the Dialog
            }
            else { // The answer is correct but the quiz is not over
                //load the next weapon after a split-second delay
                handler.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                loadNextWeapon();
                            }
                        }, 500); // 500 milliseconds for a split-second delay
            }
        } // end if
        else { // the guess was incorrect
            // play in animation
            weaponImageView.startAnimation(shakeAnimation);

            // display "Incorrect!" in red
            answerTextView.setText(R.string.incorrect_answer);
            answerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer));
            guessButton.setEnabled(false); // disable the incorrect answer button
        } // end else
    } // end submitGuess method


    private void disableButtons() {
    for (int row = 0; row < buttonTableLayout.getChildCount(); ++row) {
        TableRow tableRow = (TableRow) buttonTableLayout.getChildAt(row);
        for (int i = 0; i < tableRow.getChildCount(); ++i)
            tableRow.getChildAt(i).setEnabled(false);
    } // end inner for loop
} // end outer for loop

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.aid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
            return true;
        }
        else if (id == R.id.difficulty) {
            // create a list of the possible numbers of answer choices
            final String[] possibleChoices = getResources().getStringArray(R.array.guessesList);

            // create a new AlertDialog Builder and set its title
            AlertDialog.Builder choicesBuilder = new AlertDialog.Builder(this);
            choicesBuilder.setTitle(R.string.choices);

            // add possibleChoices items to the Dialog and set the behavior when one is clicked
            choicesBuilder.setItems(R.array.guessesList, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // update the guessRows to match the user's choice
                    guessRows = Integer.parseInt(possibleChoices[item]) / 3;
                    resetQuiz();
                } // end method onClick
            }); // end call to setItems

            // create an AlertDialog from the Builder
            AlertDialog choicesDialog = choicesBuilder.create();
            choicesDialog.show(); // show the dialog
            return true;
        }

          // TODO : Eventually create a category filter for quiz subjects
/*        else if (id == R.id.category) {
            // get array of equipment category names
            final String[] categoryNames = quizCategoryMap.keySet().toArray(new String[quizCategoryMap.size()]);

            // boolean array representing whether each category is enabled
            boolean[] categoriesEnabled = new boolean[quizCategoryMap.size()];
            for (int i = 0; i < categoriesEnabled.length; ++i)
                categoriesEnabled[i] = quizCategoryMap.get(categoryNames[i]);

            // create an AlertDialog Builder and set the dialog's title
            AlertDialog.Builder categoriesBuilder = new AlertDialog.Builder(this);
            categoriesBuilder.setTitle(R.string.category);

            // add displayNames to the Dialog and set the behavior when one of the items is clicked
            categoriesBuilder.setMultiChoiceItems(categoryNames, categoriesEnabled,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            // include or exclude the clicked category depending on whether or not it's checked
                            quizCategoryMap.put(categoryNames[which], isChecked);
                        } // end method onClick
                    }); // end call to setMultiChoiceItems

            // resets quiz when user presses the "Reset Quiz" Button
            categoriesBuilder.setPositiveButton(R.string.reset_quiz, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int button) {
                    resetQuiz();
                } // end onClick
            }); // end call to method setPositiveButton

            // create a dialog from the Builder
            AlertDialog catDialog = categoriesBuilder.create();
            catDialog.show(); // display the Dialog
            //Iterator entries = quizCategoryMap.entrySet().iterator();

            for (Map.Entry<String, Boolean> entry : quizCategoryMap.entrySet()) {
                if (entry.getValue()) {
                    quizCategories.add(entry.getKey());
                }
            }
            return true;
        }*/
        else if (id == R.id.weapon_calculator) {
            Intent calc = new Intent(this, WeaponCalculatorActivity.class);
            startActivity(calc);
            return true;
        }
        else if (id == R.id.help) {
            // create a new AlertDialog Builder and set its title
            AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
            aboutBuilder.setTitle(R.string.help);
            aboutBuilder.setMessage(R.string.aid_help);

            aboutBuilder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                } // end method onClick
            }); // end call to setNeutralButton

            // create an AlertDialog from the Builder
            AlertDialog aboutDialog = aboutBuilder.create();
            aboutDialog.show(); // show the dialog
            return true;
        }
        else if (id == R.id.about) {
            // create a new AlertDialog Builder and set its title
            AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
            aboutBuilder.setTitle(R.string.about);
            aboutBuilder.setMessage(R.string.about_message);

            aboutBuilder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                } // end method onClick
            }); // end call to setNeutralButton

            // create an AlertDialog from the Builder
            AlertDialog aboutDialog = aboutBuilder.create();
            aboutDialog.show(); // show the dialog
            return true;
        }

        return super.onOptionsItemSelected(item);
    } // End method onOptionsItemSelected
} // End memorization aid activity
