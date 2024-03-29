package comp1110.ass2.board;

import java.util.*;

import static comp1110.ass2.board.Player.getChainedOccupier;

public class Island {
    /**
     * arrange each island with a distinct number
     */
    public int islandNum;
    public int bonus;
    public Coordinate[] islandCor;

    public Island(int islandNum, Coordinate[] islandCor) {
        this.islandNum = islandNum;

        this.islandCor = islandCor;
    }

    public Island() {
        this.islandNum = 0;
        this.bonus = 0;
        this.islandCor = new Coordinate[0];
    }

    public int getIslandNum() {
        return islandNum;
    }

    public int getBonus() {
        return bonus;
    }

    @Override
    public String toString() {
        return "Island{" +
                "islandNum=" + islandNum +
                ", islandCor=" + Arrays.toString(islandCor) +
                '}';
    }

    /**
     *
     * @param islandString eg.  "i 6 7,12 8,11 9,11 9,12 10,10 10,11 11,10 11,11 11,12 12,10 12,11;"
     *
     * @return to an island with arranged number
     */

    public static Island getIslandFromString (String islandString, int islandNum) {
        Island island = new Island();
        island.islandNum = islandNum;

        String[] coordinates = islandString.split(" ");
        island.bonus = Integer.parseInt(coordinates[1]);
        Coordinate[] islandCords = new Coordinate[coordinates.length-2];
        for (int i = 0; i < islandCords.length; i++){
            int islandLocate = i + 2;
            islandCords[i] = new Coordinate(coordinates[islandLocate]);
        }
        island.islandCor = islandCords;
        return island;
    }

    /**
     * get a list of islands from a gameState
     * @param gameState same as stateString in BlueLagoon.java
     * @return A list of islands
     */

    public static List<Island> getIslands (String gameState){
        String[] gameStates = gameState.split("; ");
        List<String> islandStrings = new ArrayList<>();
        for (String state : gameStates){
            if (state.startsWith("i")) {
                islandStrings.add(state);
            }
        }
        List<Island> islands = new ArrayList<>();
        for (int i = 0; i < islandStrings.size(); i++){
            int islandNum = i + 1;
            Island island = getIslandFromString(islandStrings.get(i),islandNum);
            islands.add(island);
        }
        return islands;
    }

    /**
     *  get the island number where a coordinate is in
     * @param cor the Coordinate to be detected
     * @param islands extracted from stateString
     * @return islandNum of the island cor is belong to
     *          0 if cor does not belong to any islands
     */

    public static int getIslandNumber (Coordinate cor, List<Island> islands){
        int output = 0;
        for (Island island : islands){
            List<Coordinate> cors = Arrays.asList(island.islandCor);
            if (cors.contains(cor)){
                output = island.islandNum;
                break;
            }
        }
        //System.out.println(cor.toString()+" is on island " + output);
        return output;
    }

    /**
     * get the total numbers of settlers and villages
     * @param cors coordinates of settlers and villages cmobined (1 player)
     * @param islands lists of islands given by a gameState
     * @return array of int
     * ex. [3,4,7,0]
     * a player has 3 occupants on the first island
     *              4 occupants on the second island
     *              7 occupants on the third island
     *              ...
     */
    public static int[] getOccupiedNumbers (List<Coordinate> cors, List<Island> islands){
        int[] output = new int[islands.size()];
        for (Coordinate cor : cors){
            // this islandNum can be 0
            int islandNum = getIslandNumber(cor,islands);
            if (islandNum != 0){
                output[islandNum-1] += 1;
            }
        }
        return output;
    }

    /**
     * given list of settlers and villages,
     * @param cors list of settlers and villages(combined)
     * @param islands list of islands given by gameState
     * @return to a hashset of intergers, represents island numbers occupied,
     * no duplicates or 0s permitted
     */
    public static HashSet<Integer> getOccupiedIslands (List<Coordinate> cors, List<Island> islands){
        HashSet<Integer> islandsOccupied = new HashSet<>();
        for (Coordinate cor : cors){
            int islandNum = getIslandNumber(cor,islands);
            islandsOccupied.add(islandNum);
        }
        // islands with islandNum 0 are not island, so we remove them
        islandsOccupied.remove(0);
        return islandsOccupied;

    }

    /**
     *  a helper method for totalIslandScore in task11
     * @param cors a list of Coordinates which can be gotten from player's settlers and villages
     * @param islands the list of islands given by the stateString
     * @return the score given the number of islands occupied
     */

    public static int getIslandScore (List<Coordinate> cors, List<Island> islands){
        HashSet<Integer> islandsOccupied = getOccupiedIslands(cors,islands);
        int output;
        //somehow when there are more than 8 islands, the scores remains 20
        switch (islandsOccupied.size()){
            case 8,9,10,11 -> output = 20;
            case 7 -> output = 10;
            default -> output = 0;
        }
        //System.out.println(islandsOccupied.size() + " islands with score " + output);
        return output;
    }

