package rodriguez.codingame;

import java.util.*;
import java.util.stream.Collectors;

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

            long startTime = System.currentTimeMillis();

            troops.resetList();
            factories.resetList();
            bombs.resetTemporaryBombList();
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
                    bombs.addBomb(entityId, arg1, arg2, arg3, arg4);
                    if (arg1 == 1) {
                        factories.getAllMyFactories().stream().filter(f -> f.getId() == arg3).findFirst().ifPresent(Factory::setAsBeingBobardedByMe);
                        factories.getAllEnemyFactories().stream().filter(f -> f.getId() == arg3).findFirst().ifPresent(Factory::setAsBeingBobardedByMe);
                    }
                }
            }
            bombs.updateListOfBombs();

            long midTime = System.currentTimeMillis();

            engine.move();

            long endTime = System.currentTimeMillis();

            System.err.println("TOTAL TIME " + (endTime - startTime));
            System.err.println("PREPROCESSING TIME " + (midTime - startTime));
            // To debug: System.err.println("Debug messages...");

        }
    }

}

class GameEngine {
    FactoriesMap map;
    FactoriesList factories;
    TroopsList troops;
    BombsList bombs;
    List<String> actions = new ArrayList<>();
    List[] myFactoriesInDanger = new ArrayList[4];
    int remainingNumberOfBombs = 2;


    GameEngine() {
        myFactoriesInDanger[0] = new ArrayList<Factory>();
        myFactoriesInDanger[1] = new ArrayList<Factory>();
        myFactoriesInDanger[2] = new ArrayList<Factory>();
        myFactoriesInDanger[3] = new ArrayList<Factory>();
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
        actions.clear();
        myFactoriesInDanger[0].clear();
        myFactoriesInDanger[1].clear();
        myFactoriesInDanger[2].clear();
        myFactoriesInDanger[3].clear();

        long startTime = System.currentTimeMillis();

        for (Factory myFactory : factories.getAllMyFactories()) {
            //TODO: Need a system to prioritise attacking nearer factories
            setNumberOfCyborgsToTheMaximumItIsSafeToSpend(myFactory);
            if (myFactory.isInDanger()) continue;
            defendMyFactories(myFactory, 3);
            defendMyFactories(myFactory, 2);
            upgradeMyFactory(myFactory);
            defendMyFactories(myFactory, 1);
            attackAllFactories(myFactory);
//            attackFactories(myFactory, TypesOfFactories.ENEMY, 3);
//            attackFactories(myFactory, TypesOfFactories.ENEMY, 2);
//            attackFactories(myFactory, TypesOfFactories.NEUTRAL, 3);
//            attackFactories(myFactory, TypesOfFactories.NEUTRAL, 2);
//            attackFactories(myFactory, TypesOfFactories.ENEMY, 1);
//            attackFactories(myFactory, TypesOfFactories.NEUTRAL, 1);
            avoidEnemyBomb(myFactory);
            if (remainingNumberOfBombs > 0) {
                sendMyBomb();
            }
        }

        long midTime = System.currentTimeMillis();

        executeActions();

        long endTime = System.currentTimeMillis();

        System.err.println("CALCULATIONS TIME " + (midTime - startTime));
        System.err.println("EXECUTION TIME " + (endTime - midTime));
    }

    private void sendMyBomb() {
        List<Factory> possibleTargets = findPossibleTargets();
        Factory bestTarget = findBestTarget(possibleTargets);
        if (bestTarget != null) {
            Factory closestFactoryToTarget = findMyClosestFactoryToTarget(bestTarget);
            remainingNumberOfBombs--;
            actionBomb(closestFactoryToTarget, bestTarget);
        }
    }

    private List<Factory> findPossibleTargets() {
        List<Factory> possibleTargets = new ArrayList<>();
        for (Factory factory : factories.getEnemyFactoriesOfLevel(3)) {
            if (!factory.isBeingBombardedByMe() && factory.getDelay() == 0)
                possibleTargets.add(factory);
        }
        return possibleTargets;
    }

    private Factory findBestTarget(List<Factory> possibleTargets) {
        int numberOfTroops = Integer.MIN_VALUE;
        Factory bestTarget = null;
        for (Factory factory : possibleTargets) {
            if (factory.getCyborgs() > numberOfTroops) {
                numberOfTroops = factory.getCyborgs();
                bestTarget = factory;
            }
        }
        return bestTarget;
    }

