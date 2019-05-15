package rodriguez.codingame;

public class Auxiliary {

    final FactoriesList factories = new FactoriesList();
    final TroopsList troops = new TroopsList();
    GameEngine engine;

    public GameEngine engineForTesting() {

        final FactoriesMap map = new FactoriesMap();
        final BombsList bombs = new BombsList();
        engine = new GameEngine();
        int factoryCount = 3; // the number of factories
        int linkCount = 3; // the number of links between factories
        map.setMapSize(factoryCount);
        for (int i = 0; i < linkCount; i++) {
            int factory1 = i;
            int factory2 = i + 1;
            if (factory2 == 3) factory2 = 2;
            int distance = 10;
            map.add(factory1, factory2, distance);
        }
        engine.setFactories(factories);
        engine.setMap(map);
        engine.setTroops(troops);
        engine.setBombs(bombs);

        return engine;
    }

    public void setDistance(Factory factory1, Factory factory2, int distance) {
        engine.map.add(factory1.getId(), factory2.getId(), distance);
    }

    public void setFactory(int id, int owner, int cyborg, int production, int delay){
        Factory factory = new Factory();
        factory.setId(id);
        factory.setOwner(owner);
        factory.setCyborgs(cyborg);
        factory.setProduction(production);
        factory.setDelay(delay);
        factories.setFactory(factory);
    }

    public void setTroop(int owner, int origin, int target, int cyborgs, int timeToObjective){
        Troop troop = new Troop();
        troop.setOwner(owner);
        troop.setOrigin(origin);
        troop.setTarget(target);
        troop.setCyborgs(cyborgs);
        troop.setTimeToObjective(timeToObjective);
        troops.setTroop(troop);
    }

}
