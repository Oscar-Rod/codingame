package rodriguez.codingame2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        final FactoriesMap map = new FactoriesMap();
        final FactoriesList factories = new FactoriesList();
        final TroopsList troops = new TroopsList();
        final BombsList bombs = new BombsList();
        final GameEngine engine = new GameEngine();
        Scanner in = new Scanner(System.in);
        int factoryCount = in.nextInt(); // the number of factories
        int linkCount = in.nextInt(); // the number of links between factories
        map.setMapSize(factoryCount);
        for (int i = 0; i < linkCount; i++) {
            int factory1 = in.nextInt();
            int factory2 = in.nextInt();
            int distance = in.nextInt();
            map.add(factory1, factory2, distance);
        }
        engine.setFactories(factories);
        engine.setMap(map);
        engine.setTroops(troops);
        engine.setBombs(bombs);

        // game loop
        while (true) {
            troops.resetList();
            factories.resetList();
            bombs.resetList();
            int entityCount = in.nextInt(); // the number of entities (e.g. factories and troops)
            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();
                int arg1 = in.nextInt();
                int arg2 = in.nextInt();
                int arg3 = in.nextInt();
                int arg4 = in.nextInt();
                int arg5 = in.nextInt();
                if (entityType.equals("FACTORY")) {
                    Factory factory = new Factory();
                    factory.setId(entityId);
                    factory.setOwner(arg1);
                    factory.setCyborgs(arg2);
                    factory.setProduction(arg3);
                    factory.setDelay(arg4);
                    factories.setFactory(factory);
                } else if (entityType.equals("TROOP")) {
                    Troop troop = new Troop();
                    troop.setOwner(arg1);
                    troop.setOrigin(arg2);
                    troop.setTarget(arg3);
                    troop.setCyborgs(arg4);
                    troop.setTimeToObjective(arg5);
                    troops.setTroop(troop);
                } else if (entityType.equals("BOMB")) {
                    Bomb bomb = new Bomb();
                    bomb.setOwner(arg1);
                    bomb.setOrigin(arg2);
                    bomb.setTarget(arg3);
                    bomb.setTimeToObjective(arg4);
                    bombs.setBomb(bomb);
                }
            }
            engine.move();
            // To debug: System.err.println("Debug messages...");

        }
    }

}

class GameEngine {
    FactoriesMap map;
    FactoriesList factories;
    TroopsList troops;
    BombsList bombs;
    List<Integer> enemyFactoriesAttacked = new ArrayList<>();
    List<String> actions = new ArrayList<>();
    List[] myFactoriesInDanger = new ArrayList[4];


    GameEngine() {
        myFactoriesInDanger[0] = new ArrayList<Integer>();
        myFactoriesInDanger[1] = new ArrayList<Integer>();
        myFactoriesInDanger[2] = new ArrayList<Integer>();
        myFactoriesInDanger[3] = new ArrayList<Integer>();
    }

    public void setMap(FactoriesMap map) {
        this.map = map;
    }

    public void setFactories(FactoriesList factories) {
        this.factories = factories;
    }

    public void setTroops(TroopsList troops) {
        this.troops = troops;
    }

    public void setBombs(BombsList bombs) {
        this.bombs = bombs;
    }

    public void move() {
        enemyFactoriesAttacked.clear();
        actions.clear();
        myFactoriesInDanger[0].clear();
        myFactoriesInDanger[1].clear();
        myFactoriesInDanger[2].clear();
        myFactoriesInDanger[3].clear();
        getMyFactoriesInDanger();

        for (Factory myFactory : factories.getMyFactories()){
            //TODO: Need a system to priorityze attacking nearer factories
            setNumberOfCyborgsToTheMaximumItIsSafeToSpend(myFactory);
            upgradeMyFactory(myFactory);
            attackEnemyFactories(myFactory, 3);
            attackEnemyFactories(myFactory, 2);
            attackNeutralFactories(myFactory, 3);
            attackNeutralFactories(myFactory, 2);
            attackEnemyFactories(myFactory, 1);
            attackNeutralFactories(myFactory, 1);
        }
        executeActions();
    }

    private void setNumberOfCyborgsToTheMaximumItIsSafeToSpend(Factory myFactory) {
        int incomingEnemyTroops = enemyTroopsGoingToFactoryPonderingDistance(myFactory);
        if (incomingEnemyTroops < 0) return;
        if (incomingEnemyTroops > myFactory.getCyborgs()) myFactory.setCyborgs(0);
        else myFactory.setCyborgs(myFactory.getCyborgs() - incomingEnemyTroops);
    }

    private void getMyFactoriesInDanger() {
        for (Factory myFactory : factories.getMyFactories()){
            if (myFactory.getCyborgs() < enemyTroopsGoingToFactoryPonderingDistance(myFactory)) {
                System.err.println("Factory " + myFactory.getId() + " in danger");
                myFactoriesInDanger[myFactory.getProduction()].add(myFactory.getId());
            }
        }
    }