    public void setNumberOfCyborgsToTheMaximumItIsSafeToSpend(Factory myFactory) {
        int[] predictedNumberOfTroopsInMyFactory = predictNumberOfTroopsInTheFactory(myFactory);

        int numberOfTroopsINeedToDefendMyFactory = getNumberOfTroopsINeedToDefendMyFactory(myFactory, predictedNumberOfTroopsInMyFactory);

        if (numberOfTroopsINeedToDefendMyFactory <= 0) { //Factory will be conquered
            int[] numberOfTroopsAndNumberOfTurnsUntilArrival = findFirstReinforcementNeededAndTurnsUntilAttack(predictedNumberOfTroopsInMyFactory);
            myFactory.setAsEndangered();
            myFactory.setNumberOfTroopsIncoming(Math.abs(numberOfTroopsAndNumberOfTurnsUntilArrival[0]));
            myFactory.setNumberOfTurnsUntilArrival(numberOfTroopsAndNumberOfTurnsUntilArrival[1]);
            myFactoriesInDanger[myFactory.getProduction()].add(myFactory);
        } else myFactory.setCyborgs(numberOfTroopsINeedToDefendMyFactory);
    }

    public int getNumberOfTroopsINeedToDefendMyFactory(Factory myFactory, int[] foreseenNumberOfTroopsInTheFactory) {
        int numberOfTroopsINeedToDefendMyFactory = myFactory.getCyborgs();
        for (int i = 0; i < 20; i++) {
            if (foreseenNumberOfTroopsInTheFactory[i] < numberOfTroopsINeedToDefendMyFactory)
                numberOfTroopsINeedToDefendMyFactory = foreseenNumberOfTroopsInTheFactory[i];
            if (numberOfTroopsINeedToDefendMyFactory < 0) break;
        }
        return numberOfTroopsINeedToDefendMyFactory;
    }

    public int[] findFirstReinforcementNeededAndTurnsUntilAttack(int[] predictedNumberOfTroopsInMyFactory) {
        int[] troopsAndTurn = new int[2];
        for (int i = 0; i < 20; i++) {
            if (predictedNumberOfTroopsInMyFactory[i] < 0) {
                troopsAndTurn[0] = predictedNumberOfTroopsInMyFactory[i];
                troopsAndTurn[1] = i + 1;
                break;
            }
        }
        return troopsAndTurn;
    }

    public void defendMyFactories(Factory myFactory, int level) {
        List<Factory> listOfFactoriesInDanger = myFactoriesInDanger[level];
        for (Factory endangeredFactory : listOfFactoriesInDanger) {
            if (endangeredFactory.getNumberOfTroopsIncoming() > myFactory.getCyborgs()) continue;
            if (endangeredFactory.getNumberOfTurnsUntilArrival() < map.getDistance(myFactory.getId(), endangeredFactory.getId()))
                continue;
            actionMove(myFactory, endangeredFactory, endangeredFactory.getNumberOfTroopsIncoming());
        }
    }

