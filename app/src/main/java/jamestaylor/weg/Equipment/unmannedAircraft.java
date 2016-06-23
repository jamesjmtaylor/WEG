package jamestaylor.weg.Equipment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 8/8/2015.
 */
// TODO : complete class
public class unmannedAircraft extends aircraft {
    public Integer endurance;
    public String optics;

    public unmannedAircraft() {
            super();
            this.endurance = 0;
            this.optics = "null";
        }

    public unmannedAircraft(String id, String pic, String grp, String ind,
                            Integer speed, Integer auto, Integer ceiling, Integer endurance,
                            String optics) {
        super(id, pic, grp, ind, speed, auto, ceiling);
            this.endurance = endurance;
            this.optics = optics;
        }

    @Override
    public List<String> getFirerEffects(Equipment target, Integer range){
            return super.getFirerEffects(target, range);
        }

    @Override
    public List<String> getStatistics() {
            List<String> statistics = new ArrayList<>();
            statistics.addAll(super.getStatistics()); // call to the groundWeapon to get its statistics first
            statistics.add("Loiter Time");
            statistics.add(endurance.toString() + " hours");
            statistics.add("Surveillance Optics");
            statistics.add(optics);
            return statistics;
        }

}

