package org.vaadin.demo.push;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import elemental.json.JsonFactory;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.vaadin.demo.push.components.PushNotificationButton;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.concurrent.ExecutionException;

@Route("")
@JavaScript("frontend://src/web-push.js")
public class WebPushDemo extends VerticalLayout implements PageConfigurator {

  // DEMO ONLY. DON'T STORE KEYS IN SOURCE!
  String vapidPubKey = "BBZaPWWVHE_NDIPuYQpJS0WAo_VrGjCEBYBfvLurP6s3Cc89J2g9HZ9oPnQJopcB4fpJCf9LlZa4rMjVMsPH2o8";
  String vapidPrivateKey = "F7SfQROj-mgtcF3X54mjfR-tqEjRasPpuQ9hCj07Ask";
  private Subscription subscription;
  private HorizontalLayout pushMessageLayout;
  private final PushService pushService;

  public WebPushDemo() throws GeneralSecurityException {
    Security.addProvider(new BouncyCastleProvider());
    pushService = new PushService(vapidPubKey, vapidPrivateKey, "mailto:webmaster@vaadin.com");

    add(new H1("Web Push"));
    add(new Paragraph("Demo app for web push notifications in Vaadin apps. You need to click disable/enable after restart as the subscription is not persisted across deploys on the server."));
    add(new PushNotificationButton(vapidPubKey, this::setSubscription));
    add(createPushNotificationForm());
  }

  private Component createPushNotificationForm() {
    pushMessageLayout = new HorizontalLayout();
    TextField contentField = new TextField();
    Button sendButton = new Button("Send");

    pushMessageLayout.add(contentField, sendButton);
    pushMessageLayout.setEnabled(false);

    sendButton.addClickListener(click -> {
      try {
        JsonObject payload = new JsonObject();
        payload.addProperty("title", "Server Says");
        payload.addProperty("body", contentField.getValue());
        pushService.sendAsync(new Notification(subscription, payload.toString()));
      } catch (Exception e) {
        System.err.println("Push message sending failed!");
      }
    });
    return pushMessageLayout;
  }

  private void setSubscription(Subscription subscription) {
    //TODO: subscription should be persisted somewhere for future use
    this.subscription = subscription;
    pushMessageLayout.setEnabled(subscription != null);
  }

  @Override
  public void configurePage(InitialPageSettings pageSettings) {
    pageSettings.addLink("manifest", "frontend/manifest.webmanifest");
  }
}