    public int[] predictNumberOfTroopsInTheFactory(Factory target) {
        //Positive number means I will have this number of troops in that factory at the given turn.
        //Negative number means enemy will have this number of troops in that factory at the given turn.
        //Index 0 is th next turn
        //TODO: ADD the impact of my bombs to predict more accurately the troops
        //TODO: Save this as a field in the factory itself. Now I am calculating all of this for each of my factories, for all the enemy factories
        int[] myTroopsArrivingEachTurnToTarget = new int[20];
        int[] enemyTroopsArrivingEachTurnToTarget = new int[20];
        for (Troop troop : troops.getEnemyTroops()) {
            if (troop.getTarget() == target.getId()) {
                enemyTroopsArrivingEachTurnToTarget[troop.getTimeToObjective() - 1] -= troop.getCyborgs();
            }
        }

        for (Troop troop : troops.getMyTroops()) {
            if (troop.getTarget() == target.getId()) {
                myTroopsArrivingEachTurnToTarget[troop.getTimeToObjective() - 1] += troop.getCyborgs();
            }
        }
        int[] totalTroopsArrivedUntilEachTurn = new int[20];
        if (target.getOwner() != 0) {
            for (int i = 0; i < 20; i++) {
                int production = target.getProduction();
                if (target.getDelay() != 0) production = 0;
                if (i == 0)
                    totalTroopsArrivedUntilEachTurn[i] = (target.getCyborgs() + production) * target.getOwner() + myTroopsArrivingEachTurnToTarget[i] + enemyTroopsArrivingEachTurnToTarget[i];
                else
                    totalTroopsArrivedUntilEachTurn[i] = production * target.getOwner() + myTroopsArrivingEachTurnToTarget[i] + enemyTroopsArrivingEachTurnToTarget[i] + totalTroopsArrivedUntilEachTurn[i - 1];

                if (totalTroopsArrivedUntilEachTurn[i] != 0) {
                    int signOfTroops = Integer.signum(totalTroopsArrivedUntilEachTurn[i]);
                    if (target.getOwner() != signOfTroops) {
                        target.setOwner(target.getOwner() * -1);
                    }
                }
            }
        } else {
            boolean conquered = false;
            for (int i = 0; i < 20; i++) {
                if (!conquered) {
                    if (i == 0)
                        totalTroopsArrivedUntilEachTurn[i] = (target.getCyborgs() - Math.abs(myTroopsArrivingEachTurnToTarget[i]) - Math.abs(enemyTroopsArrivingEachTurnToTarget[i])) * -1;
                    else
                        totalTroopsArrivedUntilEachTurn[i] = (Math.abs(totalTroopsArrivedUntilEachTurn[i - 1]) - Math.abs(myTroopsArrivingEachTurnToTarget[i]) - Math.abs(enemyTroopsArrivingEachTurnToTarget[i])) * -1;

                    if (totalTroopsArrivedUntilEachTurn[i] > 0) {
                        int newOwner = Math.abs(myTroopsArrivingEachTurnToTarget[i]) > Math.abs(enemyTroopsArrivingEachTurnToTarget[i]) ? 1 : -1;
                        totalTroopsArrivedUntilEachTurn[i] = totalTroopsArrivedUntilEachTurn[i] * newOwner;
                        target.setOwner(newOwner);
                        conquered = true;

                    }
                } else {
                    totalTroopsArrivedUntilEachTurn[i] = target.getProduction() * target.getOwner() + myTroopsArrivingEachTurnToTarget[i] + enemyTroopsArrivingEachTurnToTarget[i] + totalTroopsArrivedUntilEachTurn[i - 1];
                    int signOfTroops = Integer.signum(totalTroopsArrivedUntilEachTurn[i]);
                    if (target.getOwner() != signOfTroops) {
                        target.setOwner(target.getOwner() * -1);
                    }
                }
            }
        }

        return totalTroopsArrivedUntilEachTurn;
    }

    public void upgradeMyFactory(Factory myFactory) {
        if (myFactory.getProduction() < 3 && myFactory.getCyborgs() >= 10) {
            actionUpgrade(myFactory);
        }
    }

    public void attackFactories(Factory myFactory, TypesOfFactories typeOfTarget, int level) {
        List<MovementAction> possibleMovements = new ArrayList<>();
        List<Factory> possibleTargets = typeOfTarget == TypesOfFactories.ENEMY ? factories.getEnemyFactoriesOfLevel(level) : factories.getNeutralFactoriesOfLevel(level);
        for (Factory target : possibleTargets) {
            if (!target.isBeingAttackedByMe()) {
                MovementAction movementAction = attackFactory(myFactory, target);
                if (movementAction != null) {
                    possibleMovements.add(movementAction);
                }
            }
        }

        List<MovementAction> orderedMovements = possibleMovements.stream().sorted((mv1, mv2) -> mv1.getPriority().compareTo(mv2.getPriority())).collect(Collectors.toList());
        addMovements(orderedMovements);
    }

    public void attackAllFactories(Factory myFactory) {
        List<MovementAction> possibleMovements = new ArrayList<>();
        List<Factory> possibleTargets = factories.getAllEnemyFactories();
        possibleTargets.addAll(factories.getAllNeutralFactories());
        for (Factory target : possibleTargets) {
            if (!target.isBeingAttackedByMe()) {
                MovementAction movementAction = attackFactory(myFactory, target);
                if (movementAction != null) {
                    possibleMovements.add(movementAction);
                }
            }
        }

        List<MovementAction> orderedMovements = possibleMovements.stream().sorted((mv1, mv2) -> mv2.getPriority().compareTo(mv1.getPriority())).collect(Collectors.toList());
        addMovements(orderedMovements);
    }

