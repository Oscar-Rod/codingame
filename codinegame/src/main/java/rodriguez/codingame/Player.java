package rodriguez.codingame;

import java.util.*;

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

    GameEngine(){};

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

    public void move(){
        // System.out.println("WAIT");
        List myFactoriesInDanger = getMyFactoriesInDanger();
        enemyFactoriesAttacked.clear();

        String completeMove = "";
        for (Factory myFactory : factories.getMyFactories()) {
            if (myFactoriesInDanger.contains(myFactory.getId())) {
                continue;
            }
            completeMove = upgradeMyFactory(myFactory, completeMove);
            completeMove = attackEnemyFactories(myFactory, completeMove);
            completeMove = attackNeutralFactories(myFactory, completeMove);
            //if (!bombSent) completeMove = sendBomb(myFactory, completeMove);
        }

        if (!completeMove.isEmpty()) System.out.println(completeMove);
        else System.out.println("WAIT");
    }

    private String upgradeMyFactory(Factory myFactory, String completeMove) {
        if (myFactory.getCyborgs() - getEnemyReinforcements(myFactory) > 10) {
            completeMove = addAction(completeMove, "INC " + myFactory.getId());
            myFactory.setCyborgs(myFactory.getCyborgs() - 10);
        }
        return completeMove;
    }

    private List getMyFactoriesInDanger() {
        List<Integer> myFactoriesInDanger = new ArrayList<>();
        for (Factory myFactory : factories.getMyFactories()){
            if (myFactory.getCyborgs() - getEnemyReinforcements(myFactory) < 0) {
                myFactoriesInDanger.add(myFactory.getId());
            }
        }
        return myFactoriesInDanger;
    }

    private String attackNeutralFactories(Factory myFactory, String completeMove) {
        for (Factory neutralFactory : factories.getNeutralFactories()) {
            for (int i = 3; i > 0; i--) {
                if (neutralFactory.getProduction() == i) {
                    int enemyTroops = neutralFactory.getCyborgs();
                    int neededTroops = enemyTroops - calculateReinforcements(neutralFactory) + 1;
                    int availableTroops = getAvailableTroops(myFactory);
                    if (availableTroops > neededTroops && neededTroops > 0) {
                        myFactory.setCyborgs(myFactory.getCyborgs() - neededTroops);
                        completeMove = addAction(completeMove, "MOVE " + myFactory.getId() + " " + neutralFactory.getId() + " " + neededTroops);
                    }
                }
            }
        }
        return completeMove;
    }

    private String attackEnemyFactories(Factory myFactory, String completeMove) {
        for (Factory enemyFactory : factories.getEnemyFactories()) {
            if (enemyFactoriesAttacked.contains(enemyFactory.getId())) continue;
            int distance = map.getDistance(myFactory.getId(), enemyFactory.getId());
            for (int i = 3; i > 0; i--) {
                if (enemyFactory.getProduction() == i) {
                    int neededTroops = getNeededTroops(enemyFactory, distance);
                    int availableTroops = getAvailableTroops(myFactory);
                    if (availableTroops > neededTroops && neededTroops > 0) {
                        myFactory.setCyborgs(myFactory.getCyborgs() - neededTroops);
                        completeMove = addAction(completeMove, "MOVE " + myFactory.getId() + " " + enemyFactory.getId() + " " + neededTroops);
                        enemyFactoriesAttacked.add(enemyFactory.getId());
                    }
                }
            }
        }
        return completeMove;
    }

    private int getAvailableTroops(Factory myFactory) {
        return myFactory.getCyborgs() - getEnemyReinforcements(myFactory);
    }

    private int getNeededTroops(Factory enemyFactory, int distance) {
        int currentEnemyTroops = enemyFactory.getCyborgs();
        int production = enemyFactory.getProduction();
        int predictedEnemyTroops = currentEnemyTroops + 1 + distance * production;
        return predictedEnemyTroops - calculateReinforcements(enemyFactory) + 1;
    }

//    private String sendBomb(Factory myFactory, String completeMove) {
//        if (enemyFactory.getCyborgs() >= 10 && enemyFactory.getProduction() > 1 && distance <10) {
//                completeMove = addAction(completeMove, "BOMB " + myFactory.getId() + " " + enemyFactory.getId());
//        }
//    }

    private int calculateReinforcements(Factory target){
        int reinforcements = 0;
        reinforcements -= getEnemyReinforcements(target);
        reinforcements += getMyReinforcements(target);
        return reinforcements;
    }

    private int getMyReinforcements(Factory target) {
        int reinforcements = 0;
        for (Troop troop : troops.getMyTroops()){
            if (troop.getTarget() == target.getId()) reinforcements += troop.getCyborgs();
        }
        return reinforcements;
    }

    private int getEnemyReinforcements(Factory target) {
        int reinforcements = 0;
        for (Troop troop : troops.getEnemyTroops()){
            if (troop.getTarget() == target.getId()) reinforcements += troop.getCyborgs();
        }
        return reinforcements;
    }

    private String addAction(String completeTurn, String nextAction) {
        if (completeTurn.isEmpty()) completeTurn = nextAction;
        else {
            completeTurn = completeTurn + ";" + nextAction;
        }
        return completeTurn;
    }
}


