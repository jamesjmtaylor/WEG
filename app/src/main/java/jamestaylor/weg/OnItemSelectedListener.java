package jamestaylor.weg;

import android.view.View;
import android.widget.AdapterView;

public class OnItemSelectedListener implements AdapterView.OnItemSelectedListener {
    public void onItemSelected(AdapterView parent, View view, int pos, long id) {
        String selected = parent.getItemAtPosition(pos).toString();
    }
    public void onNothingSelected(AdapterView parent){}
}
