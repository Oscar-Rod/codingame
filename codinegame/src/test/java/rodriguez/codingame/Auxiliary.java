package rodriguez.codingame;

public class Auxiliary {

    public static GameEngine setupTest() {

        final FactoriesMap map = new FactoriesMap();
        final FactoriesList factories = new FactoriesList();
        final TroopsList troops = new TroopsList();
        final BombsList bombs = new BombsList();
        final GameEngine engine = new GameEngine();
        int factoryCount = 2; // the number of factories
        int linkCount = 1; // the number of links between factories
        map.setMapSize(factoryCount);
        for (int i = 0; i < linkCount; i++) {
            int factory1 = 0;
            int factory2 = 1;
            int distance = 10;
            map.add(factory1, factory2, distance);
        }
        engine.setFactories(factories);
        engine.setMap(map);
        engine.setTroops(troops);
        engine.setBombs(bombs);

        troops.resetList();
        factories.resetList();
        bombs.resetList();


        Factory factory = new Factory();
        factory.setId(0);
        factory.setOwner(1);
        factory.setCyborgs(10);
        factory.setProduction(2);
        factory.setDelay(0);
        factories.setFactory(factory);

        Troop troop1 = new Troop();
        troop1.setOwner(1);
        troop1.setOrigin(5);
        troop1.setTarget(0);
        troop1.setCyborgs(4);
        troop1.setTimeToObjective(2);
        troops.setTroop(troop1);

        Troop troop2 = new Troop();
        troop2.setOwner(-1);
        troop2.setOrigin(5);
        troop2.setTarget(0);
        troop2.setCyborgs(15);
        troop2.setTimeToObjective(3);
        troops.setTroop(troop2);

        Troop troop3 = new Troop();
        troop3.setOwner(-1);
        troop3.setOrigin(5);
        troop3.setTarget(0);
        troop3.setCyborgs(10);
        troop3.setTimeToObjective(5);
        troops.setTroop(troop2);

        return engine;
    }
}