    /**
     * helper method for task11 part2
     * @param cors a list of Coordinates which can be gotten from player's settlers and villages
     * @param islands the list of islands given by the stateString
     * @return the score calculated by the maximun number of chained occupiers
     */
    public static int getLinkedScore(List<Coordinate> cors, List<Island> islands){
        // a list of lists of Coordinates of a player's occupiers that are chained
        List<List<Coordinate>> separated =  getChainedOccupier(new ArrayList<>(),cors);
        //System.out.println(separated);
        List<Integer> scores = new ArrayList<>();
        // for each separated linked coordinates, score them in terms of islands occupied
        for (List<Coordinate> each : separated){
            //System.out.println("the player has occupied "+each);
            HashSet<Integer> islandsOccupied = getOccupiedIslands(each,islands);
            //System.out.println("has linked island "+islandsOccupied.size());
            scores.add(5*islandsOccupied.size());
        }
        if (scores.size()==0){
            return 0;
        }
        return Collections.max(scores);
    }



    public static final String DEFAULT_GAME = "a 13 2; c 0 E; i 6 0,0 0,1 0,2 0,3 1,0 1,1 1,2 1,3 1,4 2,0 2,1; i 6 0,5 0,6 0,7 1,6 1,7 1,8 2,6 2,7 2,8 3,7 3,8; i 6 7,12 8,11 9,11 9,12 10,10 10,11 11,10 11,11 11,12 12,10 12,11; i 8 0,9 0,10 0,11 1,10 1,11 1,12 2,10 2,11 3,10 3,11 3,12 4,10 4,11 5,11 5,12; i 8 4,0 5,0 5,1 6,0 6,1 7,0 7,1 7,2 8,0 8,1 8,2 9,0 9,1 9,2; i 8 10,3 10,4 11,0 11,1 11,2 11,3 11,4 11,5 12,0 12,1 12,2 12,3 12,4 12,5; i 10 3,3 3,4 3,5 4,2 4,3 4,4 4,5 5,3 5,4 5,5 5,6 6,3 6,4 6,5 6,6 7,4 7,5 7,6 8,4 8,5; i 10 5,8 5,9 6,8 6,9 7,8 7,9 7,10 8,7 8,8 8,9 9,7 9,8 9,9 10,6 10,7 10,8 11,7 11,8 12,7 12,8; s 0,0 0,5 0,9 1,4 1,8 1,12 2,1 3,5 3,7 3,10 3,12 4,0 4,2 5,9 5,11 6,3 6,6 7,0 7,8 7,12 8,2 8,5 9,0 9,9 10,3 10,6 10,10 11,0 11,5 12,2 12,8 12,11; r C B W P S; " +
            "p 0 0 0 0 0 0 0 S T; p 1 42 1 2 3 4 5 S 5,6 8,7 T 1,2;";
    public static final String WHEELS_GAME = "a 13 2; c 0 E; i 5 0,1 0,2 0,3 0,4 1,1 1,5 2,0 2,5 3,0 3,6 4,0 4,5 5,1 5,5 6,1 6,2 6,3 6,4; i 5 0,8 0,9 0,10 1,8 1,11 2,7 2,11 3,8 3,11 4,8 4,9 4,10; i 7 8,8 8,9 8,10 9,8 9,11 10,7 10,11 11,8 11,11 12,8 12,9 12,10; i 7 10,0 10,1 10,4 10,5 11,0 11,2 11,3 11,4 11,6 12,0 12,1 12,4 12,5; i 9 2,2 2,3 3,2 3,4 4,2 4,3; i 9 2,9; i 9 6,6 6,7 6,8 6,9 6,10 6,11 7,6 8,0 8,1 8,2 8,3 8,4 8,5; i 9 10,9; s 0,1 0,4 0,10 2,2 2,3 2,9 2,11 3,0 3,2 3,4 3,6 4,2 4,3 4,10 6,1 6,4 6,6 6,11 8,0 8,5 8,8 8,10 10,0 10,5 10,7 10,9 10,11 11,3 12,1 12,4 12,8 12,10; r C B W P S; " +
            "p 0 0 0 0 0 0 0 S T; p 1 0 0 0 0 0 0 S T;";
    public static final String FACE_GAME = "a 13 2; c 0 E; i 6 0,0 0,1 0,2 0,3 0,4 0,5 0,6 0,7 0,8 0,9 0,10 0,11 1,0 1,12 2,0 2,11 3,0 3,12 4,0 4,11 5,0 5,12 6,0 6,11 7,0 7,12 8,0 8,11 9,0 9,12 10,0 10,11 11,0 11,12 12,0 12,1 12,2 12,3 12,4 12,5 12,6 12,7 12,8 12,9 12,10 12,11; i 6 2,4 2,5 2,6 2,7; i 9 4,4 4,5 4,6 4,7; i 9 6,5 6,6 7,5 7,7 8,5 8,6; i 12 2,2 3,2 3,3 4,2 5,2 5,3 6,2 7,2 7,3; i 12 2,9 3,9 3,10 4,9 5,9 5,10 6,9 7,9 7,10; i 12 9,2 9,10 10,2 10,3 10,4 10,5 10,6 10,7 10,8 10,9; s 0,3 0,8 1,0 1,12 2,2 2,4 2,7 2,9 4,2 4,5 4,6 4,9 5,0 5,12 6,2 6,5 6,6 6,9 8,0 8,5 8,6 8,11 9,2 9,10 10,3 10,5 10,6 10,8 11,0 11,12 12,4 12,7; r C B W P S; " +
            "p 0 0 0 0 0 0 0 S T; p 1 0 0 0 0 0 0 S T;";
    public static final String SIDES_GAME =  "a 7 2; c 0 E; i 4 0,0 0,1 0,2 0,3 1,0 1,1 1,2 1,3 2,0 2,1 2,2 2,3 3,0 3,1 3,2 3,3 4,0 4,1 4,2 4,3 5,0 5,1 5,2 5,3 6,0 6,1 6,2 6,3; i 20 0,5 1,5 1,6 2,5 3,5 3,6 4,5 5,5 5,6 6,5; s 0,0 0,1 0,2 0,3 1,1 1,2 1,3 1,5 1,6 2,0 2,1 2,2 2,3 3,0 3,1 3,2 3,3 3,5 3,6 4,0 4,1 4,2 4,3 5,1 5,2 5,3 5,5 5,6 6,0 6,1 6,2 6,3; r C B W P S; " +
            "p 0 0 0 0 0 0 0 S T; p 1 0 0 0 0 0 0 S T;";
    public static final String SPACE_INVADERS_GAME = "a 23 2; c 0 E; i 6 0,2 0,7 1,3 1,7 2,2 2,3 2,4 2,5 2,6 2,7 3,2 3,4 3,5 3,6 3,8 4,0 4,1 4,2 4,3 4,4 4,5 4,6 4,7 4,8 4,9 5,0 5,1 5,3 5,4 5,5 5,6 5,7 5,9 5,10 6,0 6,2 6,7 6,9 7,3 7,4 7,6 7,7; i 6 0,14 0,19 1,15 1,19 2,14 2,15 2,16 2,17 2,18 2,19 3,14 3,16 3,17 3,18 3,20 4,12 4,13 4,14 4,15 4,16 4,17 4,18 4,19 4,20 4,21 5,12 5,13 5,15 5,16 5,17 5,18 5,19 5,21 5,22 6,12 6,14 6,19 6,21 7,15 7,16 7,18 7,19; i 6 17,9 18,8 18,9 19,6 19,7 19,8 19,9 19,10 19,11 19,12 20,5 20,6 20,7 20,8 20,9 20,10 20,11 20,12 21,5 21,6 21,7 21,8 21,9 21,10 21,11 21,12 21,13 22,5 22,6 22,7 22,8 22,9 22,10 22,11 22,12; i 8 12,3 12,5 13,3 13,4 13,5 13,6 14,1 14,2 14,3 14,4 14,5 15,1 15,2 15,3 16,1 16,2; i 8 12,17 12,18 12,19 13,17 13,18 13,19 13,20 14,17 14,18 14,19 14,20 15,19 15,20 15,21 16,19 16,20; i 8 13,14 14,13 14,14 15,13 15,14 15,15 16,13 16,14; i 8 14,7 15,7 15,8 16,7; i 10 8,9 9,9 10,9 11,9; i 10 8,12 9,13 10,12 11,13; i 10 9,1 10,1 11,1 12,1; i 10 9,22 10,21 11,22 12,21; i 10 13,10 14,10 15,10; i 10 17,0 18,0 19,0 20,0; i 10 17,16 18,16 19,16 20,16; s 0,2 0,7 0,14 0,19 3,5 3,17 6,0 6,9 6,12 6,21 7,4 7,6 7,16 7,18 11,9 11,13 12,1 12,19 12,21 13,10 15,2 15,8 15,14 15,20 17,9 18,8 18,9 20,0 20,16 21,6 21,9 21,12; r C B W P S; " +
            "p 0 0 0 0 0 0 0 S T; p 1 0 0 0 0 0 0 S T;";


    public static void main(String[] args) {
        List<Island> defaultIsland = getIslands(DEFAULT_GAME);
        for (Island i : defaultIsland){
            System.out.println(i);
        }

    }

}