    private void addMovements(List<MovementAction> orderedMovements) {
        for (MovementAction movement : orderedMovements) {
            addMovement(movement);
        }
    }

    private void addMovement(MovementAction movement) {
        int myTroops = movement.getMyFactory().getCyborgs();
        int troopsNeeded = movement.getTroopsNeeded();
        if (myTroops >= troopsNeeded) {
            actionMove(movement.getMyFactory(), movement.getTarget(), movement.getTroopsNeeded());
            movement.getTarget().setAsIAmAttackingIt();
        }
    }

    public MovementAction attackFactory(Factory myFactory, Factory target) {
        int numberOfTroopsINeedToConquerTheFactory = getNumberOfTroopsINeedToConquerTheFactory(myFactory, target);
        if (numberOfTroopsINeedToConquerTheFactory == 0) return null; //Factory is already being conquered

        if (numberOfTroopsINeedToConquerTheFactory > myFactory.getCyborgs()) return null;

        return new MovementAction(myFactory, target, numberOfTroopsINeedToConquerTheFactory, map.getDistance(myFactory.getId(), target.getId()));
    }

    public int getNumberOfTroopsINeedToConquerTheFactory(Factory myFactory, Factory target) {
        int numberOfTroopsINeedToConquerTheFactory = Integer.MIN_VALUE;
        int distanceBetweenFactories = map.getDistance(myFactory.getId(), target.getId());
        int[] foreseenNumberOfTroopsInTheFactory = predictNumberOfTroopsInTheFactory(target);
        for (int i = distanceBetweenFactories; i < 20; i++) {
            if (numberOfTroopsINeedToConquerTheFactory > 0 && foreseenNumberOfTroopsInTheFactory[i] < 0)
                numberOfTroopsINeedToConquerTheFactory = Integer.MIN_VALUE;
            if (foreseenNumberOfTroopsInTheFactory[i] > numberOfTroopsINeedToConquerTheFactory)
                numberOfTroopsINeedToConquerTheFactory = foreseenNumberOfTroopsInTheFactory[i];
        }
        if (numberOfTroopsINeedToConquerTheFactory > 0) return 0;
        return Math.abs(numberOfTroopsINeedToConquerTheFactory) + 1;
    }

    public void avoidEnemyBomb(Factory myFactory) {
        boolean myFactoryIsInDanger = canTheBombHitMyFactory(myFactory);
        if (myFactoryIsInDanger) sendAllTroopsToClosestFactory(myFactory);
    }

    private void sendAllTroopsToClosestFactory(Factory myFactory) {
        Factory closestFactory = findMyClosestFactoryToTarget(myFactory);
        if (closestFactory != null) actionMove(myFactory, closestFactory, myFactory.getCyborgs());
    }

    private Factory findMyClosestFactoryToTarget(Factory destination) {
        int distance = Integer.MAX_VALUE;
        Factory closestFactory = null;
        for (Factory factory : factories.getAllMyFactories()) {
            if (factory.getId() == destination.getId()) continue;
            int newDistance = map.getDistance(destination.getId(), factory.getId());
            if (newDistance < distance) {
                distance = newDistance;
                closestFactory = factory;
            }

        }
        return closestFactory;
    }

    private boolean canTheBombHitMyFactory(Factory myFactory) {
        for (Bomb bomb : bombs.getEnemyBombs()) {
            int distanceTraveledByTheBomb = bomb.getTurnsSinceLaunching();
            if (map.getDistance(myFactory.getId(), bomb.getOrigin()) == distanceTraveledByTheBomb + 1) {
                return true;
            }
        }
        for (Bomb bomb : bombs.getMyBombs()) {
            int distanceTraveledByTheBomb = bomb.getTurnsSinceLaunching();
            if (map.getDistance(myFactory.getId(), bomb.getOrigin()) == distanceTraveledByTheBomb + 1) {
                return true;
            }
        }
        return false;
    }

    public void actionUpgrade(Factory factory) {
        addAction("INC " + factory.getId());
        factory.setCyborgs(factory.getCyborgs() - 10);
    }

    public void actionBomb(Factory origin, Factory target) {
        addAction("BOMB " + origin.getId() + " " + target.getId());
    }

