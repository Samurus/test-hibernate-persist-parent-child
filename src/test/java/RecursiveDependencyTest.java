import com.google.inject.Key;
import com.google.inject.persist.Transactional;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.iterableWithSize;

@RunWith(JukitoRunner.class)
@UseModules(DatabaseTestModule.class)
public class RecursiveDependencyTest {

    @Inject EntityManager entityManager;

    @Test
    @Transactional
    public void testBiDirectionalPersistenceWithUUIDGenerator() throws Exception {
        Riddle parent = new Riddle();
        KeyWord child = new KeyWord();

        // Add child to parents children
        List<KeyWord> parentsChildren = new ArrayList<>();
        parentsChildren.add(child);
        parentsChildren.add(child);
        parent.setChildren(parentsChildren);
        // Set back reference as well
        child.setParent(parent);

        entityManager.persist(parent);

        assertThat(parent.getChildren(), iterableWithSize(2));
    }

}
