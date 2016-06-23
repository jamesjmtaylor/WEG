package jamestaylor.weg.Equipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A weapon representing a piece of content.
 */
public abstract class Equipment {
    public String id;
    public String picture;
    public String groupIcon;
    public String individualIcon;
    public Integer armorThickness;
    public Integer ceiling;

    @Override
    public String toString() {
        return id;
    }

    public List<String> getFirerEffects(Equipment target, Integer range) { return null;}

    public Equipment () {
        this.id = null;
        this.picture = null;
        this.groupIcon = null;
        this.individualIcon = null;
        this.armorThickness = 0;
        this.ceiling = 0;
    }

    public Equipment (String id, String pic, String grp, String ind) {
        this.id = id;
        this.picture = pic;
        this.groupIcon = grp;
        this.individualIcon = ind;
        this.armorThickness = 0; // All equipment has to have an armor value.  Most has none, so it's set this way until another constructor changes that.
        this.ceiling = 0; // All equipment has to have a ceiling value.  Most are on the ground, so they have a value of 0.
    }

    public List<String> getStatistics() {
        List<String> statistics = new ArrayList<>();
        return statistics;
    }
}

