package jamestaylor.weg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import jamestaylor.weg.Equipment.Equipment;
import jamestaylor.weg.Equipment.EquipmentContent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import jamestaylor.weg.R;

/**
 * Created by James on 5/27/2015.
 */
public class WeaponCalculatorActivity extends ActionBarActivity {
    // constants used when saving/restoring state
    private static final String RANGE = "RANGE";
    private static final String MAX = "MAX";
    private static final String FIRER = "FIRER";
    private static final String TARGET = "TARGET";

    // variables used throughout the class
    private int range;
    private int max;
    private String firer;
    private String target;

    // XML files to manipulate and reference
    private ImageView firerView; // Displays an image of the firer
    private ImageView targetView; // Displays an image of the firer
    private Spinner firerSpinner; // Gets the users selection of firer
    private Spinner targetSpinner; // Gets the users selection of firer
    private TextView rangeText; // Displays the current range on the seekBar
    private LinearLayout effectLinearLayout; // table of effects
    private SeekBar rangeSeekBar; // table of effects

    // The adapter to convert the Weapons list into something Spinner can use and display
    ArrayAdapter<Equipment> cAdapter;

    // Get the weapons represented by the firer and target Strings from the Spinner Listeners
    private Equipment fEquipment;
    private Equipment tEquipment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weapon_calculator_activity);

        // get references to the XML components and set listeners to those that will change
        firerView = (ImageView) findViewById(R.id.firerImageView);
        targetView = (ImageView) findViewById(R.id.targetImageView);

        // get a reference to the linear layout
        effectLinearLayout = (LinearLayout) findViewById(R.id.effectLinearLayout);

        // Set the seekBar, its listener, and the rangeView that represents it
        rangeSeekBar = (SeekBar) findViewById(R.id.rangeSeekBar);
        rangeSeekBar.setOnSeekBarChangeListener(rangeSeekBarListener);
        rangeText = (TextView) findViewById(R.id.rangeTextView);

        // references to the Spinners as well as listeners and ArrayAdapters to receive user inputs
        firerSpinner = (Spinner) findViewById(R.id.firerSpinner);
        targetSpinner = (Spinner) findViewById(R.id.targetSpinner);



        // Create an ArrayAdapter using the equipment class and add all the equipment to the spinners
        cAdapter = new ArrayAdapter<Equipment>(this,
                R.layout.support_simple_spinner_dropdown_item,
                EquipmentContent.EQUIPMENT_LIST);
        firerSpinner.setAdapter(cAdapter);
        targetSpinner.setAdapter(cAdapter);

        // create the listeners for the spinners.  The runnable is necessary to prevent the
        // application from initializing with a weapon already selected.
        firerSpinner.post(new Runnable() {
            public void run() {
                firerSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                    public void onItemSelected(AdapterView parent, View view, int pos, long id) {
                        firer = parent.getItemAtPosition(pos).toString();
                        target = targetSpinner.getSelectedItem().toString();
                        updatePictures();
                        updateEffects();
                    }
                    public void onNothingSelected(AdapterView parent){ // Do nothing
                    }
                });
            }
        });

        targetSpinner.post(new Runnable() {
            public void run() {
                targetSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                    public void onItemSelected(AdapterView parent, View view, int pos, long id) {
                        target = parent.getItemAtPosition(pos).toString();
                        firer = firerSpinner.getSelectedItem().toString();
                        updatePictures();
                        updateEffects();
                    }
                    public void onNothingSelected(AdapterView parent){ // Do nothing
                    }
                });
            }
        });






        // check if the app just started or is being restored from memory
        if (savedInstanceState == null) { // The app just started
            initiateVanillaActivity(); // load the basic presets

        } else { // the app is being restored from memory
            range = savedInstanceState.getInt(RANGE);
            max = savedInstanceState.getInt(MAX);
            if (savedInstanceState.getString(FIRER) != null) {
                firer = savedInstanceState.getString(FIRER);
                target = savedInstanceState.getString(TARGET);
                updatePictures();
                updateRange();
                updateEffects();
            }
        }
    }
    private void updateEffects() {
        tEquipment = getWeapon(target);
        fEquipment = getWeapon(firer);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        clearEffects();

        List<String> effects = fEquipment.getFirerEffects(tEquipment, range);
        Iterator it = effects.iterator(); //get the iterator for the equipment data
        while (it.hasNext()) {
            String effect = it.next().toString();
            it.remove(); // avoids a ConcurrentModificationException

            TextView newLayout = (TextView) inflater.inflate(R.layout.effects_text_view, null);
            effectLinearLayout.addView(newLayout);
            ((TextView) newLayout.findViewById(R.id.WeaponEffect)).setText(effect);

            if (effect.contains("is out of range") || effect.contains("cannot engage this target")) {
                newLayout.setBackgroundResource(R.drawable.black_background);
                newLayout.setTextColor(getResources().getColor(R.color.white));
            } else if (effect.contains("is ineffective") || effect.contains("too small")) {
                newLayout.setBackgroundResource(R.drawable.red_background);
                newLayout.setTextColor(getResources().getColor(R.color.black));
            } else if (effect.contains("effective against flanks") || effect.contains("ceiling")) {
                newLayout.setBackgroundResource(R.drawable.yellow_background);
                newLayout.setTextColor(getResources().getColor(R.color.black));
            } else if (effect.contains("is effective")) {
                newLayout.setBackgroundResource(R.drawable.green_background);
                newLayout.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(RANGE, range);
        outState.putInt(MAX, max);
        outState.putString(FIRER, firer);
        outState.putString(TARGET, target);
    }
    // Activity to initiate the basic settings and pictures
    private void initiateVanillaActivity() {
        range = 1000;
        max = 5000;
        loadPicture("target", targetView);
        loadPicture("firer", firerView);
    }

    // Activity to update the settings and pictures
    private void updatePictures() {
        tEquipment = getWeapon(target);
        fEquipment = getWeapon(firer);
        loadPicture(tEquipment.picture, targetView);
        loadPicture(fEquipment.picture, firerView);
    }
    // A helper method to take the firer or target weapon object and the imageView and load it
    private void loadPicture(String picture, ImageView view) {
        AssetManager assets = getAssets();
        InputStream pictureStream;

        try
        {
            // get an InputStream to the asset representing the next picture
            pictureStream = assets.open("Pictures/" + picture + ".png");

            // load the asset as a Drawable and display on the ImageView
            Drawable selectedPicture = Drawable.createFromStream(pictureStream, picture);
            view.setImageDrawable(selectedPicture);
        }
        catch (IOException e)
        {
            Log.e("WeaponCalcActivity", "Error loading " + picture + ".png", e);
        }
    }
    // A helper method to take the firer or target string and return a weapon object
    private Equipment getWeapon(String weapon) {
        return EquipmentContent.EQUIPMENT_MAP.get(weapon);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calculator, menu);
        return true;
    }
    private OnSeekBarChangeListener rangeSeekBarListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            range = seekBar.getProgress();
            updateRange();
            if (target != null) {
                updateEffects();
            }

        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {        }
    };

    // Updates the displayed range as well as the effects bars for each weapon system
    private void updateRange() {
        if (range > 5000) {
            rangeText.setText((double)Math.round(((double)range / 1000)*10)/10 + " kilometers");
        } else {
            rangeText.setText(range + " meters");
        }
    }
    // helper method that clears prior effects
    private void clearEffects() { effectLinearLayout.removeAllViews();}

    // helper method that returns the specified TableRow
    private TableRow getTableRow(int row) {
        return (TableRow) effectLinearLayout.getChildAt(row);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            Intent calc = new Intent(this, MainActivity.class);
            startActivity(calc);
            return true;
        }
        else if (id == R.id.memorization_aid) {
            Intent calc = new Intent(this, MemorizationAidActivity.class);
            startActivity(calc);
            return true;
        }
        else if (id == R.id.adjust_slider_maximum) {
            // create a new AlertDialog Builder and set its title
            AlertDialog.Builder maxBuilder = new AlertDialog.Builder(this);
            maxBuilder.setTitle(R.string.adjust_slider_maximum);
            final EditText rangeEditText = new EditText(this);
            maxBuilder.setView(rangeEditText);

            maxBuilder.setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    try
                    {
                        max = Integer.parseInt(rangeEditText.getText().toString());
                        if (max > range) {
                            range = max;
                            rangeSeekBar.setProgress(range);
                            rangeSeekBar.setMax(max);
                            updateRange();
                        } else {
                            rangeSeekBar.setMax(max);
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        Log.e("WeaponCalcActivity", "Error parsing " + rangeEditText.getText().toString() + " into an int", e);
                        Toast.makeText(WeaponCalculatorActivity.this, "Please enter a numerical value without any letters or special characters", Toast.LENGTH_SHORT).show();
                    }
                } // end method onClick
            }); // end call to setPositiveButton
            maxBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                } // end method onClick
            }); // end call to setPositiveButton

            // create an AlertDialog from the Builder
            AlertDialog maxDialog = maxBuilder.create();
            maxDialog.show(); // show the dialog
            return true;
        }
        else if (id == R.id.manually_enter_range) {
            // create a new AlertDialog Builder and set its title
            AlertDialog.Builder manualBuilder = new AlertDialog.Builder(this);
            manualBuilder.setTitle(R.string.manually_enter_range);
            final EditText rangeEditText = new EditText(this);
            manualBuilder.setView(rangeEditText);

            manualBuilder.setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    try
                    {
                        range = Integer.parseInt(rangeEditText.getText().toString()); // try interpretting the user input
                        if (range > max) {
                            max = range;
                            rangeSeekBar.setMax(range);
                            rangeSeekBar.setProgress(max);
                        } else {
                            rangeSeekBar.setProgress(range);
                        }
                        updateRange();
                    }
                    catch (NumberFormatException e)
                    {
                        Log.e("WeaponCalcActivity", "Error parsing " + rangeEditText.getText().toString() + " into an int", e);
                        Toast.makeText(WeaponCalculatorActivity.this, "Please enter a numerical value without any letters or special characters", Toast.LENGTH_SHORT).show();
                    }
                } // end method onClick
            }); // end call to setPositiveButton
            manualBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                } // end method onClick
            }); // end call to setPositiveButton

            // create an AlertDialog from the Builder
            AlertDialog manualDialog = manualBuilder.create();
            manualDialog.show(); // show the dialog
            return true;
        }
        else if (id == R.id.help) {
            // create a new AlertDialog Builder and set its title
            AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
            aboutBuilder.setTitle(R.string.help);
            aboutBuilder.setMessage(R.string.calculator_help);

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
    }
}
