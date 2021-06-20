import com.google.inject.Key;
import com.google.inject.persist.Transactional;
import java.util.Arrays;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Assert;
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
    public void testWithExistingData() throws Exception {
        org.hibernate.Session session = (Session) entityManager.getDelegate();

        //persist Riddle with 0 childs
        Riddle firstRiddle = new Riddle();
        firstRiddle.setQuestion("GJH");
        entityManager.persist(firstRiddle);

        //find persisted Riddle
        Criteria criteria = session.createCriteria(Riddle.class);
        Riddle parentRiddle = (Riddle) criteria.add(Restrictions.eq("question", "GJH")).uniqueResult();
        Assert.assertEquals( "GJH", parentRiddle.getQuestion());

        //update Riddle with 1 child
        KeyWord child = new KeyWord();
        child.setId(102L);
        parentRiddle.setChildren(Arrays.asList(child));
        entityManager.persist(parentRiddle);
        assertThat(parentRiddle.getChildren(), iterableWithSize(1));

        session.createCriteria(Riddle.class);
        Riddle parentRiddleWith1Child = (Riddle) criteria.add(Restrictions.eq("question", "GJH")).uniqueResult();
        assertThat(parentRiddle.getChildren(), iterableWithSize(1));

        //update Riddle with existing and new child
        List<KeyWord> children = parentRiddleWith1Child.getChildren();
        KeyWord child2 = new KeyWord();
        child2.setId(103L);

        //you cannot work on children directly
        ArrayList<KeyWord> keyWords = new ArrayList<>(children);
        keyWords.add(child2);
        parentRiddle.setChildren(keyWords);
        entityManager.persist(parentRiddle);
        assertThat(parentRiddle.getChildren(), iterableWithSize(2));

        System.out.println(parentRiddle.getChildren());



    }


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
