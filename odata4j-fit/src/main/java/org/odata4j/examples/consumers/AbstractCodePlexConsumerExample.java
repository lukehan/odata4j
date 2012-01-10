package org.odata4j.examples.consumers;

import org.core4j.Enumerable;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.ORelatedEntitiesLink;
import org.odata4j.examples.BaseCredentialsExample;
import org.odata4j.examples.ConsumerExample;

public abstract class AbstractCodePlexConsumerExample extends BaseCredentialsExample implements ConsumerExample{

  // for more info: https://codeplexodata.cloudapp.net/

  private static final int MAX_LISTING = 5;

  @Override
  public void run(String... args) {

    ODataConsumer.dump.requestHeaders(true);

    String[] codeplexCreds = args.length > 0 ? args : System.getenv("CODEPLEX").split(":");

    this.setLoginName("snd\\" + codeplexCreds[0] + "_cp");
    this.setLoginPassword(codeplexCreds[1]);

    for (String collection : Enumerable.create("TFS03", "TFS05", "TFS09")) {
//      ODataConsumer c = ODataJerseyConsumer.newBuilder("https://codeplexodata.cloudapp.net/" + collection).setClientBehaviors(OClientBehaviors.basicAuth(codeplexUser, codeplexPassword)).build();
      ODataConsumer c = this.create("https://codeplexodata.cloudapp.net/" + collection);

      for (OEntity p : c.getEntities("Projects").execute()) {
        reportEntity("project:", p);
        if (p.getProperty("Name", String.class).getValue().equals("s3"))
          continue;

        for (OEntity cs : listChildren(c, p, "Changesets")) {
          reportEntity("changeset:", cs);
          for (OEntity ch : listChildren(c, cs, "Changes")) {
            reportEntity("change:", ch);
          }
        }

        for (OEntity wi : listChildren(c, p, "WorkItems")) {
          reportEntity("workitem:", wi);
          for (OEntity a : listChildren(c, wi, "Attachments")) {
            reportEntity("attachment:", a);
          }
        }
      }
    }

  }

  private static Iterable<OEntity> listChildren(ODataConsumer c, OEntity parent, String child) {
    return c.getEntities(parent.getLink(child, ORelatedEntitiesLink.class)).execute().take(MAX_LISTING);
  }

}
