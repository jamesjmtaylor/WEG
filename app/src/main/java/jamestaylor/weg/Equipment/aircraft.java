package jamestaylor.weg.Equipment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 8/8/2015.
 */
public class aircraft extends Equipment {

    public Integer speed;
    public Integer autonomy;

    public aircraft() {
        super();
        this.speed = 0;
        this.autonomy = 0;
    }


    public aircraft(String id, String pic, String grp, String ind,
                        Integer s, Integer a, Integer c) {
        super(id, pic, grp, ind);
        this.speed = s;
        this.autonomy = a;
        this.ceiling = c;
    }

    @Override
    public List<String> getFirerEffects(Equipment target, Integer range) {
        return super.getFirerEffects(target, range);
    }

    @Override
    public List<String> getStatistics() {
        List<String> statistics = new ArrayList<>();
        // Updates the displayed range as well as the effects bars for each weapon system

        statistics.add("Maximum Speed");
        statistics.add(speed.toString() + " kph");
        statistics.add("Autonomy");
        statistics.add(autonomy.toString() + " km");
        statistics.add("Ceiling");
        statistics.add(updateRange(ceiling));
        return statistics;
    }

    private String updateRange(Integer range) {
        if (range > 8000) {
            //return (Math.round(((double)range / 1000)) + " kilometers");
            return (range / 1000) + " kilometers";
        } else {
            return range + " meters";
        }
    }
}