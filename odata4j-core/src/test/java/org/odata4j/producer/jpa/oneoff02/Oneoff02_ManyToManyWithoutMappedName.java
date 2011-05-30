package org.odata4j.producer.jpa.oneoff02;

import java.util.List;

import junit.framework.Assert;

import org.core4j.Enumerable;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperties;
import org.odata4j.core.ORelatedEntitiesLink;
import org.odata4j.producer.jpa.oneoff.OneoffTestBase;

public class Oneoff02_ManyToManyWithoutMappedName extends OneoffTestBase {

  @BeforeClass
  public static void setUpClass() throws Exception {
    setUpClass(Oneoff02_ManyToManyWithoutMappedName.class, 20);
  }

  @Test
  public void createManyToMany() {
    final long now = System.currentTimeMillis();
    ODataConsumer consumer = ODataConsumer.create(endpointUri);

    OEntity course1 = consumer
        .createEntity("Course")
        .properties(OProperties.string("CourseName", "Name1" + now))
        .get();

    OEntity course2 = consumer
        .createEntity("Course")
        .properties(OProperties.string("CourseName", "Name2" + now))
        .get();

    OEntity student = consumer
        .createEntity("Student")
        .properties(OProperties.string("StudentName", "Student" + now))
        .inline("courses", course1, course2)
        .execute();

    Assert.assertEquals(1, consumer.getEntities("Student").execute().count());
    Assert.assertEquals(2, consumer.getEntities("Course").execute().count());

    OEntity student1 = consumer.getEntities("Student").execute().first();
    Assert.assertEquals(2, consumer.getEntities(student1.getLink("courses", ORelatedEntitiesLink.class)).execute().count());
    Assert.assertEquals(student1.getEntityKey(), student.getEntityKey());
    for (OEntity course : consumer.getEntities("Course").execute()) {
      List<OEntity> courseStudents = consumer.getEntities(course.getLink("students", ORelatedEntitiesLink.class)).execute().toList();
      Assert.assertEquals(1, courseStudents.size());

    }
  }

  @Test
  public void mergeManyToMany() {
    final long now = System.currentTimeMillis();
    ODataConsumer consumer = ODataConsumer.create(endpointUri);

    OEntity course1 = consumer
        .createEntity("Course")
        .properties(OProperties.string("CourseName", "Name1" + now))
        .get();

    OEntity course2 = consumer
        .createEntity("Course")
        .properties(OProperties.string("CourseName", "Name2" + now))
        .get();

    OEntity student = consumer
        .createEntity("Student")
        .properties(OProperties.string("StudentName", "Student" + now))
        .inline("courses", course1, course2)
        .execute();

    Assert.assertNotNull(student);

    consumer.mergeEntity(student)
        .properties(OProperties.string("StudentName", "Student1" + now))
        .execute();

    Enumerable<OEntity> studentEnum = consumer.getEntities("Student").filter("StudentName eq 'Student1" + now + "'").execute();
    Assert.assertEquals(1, studentEnum.count());

    OEntity student1 = studentEnum.elementAt(0);
    Assert.assertEquals(2, consumer.getEntities(student1.getLink("courses", ORelatedEntitiesLink.class)).execute().count());
    Assert.assertEquals(student1.getEntityKey(), student.getEntityKey());
  }

  @Test
  public void deleteManyToMany() {
    final long now = System.currentTimeMillis();
    ODataConsumer consumer = ODataConsumer.create(endpointUri);

    OEntity course1 = consumer
        .createEntity("Course")
        .properties(OProperties.string("CourseName", "Name1" + now))
        .get();

    OEntity course2 = consumer
        .createEntity("Course")
        .properties(OProperties.string("CourseName", "Name2" + now))
        .get();

    OEntity student = consumer
        .createEntity("Student")
        .properties(OProperties.string("StudentName", "Student" + now))
        .inline("courses", course1, course2)
        .execute();

    Assert.assertNotNull(student);

    int beforeCount = consumer.getEntities("Course").execute().count();
    consumer.deleteEntity(student).execute();

    Enumerable<OEntity> studentEnum = consumer.getEntities("Student").filter("StudentName eq 'Student" + now + "'").execute();
    Assert.assertEquals(0, studentEnum.count());

    Assert.assertTrue(beforeCount > consumer.getEntities("Course").execute().count() ? true : false);
  }
}