    private int enemyTroopsGoingToFactoryPonderingDistance(Factory target) {
        //TODO: now if 2 troops attack at same time, the danger is ignored. If prod is 3, and next turn 2 troops of 3 each arrive, both are individually ignored, as 3 - 1 * 3 = 0
        //TODO: if 1 troop of 6 attacks, the danger is acknowledged. If prod is 3: 6 - 1 * 3 = 3
        int numberOfTroops = 0;
        for (Troop troop : troops.getEnemyTroops()) {
            if (troop.getTarget() == target.getId()) {
                //System.err.println("troops: " + troop.getCyborgs() + " TTO: " + troop.getTimeToObjective() + " target: " + target.getId());
                int incomingEnemyTroops = (troop.getCyborgs() - troop.getTimeToObjective() * target.getProduction()) < 0 ? 0 : (troop.getCyborgs() - troop.getTimeToObjective() * target.getProduction());
                //System.err.println("troops calculated: " + incomingEnemyTroops);
                numberOfTroops = numberOfTroops + incomingEnemyTroops;
                //System.err.println("Total troops: " + numberOfTroops);
            }
        }
        //System.err.println("Total troops after loop: " + numberOfTroops);
        return numberOfTroops;
    }

    private void upgradeMyFactory(Factory myFactory) {
        if (myFactory.getProduction() < 3 && myFactory.getCyborgs() > 10) {
            actionUpgrade(myFactory);
        }
    }

    private void attackNeutralFactories(Factory myFactory, int level) {
        for (Factory neutralFactory : factories.getNeutralFactories()) {
            if (neutralFactory.getProduction() == level) attackFactory(myFactory, neutralFactory);
        }
    }

    private void attackEnemyFactories(Factory myFactory, int level){
        for (Factory enemyFactory : factories.getEnemyFactories()) {
            if (enemyFactory.getProduction() == level) attackFactory(myFactory, enemyFactory);
        }
    }

    private void attackFactory(Factory myFactory, Factory target){
        //TODO: Need to ponder my troops going, to avoid a stream of troops of 1 unit. If I need 10 troops to conquer, and I have 11 troops of 1 unit going, I am not sending more. But if production is 1, I actually need more
        int myTroopsInTheFactory = myFactory.getCyborgs();
        int enemyTroopsInTheFactory = target.getCyborgs();
        int myTroopsGoingToTheFactory = myTroopsGoingToFactory(target);
        int enemyTroopsGoingToTheFactory = enemyTroopsGoingToFactory(target);
        int enemyTroopsProducedInTheFactory = target.getOwner() == 0 ? 1 : target.getProduction() * (map.getDistance(myFactory.getId(), target.getId()) + 1);

        boolean isNotAlreadyBeingConquered = !(myTroopsGoingToTheFactory - enemyTroopsInTheFactory - enemyTroopsGoingToTheFactory - enemyTroopsProducedInTheFactory > 0);
        boolean canIConquerIt = myTroopsInTheFactory + myTroopsGoingToTheFactory - enemyTroopsInTheFactory - enemyTroopsGoingToTheFactory - enemyTroopsProducedInTheFactory > 0;

        int neededTroopsForConquer = enemyTroopsInTheFactory + enemyTroopsGoingToTheFactory + enemyTroopsProducedInTheFactory - myTroopsGoingToTheFactory;

        if (isNotAlreadyBeingConquered && canIConquerIt && neededTroopsForConquer > 0 && !enemyFactoriesAttacked.contains(target.getId())) {
            actionMove(myFactory, target, neededTroopsForConquer);
            enemyFactoriesAttacked.add(target.getId());
        }

//        System.err.println("FACTORY ORIGIN: " + myFactory.getId() + "------- FACTORY TARGET: " + target.getId());
//        System.err.println("myTroopsInTheFactory " + myTroopsInTheFactory);
//        System.err.println("enemyTroopsInTheFactory " + enemyTroopsInTheFactory);
//        System.err.println("myTroopsGoingToTheFactory " + myTroopsGoingToTheFactory);
//        System.err.println("enemyTroopsGoingToTheFactory " + enemyTroopsGoingToTheFactory);
//        System.err.println("enemyTroopsProducedInTheFactory " + enemyTroopsProducedInTheFactory);
//        System.err.println("isNotAlreadyBeingConquered " + isNotAlreadyBeingConquered);
//        System.err.println("canIConquerIt " + canIConquerIt);
//        System.err.println("neededTroopsForConquer " + neededTroopsForConquer);
    }

    private int enemyTroopsGoingToFactory(Factory target) {
        int numberOfTroops = 0;
        for (Troop troop : troops.getEnemyTroops()) {
            if (troop.getTarget() == target.getId()) {
                numberOfTroops += troop.getCyborgs();
            }
        }
        return numberOfTroops;
    }

    private int myTroopsGoingToFactory(Factory target) {
        int numberOfTroops = 0;
        for (Troop troop : troops.getMyTroops()) {
            if (troop.getTarget() == target.getId()) {
                numberOfTroops += troop.getCyborgs();
            }
        }
        return numberOfTroops;
    }

    private void actionUpgrade(Factory factory){
        addAction("INC " + factory.getId());
        factory.setCyborgs(factory.getCyborgs() - 10);
    }

