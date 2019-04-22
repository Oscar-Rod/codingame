package rodriguez.codingame;


import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerTest {

    public final Auxiliary auxiliary= new Auxiliary();
    public GameEngine engine;

    @Before
    public void setUp(){
        engine = auxiliary.engineForTesting();
    }

    @Test
    public void shouldUpgradeTheFactory() {
        auxiliary.setFactory(0,1,10,2,0);
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        int numberOfCyborgs = myFactory.getCyborgs();
        engine.upgradeMyFactory(myFactory);
        int numberOfCyborgsAfterUpgrade = myFactory.getCyborgs();

        assertThat(numberOfCyborgs).isEqualTo(10);
        assertThat(numberOfCyborgsAfterUpgrade).isEqualTo(0);
    }

    @Test
    public void shouldNOTUpgradeTheFactory() {
        auxiliary.setFactory(0,1,10,2,0);
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        myFactory.setCyborgs(9);
        int numberOfCyborgs = myFactory.getCyborgs();
        engine.upgradeMyFactory(myFactory);
        int numberOfCyborgsAfterUpgrade = myFactory.getCyborgs();

        assertThat(numberOfCyborgs).isEqualTo(9);
        assertThat(numberOfCyborgsAfterUpgrade).isEqualTo(9);
    }

    @Test
    public void shouldCalculateTheCorrectNumberOfTroopsInMyFactory() {
        auxiliary.setFactory(0,1,10,2,0);
        auxiliary.setTroop(-1, 5,0, 10, 5);
        auxiliary.setTroop(-1, 5,0, 15, 3);
        auxiliary.setTroop(1, 5,0, 4, 2);
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        int[] enemiesPerTurn = engine.calculateForeseenNumberOfTroopsInTheFactory(myFactory);
        assertThat(enemiesPerTurn[0]).isEqualTo(12);
        assertThat(enemiesPerTurn[1]).isEqualTo(18);
        assertThat(enemiesPerTurn[2]).isEqualTo(5);
        assertThat(enemiesPerTurn[3]).isEqualTo(7);
        assertThat(enemiesPerTurn[4]).isEqualTo(-1);
        assertThat(enemiesPerTurn[5]).isEqualTo(-3);
        assertThat(enemiesPerTurn[6]).isEqualTo(-5);
    }

    @Test
    public void shouldCalculateTheCorrectNumberOfTroopsInEnemyFactory() {
        auxiliary.setFactory(0,-1,10,2,0);
        auxiliary.setTroop(1, 5,0, 10, 5);
        auxiliary.setTroop(1, 5,0, 15, 3);
        auxiliary.setTroop(-1, 5,0, 4, 2);
        Factory myFactory = engine.factories.getEnemyFactories().stream().filter(f -> f.getOwner() == -1).findFirst().get();
        int[] enemiesPerTurn = engine.calculateForeseenNumberOfTroopsInTheFactory(myFactory);
        assertThat(enemiesPerTurn[0]).isEqualTo(-12);
        assertThat(enemiesPerTurn[1]).isEqualTo(-18);
        assertThat(enemiesPerTurn[2]).isEqualTo(-5);
        assertThat(enemiesPerTurn[3]).isEqualTo(-7);
        assertThat(enemiesPerTurn[4]).isEqualTo(1);
        assertThat(enemiesPerTurn[5]).isEqualTo(3);
        assertThat(enemiesPerTurn[6]).isEqualTo(5);
    }

    @Test
    public void shouldCalculateTheCorrectNumberOfTroopsInNeutralFactoryIfIConquerIt() {
        auxiliary.setFactory(0,0,10,2,0);
        auxiliary.setTroop(1, 5,0, 10, 5);
        auxiliary.setTroop(1, 5,0, 15, 3);
        auxiliary.setTroop(-1, 5,0, 4, 2);
        Factory myFactory = engine.factories.getNeutralFactories().stream().filter(f -> f.getId() == 0).findFirst().get();
        int[] enemiesPerTurn = engine.calculateForeseenNumberOfTroopsInTheFactory(myFactory);
        assertThat(enemiesPerTurn[0]).isEqualTo(-10);
        assertThat(enemiesPerTurn[1]).isEqualTo(-6);
        assertThat(enemiesPerTurn[2]).isEqualTo(9);
        assertThat(enemiesPerTurn[3]).isEqualTo(11);
        assertThat(enemiesPerTurn[4]).isEqualTo(23);
        assertThat(enemiesPerTurn[5]).isEqualTo(25);
        assertThat(enemiesPerTurn[6]).isEqualTo(27);
    }

    @Test
    public void shouldCalculateTheCorrectNumberOfTroopsInNeutralFactoryIfIConquerItAndEnemyReconquersIt() {
        auxiliary.setFactory(0,0,10,2,0);
        auxiliary.setTroop(-1, 5,0, 14, 5);
        auxiliary.setTroop(1, 5,0, 15, 3);
        auxiliary.setTroop(-1, 5,0, 4, 2);
        Factory myFactory = engine.factories.getNeutralFactories().stream().filter(f -> f.getId() == 0).findFirst().get();
        int[] enemiesPerTurn = engine.calculateForeseenNumberOfTroopsInTheFactory(myFactory);
        assertThat(enemiesPerTurn[0]).isEqualTo(-10);
        assertThat(enemiesPerTurn[1]).isEqualTo(-6);
        assertThat(enemiesPerTurn[2]).isEqualTo(9);
        assertThat(enemiesPerTurn[3]).isEqualTo(11);
        assertThat(enemiesPerTurn[4]).isEqualTo(-1);
        assertThat(enemiesPerTurn[5]).isEqualTo(-3);
        assertThat(enemiesPerTurn[6]).isEqualTo(-5);
    }

    @Test
    public void shouldCalculateTheCorrectNumberOfTroopsInNeutralFactoryIfEnemyConquerIt() {
        auxiliary.setFactory(0,0,10,2,0);
        auxiliary.setTroop(-1, 5,0, 10, 5);
        auxiliary.setTroop(-1, 5,0, 15, 3);
        auxiliary.setTroop(1, 5,0, 4, 2);
        Factory myFactory = engine.factories.getNeutralFactories().stream().filter(f -> f.getId() == 0).findFirst().get();
        int[] enemiesPerTurn = engine.calculateForeseenNumberOfTroopsInTheFactory(myFactory);
        assertThat(enemiesPerTurn[0]).isEqualTo(-10);
        assertThat(enemiesPerTurn[1]).isEqualTo(-6);
        assertThat(enemiesPerTurn[2]).isEqualTo(-9);
        assertThat(enemiesPerTurn[3]).isEqualTo(-11);
        assertThat(enemiesPerTurn[4]).isEqualTo(-23);
        assertThat(enemiesPerTurn[5]).isEqualTo(-25);
        assertThat(enemiesPerTurn[6]).isEqualTo(-27);
    }

    @Test
    public void shouldCalculateTheCorrectNumberOfTroopsInNeutralFactoryIfEnemyConquerItAndIReconquerIt() {
        auxiliary.setFactory(0,0,10,2,0);
        auxiliary.setTroop(1, 5,0, 14, 5);
        auxiliary.setTroop(-1, 5,0, 15, 3);
        auxiliary.setTroop(1, 5,0, 4, 2);
        Factory myFactory = engine.factories.getNeutralFactories().stream().filter(f -> f.getId() == 0).findFirst().get();
        int[] enemiesPerTurn = engine.calculateForeseenNumberOfTroopsInTheFactory(myFactory);
        assertThat(enemiesPerTurn[0]).isEqualTo(-10);
        assertThat(enemiesPerTurn[1]).isEqualTo(-6);
        assertThat(enemiesPerTurn[2]).isEqualTo(-9);
        assertThat(enemiesPerTurn[3]).isEqualTo(-11);
        assertThat(enemiesPerTurn[4]).isEqualTo(1);
        assertThat(enemiesPerTurn[5]).isEqualTo(3);
        assertThat(enemiesPerTurn[6]).isEqualTo(5);
    }

    @Test
    public void shouldCalculateTheCorrectNumberOfTroopsInMyFactory1() {
        auxiliary.setFactory(0,1,10,2,0);
        auxiliary.setTroop(-1, 5,0, 8, 5);
        auxiliary.setTroop(-1, 5,0, 15, 3);
        auxiliary.setTroop(1, 5,0, 4, 2);
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        int[] enemiesPerTurn = engine.calculateForeseenNumberOfTroopsInTheFactory(myFactory);
        assertThat(enemiesPerTurn[0]).isEqualTo(12);
        assertThat(enemiesPerTurn[1]).isEqualTo(18);
        assertThat(enemiesPerTurn[2]).isEqualTo(5);
        assertThat(enemiesPerTurn[3]).isEqualTo(7);
        assertThat(enemiesPerTurn[4]).isEqualTo(1);
        assertThat(enemiesPerTurn[5]).isEqualTo(3);
        assertThat(enemiesPerTurn[6]).isEqualTo(5);
    }

    @Test
    public void shouldCalculateTheCorrectNumberOfTroopsInEnemyFactory1() {
        auxiliary.setFactory(0,-1,10,2,0);
        auxiliary.setTroop(1, 5,0, 8, 5);
        auxiliary.setTroop(1, 5,0, 15, 3);
        auxiliary.setTroop(-1, 5,0, 4, 2);
        Factory myFactory = engine.factories.getEnemyFactories().stream().filter(f -> f.getOwner() == -1).findFirst().get();
        int[] enemiesPerTurn = engine.calculateForeseenNumberOfTroopsInTheFactory(myFactory);
        assertThat(enemiesPerTurn[0]).isEqualTo(-12);
        assertThat(enemiesPerTurn[1]).isEqualTo(-18);
        assertThat(enemiesPerTurn[2]).isEqualTo(-5);
        assertThat(enemiesPerTurn[3]).isEqualTo(-7);
        assertThat(enemiesPerTurn[4]).isEqualTo(-1);
        assertThat(enemiesPerTurn[5]).isEqualTo(-3);
        assertThat(enemiesPerTurn[6]).isEqualTo(-5);
    }

    @Test
    public void shouldGetTheNumberOfTroopsINeedForDefense() {
        auxiliary.setFactory(0,1,10,2,0);
        auxiliary.setTroop(-1, 5,0, 10, 5);
        auxiliary.setTroop(-1, 5,0, 15, 3);
        auxiliary.setTroop(1, 5,0, 4, 2);
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        int[] enemiesPerTurn = engine.calculateForeseenNumberOfTroopsInTheFactory(myFactory);
        int troopsNeeded = engine.getNumberOfTroopsINeedToDefendMyFactory(myFactory, enemiesPerTurn);
        assertThat(troopsNeeded).isEqualTo(-1);

    }

    @Test
    public void shouldGetTheNumberOfTroopsINeedForDefense1() {
        auxiliary.setFactory(0,1,10,2,0);
        auxiliary.setTroop(-1, 5,0, 8, 5);
        auxiliary.setTroop(-1, 5,0, 15, 3);
        auxiliary.setTroop(1, 5,0, 4, 2);
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        int[] enemiesPerTurn = engine.calculateForeseenNumberOfTroopsInTheFactory(myFactory);
        int troopsNeeded = engine.getNumberOfTroopsINeedToDefendMyFactory(myFactory, enemiesPerTurn);
        assertThat(troopsNeeded).isEqualTo(1);
    }

    @Test
    public void shouldSetMyFactoryAsEndangered() {
        auxiliary.setFactory(0,1,10,2,0);
        auxiliary.setTroop(-1, 5,0, 10, 5);
        auxiliary.setTroop(-1, 5,0, 15, 3);
        auxiliary.setTroop(1, 5,0, 4, 2);
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        engine.setNumberOfCyborgsToTheMaximumItIsSafeToSpend(myFactory);
        assertThat(myFactory.isInDanger()).isTrue();
        assertThat(myFactory.getNumberOfTroopsIncoming()).isEqualTo(1);
        assertThat(myFactory.getNumberOfTurnsUntilArrival()).isEqualTo(5);
    }

    @Test
    public void shouldNotSetMyFactoryAsEndangered() {
        auxiliary.setFactory(0,1,10,2,0);
        auxiliary.setTroop(-1, 5,0, 8, 5);
        auxiliary.setTroop(-1, 5,0, 15, 3);
        auxiliary.setTroop(1, 5,0, 4, 2);
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        engine.setNumberOfCyborgsToTheMaximumItIsSafeToSpend(myFactory);

        assertThat(myFactory.isInDanger()).isFalse();
    }

    @Test
    public void shouldSendTroopsToDefendMyFactory() {
        auxiliary.setFactory(0,1,10,2,0);
        auxiliary.setFactory(1,1,10,2,0);
        auxiliary.setTroop(-1, 5,0, 30, 15);
        auxiliary.setTroop(-1, 5,0, 15, 3);
        auxiliary.setTroop(1, 5,0, 4, 2);
        Factory myFactoryInDanger = engine.factories.getMyFactories().stream().filter(f -> f.getId() == 0).findFirst().get();
        Factory myFactoryToSendTroops = engine.factories.getMyFactories().stream().filter(f -> f.getId() == 1).findFirst().get();
        engine.setNumberOfCyborgsToTheMaximumItIsSafeToSpend(myFactoryInDanger);
        engine.defendMyFactories(myFactoryToSendTroops, 2);
        assertThat(myFactoryInDanger.isInDanger()).isTrue();
        assertThat(myFactoryInDanger.getNumberOfTroopsIncoming()).isEqualTo(1);
        assertThat(myFactoryInDanger.getNumberOfTurnsUntilArrival()).isEqualTo(15);
        assertThat(myFactoryToSendTroops.getCyborgs()).isEqualTo(9);
        assertThat(engine.actions).contains("MOVE 1 0 1");
    }

    @Test
    public void shouldNotSendTroopsToDefendMyFactory() {
        auxiliary.setFactory(0,1,10,2,0);
        auxiliary.setFactory(1,1,10,2,0);
        auxiliary.setTroop(-1, 5,0, 10, 5);
        auxiliary.setTroop(-1, 5,0, 15, 3);
        auxiliary.setTroop(1, 5,0, 4, 2);
        Factory myFactoryInDanger = engine.factories.getMyFactories().stream().filter(f -> f.getId() == 0).findFirst().get();
        Factory myFactoryToSendTroops = engine.factories.getMyFactories().stream().filter(f -> f.getId() == 1).findFirst().get();
        engine.setNumberOfCyborgsToTheMaximumItIsSafeToSpend(myFactoryInDanger);
        engine.defendMyFactories(myFactoryToSendTroops, 2);
        assertThat(myFactoryInDanger.isInDanger()).isTrue();
        assertThat(myFactoryInDanger.getNumberOfTroopsIncoming()).isEqualTo(1);
        assertThat(myFactoryInDanger.getNumberOfTurnsUntilArrival()).isEqualTo(5);
        assertThat(myFactoryToSendTroops.getCyborgs()).isEqualTo(10);
        assertThat(engine.actions).isEmpty();
    }

    //TODO: Test findFirstReinforcementNeededAndTurnsUntilAttack
}