package xmldrill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Ilya Boyandin
 */
public class Rankings {
    
    private final ConcurrentHashMap<Key, AtomicInteger> pointsMap =
        new ConcurrentHashMap<Key, AtomicInteger>();

    public List<Entry> getSnapshot() {
        List<Entry> snapshot = new ArrayList<Entry>();
        for (Map.Entry<Key, AtomicInteger> e : pointsMap.entrySet()) {
            snapshot.add(new Entry(e.getKey(), e.getValue().intValue()));
        }
        Collections.sort(snapshot, Entry.COMPARE_EXERCISES_THEN_POINTS);
        return snapshot;
    }

    public List<Entry> getSnapshotFor(String exercise) {
        List<Entry> snapshot = new ArrayList<Entry>();
        for (Map.Entry<Key, AtomicInteger> e : pointsMap.entrySet()) {
            Key key = e.getKey();
            if (key.getExercise().equals(exercise)) {
                snapshot.add(new Entry(key, e.getValue().intValue()));
            }
        }
        Collections.sort(snapshot, Entry.COMPARE_POINTS);
        return snapshot;
    }

    public int getPoints(String exercise, String nick, String ip) {
        //Application();
        Key key = Key.keyFor(exercise, nick, ip);
        AtomicInteger ai = pointsMap.get(key);
        if (ai == null) {
            return 0;
        }
        return ai.intValue();
    }

    public int addPoints(String exercise, String nick, String ip, int points) {
        Key key = Key.keyFor(exercise, nick, ip);
        pointsMap.putIfAbsent(key, new AtomicInteger());

        AtomicInteger ai = pointsMap.get(key);
        return ai.getAndAdd(points);
    }

    private static final class Key {
        private Key(String exercise, String nick, String ip) {
            this.exercise = exercise;
            this.nick = nick;
            this.ip = ip;
        }
        public static Key keyFor(String exercise, String nick, String ip) {
            return new Key(exercise, nick, ip);
        }
        private final String exercise;
        private final String nick;
        private final String ip;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((exercise == null) ? 0 : exercise.hashCode());
            result = prime * result + ((ip == null) ? 0 : ip.hashCode());
            result = prime * result + ((nick == null) ? 0 : nick.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            if (exercise == null) {
                if (other.exercise != null) {
                    return false;
                }
            } else if (!exercise.equals(other.exercise)) {
                return false;
            }
            if (ip == null) {
                if (other.ip != null) {
                    return false;
                }
            } else if (!ip.equals(other.ip)) {
                return false;
            }
            if (nick == null) {
                if (other.nick != null) {
                    return false;
                }
            } else if (!nick.equals(other.nick)) {
                return false;
            }
            return true;
        }
        public String getExercise() {
            return exercise;
        }
        public String getNick() {
            return nick;
        }
        public String getIp() {
            return ip;
        }
        @Override
        public String toString()
        {
            final String TAB = "    ";

            String retValue = "";

            retValue = "Key ( "
                + "exercise = " + this.exercise + TAB
                + "nick = " + this.nick + TAB
                + "ip = " + this.ip + TAB
                + " )";

            return retValue;
        }

    }

    public static class Entry implements Serializable {
        private static final long serialVersionUID = 9167750609586530093L;
        private final Key key;
        private final int points;
        public Entry(Key key, int points) {
            this.key = key;
            this.points = points;
        }
        public String getExercise() {
            return key.getExercise();
        }
        public String getNick() {
            return key.getNick();
        }
        public String getIp() {
            return key.getIp();
        }
        public int getPoints() {
            return points;
        }
        public static Comparator<Entry> COMPARE_POINTS = new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                return -(o1.points - o2.points);  // descending
            }
        };
        public static Comparator<Entry> COMPARE_EXERCISES = new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                return o1.getExercise().compareTo(o2.getExercise());
            }
        };
        public static Comparator<Entry> COMPARE_EXERCISES_THEN_POINTS = new Comparator<Entry>() {
          @Override
            public int compare(Entry o1, Entry o2) {
                int c = COMPARE_EXERCISES.compare(o1, o2);
                if (c == 0) {
                    c = COMPARE_POINTS.compare(o1, o2);
                }
                return c;
            }
        };
        @Override
        public String toString()
        {
            final String TAB = "    ";

            String retValue = "";

            retValue = "Entry ( "
                + "key = " + this.key + TAB
                + "points = " + this.points + TAB
                + " )";

            return retValue;
        }

    }
}