    public void actionMove(Factory origin, Factory target, int numberOfTroops) {
        addAction("MOVE " + origin.getId() + " " + target.getId() + " " + numberOfTroops);
        origin.setCyborgs(origin.getCyborgs() - numberOfTroops);
    }

    public void addAction(String action) {
        actions.add(action);
    }

    public void executeActions() {
        if (actions.isEmpty()) System.out.println("WAIT");
        else {
            StringBuilder nextMove = new StringBuilder(actions.get(0));
            for (int i = 1; i < actions.size(); i++) {
                nextMove.append(";").append(actions.get(i));
            }
            System.out.println(nextMove.toString());
        }

    }
}

enum TypesOfFactories {
    MINE,
    ENEMY,
    NEUTRAL;
}

class MovementAction {
    private int troopsNeeded;
    private int distanceTraveled;
    private int productionOfConqueredFactory;
    private Factory myFactory;
    private Factory target;
    private Integer priority; //The higher, the better is to attack it

    public MovementAction(Factory myFactory, Factory target, int troopsNeeded, int distanceTraveled) {
        this.myFactory = myFactory;
        this.target = target;
        this.troopsNeeded = troopsNeeded;
        this.distanceTraveled = distanceTraveled;
        this.productionOfConqueredFactory = target.getProduction();
        int priority = calculatePriority();
        this.priority = priority;
    }

    private int calculatePriority() {
        if (productionOfConqueredFactory == 0) {
            troopsNeeded = troopsNeeded + 10;
        }
        return productionOfConqueredFactory * 10 - distanceTraveled - troopsNeeded;
    }

    public Integer getPriority() {
        return priority;
    }

    public int getTroopsNeeded() {
        return troopsNeeded;
    }

    public Integer getDistanceTraveled() {
        return distanceTraveled;
    }

    public int getProductionOfConqueredFactory() {
        return productionOfConqueredFactory;
    }

    public Factory getMyFactory() {
        return myFactory;
    }

    public Factory getTarget() {
        return target;
    }
}


class FactoriesMap {
    private int[][] factories;
    private int numberOfFactories;

    public FactoriesMap() {
    }

    public void setMapSize(int numberOfFactories) {
        factories = new int[numberOfFactories][numberOfFactories];
        this.numberOfFactories = numberOfFactories;
    }

    public void add(int factory1, int factory2, int distance) {
        factories[factory1][factory2] = distance;
        factories[factory2][factory1] = distance;
    }

    public int getDistance(int factory1, int factory2) {
        return factories[factory1][factory2];
    }

    public int getNumberOfFactories() {
        return numberOfFactories;
    }
}

class FactoriesList {
    List<List<Factory>> myFactories = new ArrayList<>();
    List<List<Factory>> enemyFactories = new ArrayList<>();
    List<List<Factory>> neutralFactories = new ArrayList<>();
    List<Factory> allMyFactories = new ArrayList<>();
    List<Factory> allEnemyFactories = new ArrayList<>();
    List<Factory> allNeutralFactories = new ArrayList<>();

    public FactoriesList() {
        populateLists();
    }

    private void populateLists() {
        for (int i = 0; i < 4; i++) {
            myFactories.add(new ArrayList<>());
        }
        for (int i = 0; i < 4; i++) {
            enemyFactories.add(new ArrayList<>());
        }
        for (int i = 0; i < 4; i++) {
            neutralFactories.add(new ArrayList<>());
        }
    }

    public void setFactory(Factory factory) {
        if (factory.getOwner() == 1) {
            myFactories.get(factory.getProduction()).add(factory);
            allMyFactories.add(factory);
        } else if (factory.getOwner() == -1) {
            enemyFactories.get(factory.getProduction()).add(factory);
            allEnemyFactories.add(factory);
        } else {
            neutralFactories.get(factory.getProduction()).add(factory);
            allNeutralFactories.add(factory);
        }
    }

    public void resetList() {
        myFactories.clear();
        neutralFactories.clear();
        enemyFactories.clear();
        allMyFactories.clear();
        allEnemyFactories.clear();
        allNeutralFactories.clear();
        populateLists();
    }

    public List<Factory> getMyFactoriesOfLevel(int level) {
        return myFactories.get(level);
    }

    public List<Factory> getEnemyFactoriesOfLevel(int level) {
        return enemyFactories.get(level);
    }

    public List<Factory> getNeutralFactoriesOfLevel(int level) {
        return neutralFactories.get(level);
    }

