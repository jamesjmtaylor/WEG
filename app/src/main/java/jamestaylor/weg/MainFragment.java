package jamestaylor.weg;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import jamestaylor.weg.Equipment.Equipment;
import jamestaylor.weg.Equipment.EquipmentContent;
import jamestaylor.weg.Equipment.fightingVehicle;
import jamestaylor.weg.Equipment.groundWeapon;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jamestaylor.weg.R;

public class MainFragment extends Fragment {

    ImageView weaponImageView; //displays the weapon picture
    ImageView groupImageView; //displays the weapon group operational graphic
    ImageView individualImageView; //displays the weapon individual operational graphic

    LinearLayout statLinearLayout; // layout of effects

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */

    public static final String ARG_ITEM_ID = "item_id";
    //public static String ARG_ITEM_ID = "item_id";

    /**
     * The weapon content this fragment is presenting.
     */
    private Equipment mEquipment;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the weapon content specified by the fragment
            mEquipment = EquipmentContent.EQUIPMENT_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        // get reference to the ImageViews
        weaponImageView = (ImageView) rootView.findViewById(R.id.picture);
        groupImageView = (ImageView) rootView.findViewById(R.id.groupSymbol);
        individualImageView = (ImageView) rootView.findViewById(R.id.individualSymbol);

        // get a reference to the linear layout
        statLinearLayout = (LinearLayout) rootView.findViewById(R.id.statLinearLayout);

        if (mEquipment != null) {

            AssetManager assets = getActivity().getAssets();
            InputStream pictureStream;
            InputStream groupStream;
            InputStream individualStream;

            try
            {
               // get an InputStream to the asset representing the next picture
                pictureStream = assets.open("Pictures/" + mEquipment.picture + ".png");

                // load the asset as a Drawable and display on the ImageView
                Drawable picture = Drawable.createFromStream(pictureStream, mEquipment.picture);
                weaponImageView.setImageDrawable(picture);
            }
            catch (IOException e)
            {
                Log.e("MainFragment", "Error loading " + mEquipment.picture + ".png", e);
            }
            try
            {
                // get an InputStream to the asset representing the next picture
                groupStream = assets.open("Group/" + mEquipment.groupIcon + ".png");

                // load the asset as a Drawable and display on the ImageView
                Drawable group = Drawable.createFromStream(groupStream, mEquipment.groupIcon);
                groupImageView.setImageDrawable(group);
            }
            catch (IOException e)
            {
                Log.e("MainFragment", "Error loading " + mEquipment.groupIcon + ".png", e);
            }
            try
            {
                // get an InputStream to the asset representing the next picture
                individualStream = assets.open("Individual/" + mEquipment.individualIcon + ".png");

                // load the asset as a Drawable and display on the ImageView
                Drawable individual = Drawable.createFromStream(individualStream, mEquipment.individualIcon);
                individualImageView.setImageDrawable(individual);
            }
            catch (IOException e)
            {
                Log.e("MainFragment", "Error loading " + mEquipment.individualIcon + ".png", e);
            }
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mEquipment.id); // Sets the title text of the fragment
            populateStatistics(inflater);
        }
        return rootView;
    }

    // Updates the displayed range as well as the effects bars for each weapon system
    private String updateRange(int range) {
        if (range > 8000) {
            return (Math.round(((double)range / 1000)) + " kilometers");
        } else {
           return range + " meters";
        }
    }

    public void populateStatistics(LayoutInflater inflater) {
        List<String> statistics = mEquipment.getStatistics();
        Iterator it = statistics.iterator(); //get the iterator for the equipment data
        while (it.hasNext()) {
            //The odd strings are always the category, the even strings are always the statistic itself.
            //I realize this implementation is tightly coupled, but right now good OOP is a nice to
            // have, rather than a must
            String cat = it.next().toString(); // short for category
            it.remove(); // avoids a ConcurrentModificationException
            String stat = it.next().toString(); // short for statistic
            it.remove(); // avoids a ConcurrentModificationException

            // avoids displaying empty values to include no armor, no penetration, no range
                if (!stat.equals("0 mm thick") && !stat.equals("0 mm") &&
                    !stat.equals("null") && !stat.equals("0 meters") && !stat.equals("0 mt")) {
                    TableLayout newLayout = (TableLayout) inflater.inflate(R.layout.statistics_text_view, null);
                    statLinearLayout.addView(newLayout);
                    ((TextView) newLayout.findViewById(R.id.category)).setText(cat);
                    ((TextView) newLayout.findViewById(R.id.statistic)).setText(stat);
                    // Determines whether to bold or not to bold (only weapon names and vehicle values are bold)
                    if ((cat.contains("Maximum") && !(cat.contains("Speed"))) || cat.contains("Minimum Altitude") || cat.contains("Penetration")) {
                        ((TextView) newLayout.findViewById(R.id.category)).setTypeface(null, Typeface.NORMAL);
                        ((TextView) newLayout.findViewById(R.id.statistic)).setTypeface(null, Typeface.NORMAL);
                    }
                }

        }
    }
}
