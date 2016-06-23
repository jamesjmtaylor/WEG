package jamestaylor.weg.Equipment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 8/8/2015.
 */
public class antiAircraftVehicle extends antiAircraft {
    public Integer speed;
    public Integer autonomy;
    public Integer weight;

    public antiAircraftVehicle() {
        super();
        this.armorThickness = 0;
        this.speed = 0;
        this.autonomy = 0;
        this.weight = 0;
    }

    public antiAircraftVehicle(String id, String pic, String grp, String ind,
                               String m, Integer mr, Integer ma, Integer mm,
                               String g, Integer gr, Integer ga, Integer gp,
                               Integer armor, Integer speed, Integer auto, Integer weight) {
        super(id, pic, grp, ind, m, mr, ma, mm, g, gr, ga, gp);
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