    private void actionBomb(Factory origin, Factory target){
        addAction("BOMB " + origin.getId() + " " + target.getId());
    }

    private void actionMove(Factory origin, Factory target, int numberOfTroops){
        addAction("MOVE " + origin.getId() + " " + target.getId() + " " + numberOfTroops);
        origin.setCyborgs(origin.getCyborgs() - numberOfTroops);
    }

    private void addAction(String action){
       actions.add(action);
    }

    private void executeActions(){
        if (actions.isEmpty()) System.out.println("WAIT");
        else {
            StringBuilder nextMove = new StringBuilder(actions.get(0));
            for (int i = 1; i < actions.size(); i++){
                nextMove.append(";").append(actions.get(i));
            }
            System.out.println(nextMove.toString());
        }

    }
}


class FactoriesMap {
    private int[][] factories;

    public FactoriesMap() {
    }

    public void setMapSize(int numberOfFactories) {
        factories = new int[numberOfFactories][numberOfFactories];
    }

    public void add(int factory1, int factory2, int distance) {
        factories[factory1][factory2] = distance;
        factories[factory2][factory1] = distance;
    }

    public int getDistance(int factory1, int factory2) {
        return factories[factory1][factory2];
    }
}

class FactoriesList {
    List myFactories = new ArrayList<Factory>();
    List enemyFactories = new ArrayList<Factory>();
    List neutralFactories = new ArrayList<Factory>();

    public FactoriesList() {
    }

    public void setFactory(Factory factory) {
        if (factory.getOwner() == 1) myFactories.add(factory);
        else if (factory.getOwner() == -1) enemyFactories.add(factory);
        else neutralFactories.add(factory);
    }

    public void resetList() {
        myFactories.clear();
        neutralFactories.clear();
        enemyFactories.clear();
    }

    public List<Factory> getMyFactories() {
        return myFactories;
    }

    public List<Factory> getEnemyFactories() {
        return enemyFactories;
    }

    public void setEnemyFactories(List<Factory> newEnemyFactories) {
        enemyFactories = newEnemyFactories;
    }

    public List<Factory> getNeutralFactories() {
        return neutralFactories;
    }

}

class TroopsList {
    List enemyTroops = new ArrayList<Troop>();
    List myTroops = new ArrayList<Troop>();

    public TroopsList() {
    }

    public void resetList() {
        enemyTroops.clear();
        myTroops.clear();
    }

    public void setTroop(Troop troop) {
        if (troop.getOwner() == 1) myTroops.add(troop);
        else enemyTroops.add(troop);
    }

    public List<Troop> getEnemyTroops() {
        return enemyTroops;
    }

    public List<Troop> getMyTroops() {
        return myTroops;
    }
}

class BombsList {
    List enemyBombs = new ArrayList<Bomb>();
    List myBombs = new ArrayList<Bomb>();

    public BombsList() {
    }

    public void resetList() {
        enemyBombs.clear();
        myBombs.clear();
    }

    public void setBomb(Bomb bomb) {
        if (bomb.getOwner() == 1) myBombs.add(bomb);
        else enemyBombs.add(bomb);
    }

    public List<Bomb> getEnemyBombs() {
        return enemyBombs;
    }

    public List<Bomb> getMyBombs() {
        return myBombs;
    }

    public boolean factoryIsOBjective(Factory factory) {
        return true;//Iterate over bombs to find if factory is an objective and dont sent more troops to an objective
    }
}

class Factory {
    private int id;
    private int owner;
    private int cyborgs;
    private int production;
    private int delay;

    public Factory() {
    }

    ;

    public void setId(int id) {
        this.id = id;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public void setCyborgs(int cyborgs) {
        this.cyborgs = cyborgs;
    }

    public void setProduction(int production) {
        this.production = production;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getId() {
        return id;
    }

    public int getOwner() {
        return owner;
    }

    public int getCyborgs() {
        return cyborgs;
    }

    public int getProduction() {
        return production;
    }

    public int getDelay() {
        return delay;
    }

}

class Troop {
    private int owner;
    private int cyborgs;
    private int origin;
    private int target;
    private int timeToObjective;

    public Troop() {
    }

    ;

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public void setCyborgs(int cyborgs) {
        this.cyborgs = cyborgs;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public void setTimeToObjective(int timeToObjective) {
        this.timeToObjective = timeToObjective;
    }

    public int getOwner() {
        return owner;
    }

    public int getCyborgs() {
        return cyborgs;
    }

    public int getOrigin() {
        return origin;
    }

    public int getTarget() {
        return target;
    }

    public int getTimeToObjective() {
        return timeToObjective;
    }
}

class Bomb {

    private int owner;
    private int origin;
    private int target;
    private int timeToObjective;

    public Bomb() {
    }

    ;

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public void setTimeToObjective(int timeToObjective) {
        this.timeToObjective = timeToObjective;
    }

    public int getOwner() {
        return owner;
    }

    public int getOrigin() {
        return origin;
    }

    public int getTarget() {
        return target;
    }

    public int getTimeToObjective() {
        return timeToObjective;
    }
}