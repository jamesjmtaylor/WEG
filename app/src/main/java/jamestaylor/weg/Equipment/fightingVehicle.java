package jamestaylor.weg.Equipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by James on 8/5/2015.
 */
public class fightingVehicle extends groundWeapon {
    public Integer speed;
    public Integer autonomy;
    public Integer weight;

    public fightingVehicle() {
        super();
        this.armorThickness = 0;
        this.speed = 0;
        this.autonomy = 0;
        this.weight = 0;
    }

    public fightingVehicle(String id, String pic, String grp, String ind,
                           String pw, Integer pwr, Integer pwp,
                           String aw, Integer awr, Integer awp,
                           String at, Integer atwr, Integer atwp,
                           Integer armor, Integer speed, Integer auto, Integer weight) {
        super(id, pic, grp, ind, pw, pwr, pwp, aw, awr, awp, at, atwr, atwp);
        this.armorThickness = armor;
        this.speed = speed;
        this.autonomy = auto;
        this.weight = weight;
    }

    @Override
    public List<String> getFirerEffects(Equipment target, Integer range){
        return super.getFirerEffects(target, range);
    }

    @Override
    public List<String> getStatistics() {
        List<String> statistics = new ArrayList<>();
        statistics.addAll(super.getStatistics()); // call to the groundWeapon to get its statistics first
        statistics.add("Vehicle Armor Thickness");
        statistics.add(armorThickness.toString() + " mm thick");
        statistics.add("Vehicle Maximum Speed");
        statistics.add(speed.toString() + " kph");
        statistics.add("Vehicle Autonomy");
        statistics.add(autonomy.toString() + " km");
        statistics.add("Vehicle Weight");
        statistics.add(weight.toString() + " mt");

        return statistics;
    }

}