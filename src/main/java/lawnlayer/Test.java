package lawnlayer;

public class Test {

    private static void ascendingSort(TileList bounds, String xy) {

        int i = 0;
        while (i < bounds.size()) {

            Tile largest = bounds.get(i);

            for (int j = i; j < bounds.size(); j++) {

                Tile current = bounds.get(j);

                if ((xy.equals("X") &&
                    current.getX() > largest.getX()) ||
                    (xy.equals("Y") &&
                    current.getY() > largest.getY()))

                    largest = current;
            }
            bounds.remove(largest);
            bounds.add(0, largest);
            i++;
        }
    }

    private static void descendingSort(TileList bounds, String xy) {

        int i = 0;
        while (i < bounds.size()) {

            Tile largest = bounds.get(0);

            for (int j = 0; j < (bounds.size() - i); j++) {

                Tile current = bounds.get(j);

                if ((xy.equals("X") &&
                    current.getX() > largest.getX()) ||
                    (xy.equals("Y") &&
                    current.getY() > largest.getY()))

                    largest = current;
            }
            bounds.remove(largest);
            bounds.add(bounds.size(), largest);
            i++;
        }
    }

    public static void main(String[] args) {
        
        TileList bounds = new TileList();
        // bounds.add(new Tile(4, 0));
        // bounds.add(new Tile(50, 42));
        // bounds.add(new Tile(43, 857));
        // bounds.add(new Tile(109, 3));
        // bounds.add(new Tile(34, 65));
        // bounds.add(new Tile(0, 12));
        // bounds.add(new Tile(7, 87));
        // bounds.add(new Tile(192, 46));
        // bounds.add(new Tile(55, 93));
        // bounds.add(new Tile(5, 1));

        Test.ascendingSort(bounds, "Y");
        System.out.println(bounds);
        Test.descendingSort(bounds, "Y");
        System.out.println(bounds);
    }
}