class FactoriesMap {
    private int[][] factories;

    public FactoriesMap(){};

    public void setMapSize(int numberOfFactories){
        factories = new int[numberOfFactories][numberOfFactories];
    }

    public void add(int factory1, int factory2, int distance){
        factories[factory1][factory2] = distance;
        factories[factory2][factory1] = distance;
    }

    public int getDistance(int factory1, int factory2){
        return factories[factory1][factory2];
    }
}

class FactoriesList {
    List myFactories = new ArrayList<Factory>();
    List enemyFactories = new ArrayList<Factory>();
    List neutralFactories = new ArrayList<Factory>();

    public FactoriesList(){}

    public void setFactory(Factory factory){
        if (factory.getOwner() == 1) myFactories.add(factory);
        else if (factory.getOwner() == -1) enemyFactories.add(factory);
        else neutralFactories.add(factory);
    }

    public void resetList(){
        myFactories.clear();
        neutralFactories.clear();
        enemyFactories.clear();
    }

    public List<Factory> getMyFactories(){
        return myFactories;
    }

    public List<Factory> getEnemyFactories(){
        return enemyFactories;
    }

    public void setEnemyFactories(List<Factory> newEnemyFactories){
        enemyFactories = newEnemyFactories;
    }

    public List<Factory> getNeutralFactories(){
        return neutralFactories;
    }

}

class TroopsList {
    List enemyTroops = new ArrayList<Troop>();
    List myTroops = new ArrayList<Troop>();

    public TroopsList(){}

    public void resetList(){
        enemyTroops.clear();
        myTroops.clear();
    }

    public void setTroop(Troop troop){
        if (troop.getOwner() == 1) myTroops.add(troop);
        else enemyTroops.add(troop);
    }

    public List<Troop> getEnemyTroops(){
        return enemyTroops;
    }

    public List<Troop> getMyTroops(){
        return myTroops;
    }
}

class BombsList {
    List enemyBombs = new ArrayList<Bomb>();
    List myBombs = new ArrayList<Bomb>();

    public BombsList(){}

    public void resetList(){
        enemyBombs.clear();
        myBombs.clear();
    }

    public void setBomb(Bomb bomb){
        if (bomb.getOwner() == 1) myBombs.add(bomb);
        else enemyBombs.add(bomb);
    }

    public List<Bomb> getEnemyBombs(){
        return enemyBombs;
    }

    public List<Bomb> getMyBombs(){
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

    public Factory(){};

    public void setId(int id){
        this.id = id;
    }

    public void setOwner(int owner){
        this.owner = owner;
    }

    public void setCyborgs(int cyborgs){
        this.cyborgs = cyborgs;
    }

    public void setProduction(int production){
        this.production = production;
    }

    public void setDelay(int delay){
        this.delay = delay;
    }

    public int getId(){
        return id;
    }

    public int getOwner(){
        return owner;
    }

    public int getCyborgs(){
        return cyborgs;
    }

    public int getProduction(){
        return production;
    }

    public int getDelay(){
        return delay;
    }

}

class Troop {
    private int owner;
    private int cyborgs;
    private int origin;
    private int target;
    private int timeToObjective;

    public Troop(){};

    public void setOwner(int owner){
        this.owner = owner;
    }

    public void setCyborgs(int cyborgs){
        this.cyborgs = cyborgs;
    }

    public void setOrigin(int origin){
        this.origin = origin;
    }

    public void setTarget(int target){
        this.target = target;
    }

    public void setTimeToObjective(int timeToObjective){
        this.timeToObjective = timeToObjective;
    }

    public int getOwner(){
        return owner;
    }

    public int getCyborgs(){
        return cyborgs;
    }

    public int getOrigin(){
        return origin;
    }

    public int getTarget(){
        return target;
    }

    public int getTimeToObjective(){
        return timeToObjective;
    }
}

class Bomb {

    private int owner;
    private int origin;
    private int target;
    private int timeToObjective;

    public Bomb() {};

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