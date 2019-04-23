package rodriguez.codingame;


import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
    public void shouldCalculateForEnemyFactory(){
        auxiliary.setFactory(0,-1,10,2,0);
        auxiliary.setFactory(1,1,10,2,0);
        auxiliary.setTroop(-1, 5,0, 4, 2);
        auxiliary.setTroop(1, 5,0, 15, 3);
        auxiliary.setTroop(1, 5,0, 8, 5);
        auxiliary.setTroop(1, 5,0, 15, 11);
        auxiliary.setTroop(-1, 5,0, 11, 15);
        Factory enemyFactory = engine.factories.getEnemyFactories().stream().filter(f -> f.getOwner() == -1).findFirst().get();
        int[] enemiesPerTurn = engine.calculateForeseenNumberOfTroopsInTheFactory(enemyFactory);
        // -12, -18, -5, -7, -1, -3, -5, -7, -9, -11, 2, 4, 6, 8, -1, -3, -5, -7....
        assertThat(enemiesPerTurn[0]).isEqualTo(-12);
        assertThat(enemiesPerTurn[1]).isEqualTo(-18);
        assertThat(enemiesPerTurn[2]).isEqualTo(-5);
        assertThat(enemiesPerTurn[3]).isEqualTo(-7);
        assertThat(enemiesPerTurn[4]).isEqualTo(-1);
        assertThat(enemiesPerTurn[5]).isEqualTo(-3);
        assertThat(enemiesPerTurn[6]).isEqualTo(-5);
        assertThat(enemiesPerTurn[7]).isEqualTo(-7);
        assertThat(enemiesPerTurn[8]).isEqualTo(-9);
        assertThat(enemiesPerTurn[9]).isEqualTo(-11);
        assertThat(enemiesPerTurn[10]).isEqualTo(2);
        assertThat(enemiesPerTurn[11]).isEqualTo(4);
        assertThat(enemiesPerTurn[12]).isEqualTo(6);
        assertThat(enemiesPerTurn[13]).isEqualTo(8);
        assertThat(enemiesPerTurn[14]).isEqualTo(-1);
        assertThat(enemiesPerTurn[15]).isEqualTo(-3);
        assertThat(enemiesPerTurn[16]).isEqualTo(-5);

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

    @Test
    public void shouldCalculateFirstReinforcementNeededAndTurnsUntilAttack() {
        auxiliary.setFactory(0,1,10,2,0);
        auxiliary.setTroop(-1, 5,0, 10, 5);
        auxiliary.setTroop(-1, 5,0, 15, 3);
        auxiliary.setTroop(1, 5,0, 4, 2);
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        int[] enemiesPerTurn = engine.calculateForeseenNumberOfTroopsInTheFactory(myFactory);
        int[] firstReinforcementNeededAndTurnsUntilAttack = engine.findFirstReinforcementNeededAndTurnsUntilAttack(enemiesPerTurn);
        assertThat(firstReinforcementNeededAndTurnsUntilAttack[0]).isEqualTo(-1);
        assertThat(firstReinforcementNeededAndTurnsUntilAttack[1]).isEqualTo(5);
    }

    @Test
    public void shouldCalculateFirstReinforcementNeededAndTurnsUntilAttack1() {
        auxiliary.setFactory(0,1,10,2,0);
        auxiliary.setTroop(-1, 5,0, 8, 5);
        auxiliary.setTroop(-1, 5,0, 15, 3);
        auxiliary.setTroop(1, 5,0, 4, 2);
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        int[] enemiesPerTurn = engine.calculateForeseenNumberOfTroopsInTheFactory(myFactory);
        int[] firstReinforcementNeededAndTurnsUntilAttack = engine.findFirstReinforcementNeededAndTurnsUntilAttack(enemiesPerTurn);
        assertThat(firstReinforcementNeededAndTurnsUntilAttack[0]).isEqualTo(0);
        assertThat(firstReinforcementNeededAndTurnsUntilAttack[1]).isEqualTo(0);
    }

    @Test
    public void shouldReturnZeroBecauseItIsAlreadyBeingConquered() {
        auxiliary.setFactory(0,-1,10,2,0);
        auxiliary.setFactory(1,1,10,2,0);
        auxiliary.setTroop(1, 5,0, 10, 5);
        auxiliary.setTroop(1, 5,0, 15, 3);
        auxiliary.setTroop(-1, 5,0, 4, 2);
        Factory enemyFactory = engine.factories.getEnemyFactories().stream().filter(f -> f.getOwner() == -1).findFirst().get();
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        int numberOfTroopsINeedToConquerTheFactory = engine.getNumberOfTroopsINeedToConquerTheFactory(myFactory, enemyFactory);
        // -12, -18, -5, -7, 1, 3, 5
        assertThat(numberOfTroopsINeedToConquerTheFactory).isEqualTo(0);
    }

    @Test
    public void shouldReturnFourteen() {
        // The distance between them is 10, so it can act only from turn 10. The lower amount of enemy troops after turn 10, is 11. As it takes one extra turn, because in this turn it is generated, and starts traveling on next,
        //So I actually need 14
        auxiliary.setFactory(0,-1,10,2,0);
        auxiliary.setFactory(1,1,10,2,0);
        auxiliary.setTroop(1, 5,0, 8, 5);
        auxiliary.setTroop(1, 5,0, 15, 3);
        auxiliary.setTroop(-1, 5,0, 4, 2);
        Factory enemyFactory = engine.factories.getEnemyFactories().stream().filter(f -> f.getOwner() == -1).findFirst().get();
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        int numberOfTroopsINeedToConquerTheFactory = engine.getNumberOfTroopsINeedToConquerTheFactory(myFactory, enemyFactory);
        // -12, -18, -5, -7, -1, -3, -5, -7, -9, -11, -13...
        assertThat(numberOfTroopsINeedToConquerTheFactory).isEqualTo(14);
    }

    @Test
    public void shouldReturnEight() {
        auxiliary.setFactory(0,-1,10,2,0);
        auxiliary.setFactory(1,1,10,2,0);
        auxiliary.setTroop(1, 5,0, 4, 2);
        auxiliary.setTroop(1, 5,0, 12, 3);
        auxiliary.setTroop(1, 5,0, 8, 5);
        auxiliary.setTroop(-1, 5,0, 9, 7);
        Factory enemyFactory = engine.factories.getEnemyFactories().stream().filter(f -> f.getOwner() == -1).findFirst().get();
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        int numberOfTroopsINeedToConquerTheFactory = engine.getNumberOfTroopsINeedToConquerTheFactory(myFactory, enemyFactory);
        // -12, -10, 0, -2, 4, 6, -1, -3, -5, -7, -9...
        assertThat(numberOfTroopsINeedToConquerTheFactory).isEqualTo(10);
    }

    @Test
    public void shouldReturnZero() {
        auxiliary.setFactory(0,-1,10,2,0);
        auxiliary.setFactory(1,1,10,2,0);
        auxiliary.setTroop(1, 5,0, 8, 5);
        auxiliary.setTroop(1, 5,0, 15, 3);
        auxiliary.setTroop(1, 5,0, 15, 11);
        auxiliary.setTroop(-1, 5,0, 4, 2);
        Factory enemyFactory = engine.factories.getEnemyFactories().stream().filter(f -> f.getOwner() == -1).findFirst().get();
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        int numberOfTroopsINeedToConquerTheFactory = engine.getNumberOfTroopsINeedToConquerTheFactory(myFactory, enemyFactory);
        // -12, -18, -5, -7, -1, -3, -5, -7, -9, -11, 2...
        assertThat(numberOfTroopsINeedToConquerTheFactory).isEqualTo(0);
    }

    @Test
    public void shouldReturnTwo() {
        auxiliary.setFactory(0,-1,10,2,0);
        auxiliary.setFactory(1,1,10,2,0);
        auxiliary.setTroop(-1, 5,0, 4, 2);
        auxiliary.setTroop(1, 5,0, 15, 3);
        auxiliary.setTroop(1, 5,0, 8, 5);
        auxiliary.setTroop(1, 5,0, 15, 11);
        auxiliary.setTroop(-1, 5,0, 11, 15);
        Factory enemyFactory = engine.factories.getEnemyFactories().stream().filter(f -> f.getOwner() == -1).findFirst().get();
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        int numberOfTroopsINeedToConquerTheFactory = engine.getNumberOfTroopsINeedToConquerTheFactory(myFactory, enemyFactory);
        // -12, -18, -5, -7, -1, -3, -5, -7, -9, -11, 2, 4, 6, 8, -1, -3, -5, -7....
        assertThat(numberOfTroopsINeedToConquerTheFactory).isEqualTo(2);
    }

    @Test
    public void shouldReturn4() {
        auxiliary.setFactory(0, 0, 3, 2, 0);
        auxiliary.setFactory(1, 1, 4, 3, 0);
        auxiliary.setTroop(-1, 5, 0, 4, 3);
        Factory neutralFactory = engine.factories.getNeutralFactories().stream().filter(f -> f.getId() == 0).findFirst().get();
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getId() == 1).findFirst().get();
        auxiliary.setDistance(myFactory, neutralFactory, 3);

        int numberOfTroopsINeedToConquerTheFactory = engine.getNumberOfTroopsINeedToConquerTheFactory(myFactory, neutralFactory);
        assertThat(numberOfTroopsINeedToConquerTheFactory).isEqualTo(4);
    }

    @Test
    public void shouldAddBothBombs() {
        engine.bombs.addBomb(0, -1, 5, -1, -1);
        engine.bombs.addBomb(1, -1, 5, -1, -1);
        engine.bombs.updateListOfBombs();
        List<Bomb> enemyBombs = engine.bombs.getEnemyBombs();
        assertThat(enemyBombs.size()).isEqualTo(2);
    }

    @Test
    public void shouldRemoveBothBombs() {
        engine.bombs.addBomb(0, -1, 5, -1, -1);
        engine.bombs.addBomb(1, -1, 5, -1, -1);
        engine.bombs.updateListOfBombs();
        engine.bombs.resetTemporaryBombList();
        engine.bombs.updateListOfBombs();
        List<Bomb> enemyBombs = engine.bombs.getEnemyBombs();
        assertThat(enemyBombs.size()).isEqualTo(0);
    }

    @Test
    public void shouldRemoveOneBombsAndUpdateTheOther() {
        engine.bombs.addBomb(0, -1, 5, -1, -1);
        engine.bombs.addBomb(1, -1, 5, -1, -1);
        engine.bombs.updateListOfBombs();
        engine.bombs.resetTemporaryBombList();
        engine.bombs.addBomb(0, -1, 5, -1, -1);
        engine.bombs.updateListOfBombs();
        List<Bomb> enemyBombs = engine.bombs.getEnemyBombs();
        Bomb bomb = enemyBombs.stream().filter(b -> b.getId() == 0).findFirst().get();
        assertThat(enemyBombs.size()).isEqualTo(1);
        assertThat(bomb.getTurnsSinceLaunching()).isEqualTo(1);
    }

    @Test
    public void shouldAvoidTheBomb() {
        auxiliary.setFactory(0, 1, 5, 0, 0);
        auxiliary.setFactory(2, 1, 5, 0, 0);
        auxiliary.setFactory(1, -1, 4, 3, 0);
        Factory enemyFactory = engine.factories.getEnemyFactories().stream().filter(f -> f.getOwner() == -1).findFirst().get();
        Factory myFactory = engine.factories.getMyFactories().stream().filter(f -> f.getOwner() == 1).findFirst().get();
        auxiliary.setDistance(enemyFactory, myFactory, 2);
        engine.bombs.addBomb(0, -1, 1, -1, -1);
        engine.bombs.updateListOfBombs();
        engine.bombs.resetTemporaryBombList();

        engine.bombs.addBomb(0, -1, 1, -1, -1);
        engine.bombs.updateListOfBombs();
        engine.bombs.resetTemporaryBombList();

        engine.avoidEnemyBomb(myFactory);

        assertThat(engine.actions).contains("MOVE 0 2 5");

    }
}