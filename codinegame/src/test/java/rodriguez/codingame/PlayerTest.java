package rodriguez.codingame;


import org.assertj.core.api.Assertions;
import org.junit.Test;

public class PlayerTest {

    @Test
    public void shouldUpgradeTheFactory() {
        GameEngine engine = Auxiliary.setupTest();
        Factory myFactory = (Factory) engine.factories.myFactories.get(0);
        int numberOfCyborgs = myFactory.getCyborgs();
        engine.upgradeMyFactory(myFactory);
        int numberOfCyborgsAfterUpgrade = myFactory.getCyborgs();

        Assertions.assertThat(numberOfCyborgs).isEqualTo(10);
        Assertions.assertThat(numberOfCyborgsAfterUpgrade).isEqualTo(0);
    }

    @Test
    public void shouldNOTUpgradeTheFactory() {
        GameEngine engine = Auxiliary.setupTest();
        Factory myFactory = (Factory) engine.factories.myFactories.get(0);
        myFactory.setCyborgs(9);
        int numberOfCyborgs = myFactory.getCyborgs();
        engine.upgradeMyFactory(myFactory);
        int numberOfCyborgsAfterUpgrade = myFactory.getCyborgs();

        Assertions.assertThat(numberOfCyborgs).isEqualTo(9);
        Assertions.assertThat(numberOfCyborgsAfterUpgrade).isEqualTo(9);
    }
}