    public List<Factory> getAllMyFactories() {
        return allMyFactories;
    }

    public List<Factory> getAllEnemyFactories() {
        return allEnemyFactories;
    }

    public List<Factory> getAllNeutralFactories() {
        return allNeutralFactories;
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
    List<Bomb> enemyBombs = new ArrayList<>();
    List<Bomb> myBombs = new ArrayList<>();
    List<Bomb> temporaryBombs = new ArrayList<>();

    public BombsList() {
    }

    public void resetEnemyBombList() {
        enemyBombs.clear();
    }

    public void resetMyBombList() {
        myBombs.clear();
    }

    public void resetTemporaryBombList() {
        temporaryBombs.clear();
    }

    public List<Bomb> getEnemyBombs() {
        return enemyBombs;
    }

    public List<Bomb> getMyBombs() {
        return myBombs;
    }

    public void addBomb(int id, int owner, int origin, int target, int timeToObjective) {
        Bomb bomb = new Bomb();
        bomb.setId(id);
        bomb.setOwner(owner);
        bomb.setOrigin(origin);
        bomb.setTarget(target);
        bomb.setTimeToObjective(timeToObjective);
        bomb.setTurnsSinceLaunching(0);
        temporaryBombs.add(bomb);
    }

    public void updateListOfBombs() {
        List<Bomb> auxiliaryEnemyBombs = new ArrayList<>(enemyBombs);
        List<Bomb> auxiliaryMyBombs = new ArrayList<>(myBombs);
        for (Bomb bomb : auxiliaryEnemyBombs) {
            Optional optionalBomb = temporaryBombs.stream().filter(b -> b.getId() == bomb.getId()).findFirst();
            if (optionalBomb.isPresent()) {
                bomb.setTurnsSinceLaunching(bomb.getTurnsSinceLaunching() + 1);
                temporaryBombs.remove(optionalBomb.get());
            } else enemyBombs.remove(bomb);
        }
        for (Bomb bomb : auxiliaryMyBombs) {
            Optional optionalBomb = temporaryBombs.stream().filter(b -> b.getId() == bomb.getId()).findFirst();
            if (optionalBomb.isPresent()) {
                bomb.setTurnsSinceLaunching(bomb.getTurnsSinceLaunching() + 1);
                temporaryBombs.remove(optionalBomb.get());
            } else myBombs.remove(bomb);
        }
        for (Bomb bomb : temporaryBombs) {
            if (bomb.getOwner() == -1) enemyBombs.add(bomb);
            else myBombs.add(bomb);
        }
    }
}

class Factory {
    private int id;
    private int owner;
    private int cyborgs;
    private int production;
    private int delay;
    private boolean inDanger = false;
    private boolean alreadyBeingAttackedByMe = false;
    private int numberOfTroopsIncoming;
    private int numberOfTurnsUntilArrival;
    private boolean isBeingBombardedByMe = false;

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

    public void setAsEndangered() {
        inDanger = true;
    }

    public boolean isInDanger() {
        return inDanger;
    }

    public void setNumberOfTroopsIncoming(int numberOfTroops) {
        numberOfTroopsIncoming = numberOfTroops;
    }

    public int getNumberOfTroopsIncoming() {
        return numberOfTroopsIncoming;
    }

    public void setNumberOfTurnsUntilArrival(int turns) {
        numberOfTurnsUntilArrival = turns;
    }

    public int getNumberOfTurnsUntilArrival() {
        return numberOfTurnsUntilArrival;
    }

    public void setAsIAmAttackingIt() {
        alreadyBeingAttackedByMe = true;
    }

    public boolean isBeingAttackedByMe() {
        return alreadyBeingAttackedByMe;
    }

    public void setAsBeingBobardedByMe() {
        isBeingBombardedByMe = true;
    }

    public boolean isBeingBombardedByMe() {
        return isBeingBombardedByMe;
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

    private int id;
    private int owner;
    private int origin;
    private int target;
    private int timeToObjective;
    private int turnsSinceLaunching;

    public Bomb() {
    }

    ;

    public void setId(int id) {
        this.id = id;
    }

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

    public void setTurnsSinceLaunching(int turnsSinceLaunching) {
        this.turnsSinceLaunching = turnsSinceLaunching;
    }

    public int getId() {
        return id;
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

    public int getTurnsSinceLaunching() {
        return turnsSinceLaunching;
    }
}