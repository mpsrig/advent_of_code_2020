package mpsrig;

import java.util.ArrayList;
import java.util.List;

public abstract class ComputationWithGroups extends Runner.Computation {
    protected final List<List<String>> groups = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
        chunkGroups();
    }

    private void chunkGroups() {
        var current = new ArrayList<String>();
        for (String elem : input) {
            if (elem.equals("")) {
                if (!current.isEmpty()) {
                    groups.add(current);
                    current = new ArrayList<>();
                }
            } else {
                current.add(elem);
            }
        }
        if (!current.isEmpty()) {
            groups.add(current);
        }
    }
}
