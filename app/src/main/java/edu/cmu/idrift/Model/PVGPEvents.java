package edu.cmu.idrift.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PVGPEvents {

    /**
     * An array of sample (dummy) items.
     */
    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    static {

        addItem(new DummyItem("1", "Kick-Off Rallye\n" +
                "Sunday, July 5"));
        addItem(new DummyItem("2", "Blacktie & Tailpipes Gala\n" +
                "Friday, July 10"));
        addItem(new DummyItem("3", "Historics at Pitt Race\n" +
                "Weekend, July 10-12"));
        addItem(new DummyItem("4", "Walnut Street Car Show\n" +
                "Monday, July 13"));
        addItem(new DummyItem("5", "Waterfront Car Cruise\n" +
                "Tuesday, July 14"));
        addItem(new DummyItem("6", "Downtown Parade & Car Display\n" +
                "Wednesday, July 15"));
        addItem(new DummyItem("7", "Tune-Up at Atria’s\n" +
                "Wednesday, July 15"));
        addItem(new DummyItem("8", "Countryside Tour\n" +
                "Thursday, July 16"));
        addItem(new DummyItem("9", "Cars & Guitars at the Hard Rock\n" +
                "Thursday, July 16"));
        addItem(new DummyItem("10", "Schenley Park Vintage Car Show & Races\n" +
                "Weekend, July 18/19"));
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String id;
        public String content;

        public DummyItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
