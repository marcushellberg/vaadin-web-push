package org.vaadin.demo.push.components;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.internal.JsonSerializer;
import com.vaadin.flow.templatemodel.TemplateModel;
import elemental.json.JsonObject;
import nl.martijndwars.webpush.Subscription;

@Tag("push-notification-button")
@HtmlImport("frontend://src/push-notification-button.html")
public class PushNotificationButton extends PolymerTemplate<PushNotificationButton.Model> {

  private SubscriptionUpdateListener listener;

  @FunctionalInterface
  public interface SubscriptionUpdateListener {
    void subscriptionUpdated(Subscription subscription);
  }

  public interface Model extends TemplateModel {
    void setVapidPubKey(String vapidPubKey);
  }

  public PushNotificationButton(String vapidPubKey, SubscriptionUpdateListener listener) {
    this.listener = listener;
    getModel().setVapidPubKey(vapidPubKey);
  }

  @ClientCallable
  void updateSubscription(JsonObject subscriptionJson) {
    Subscription subscription = null;
    if(subscriptionJson != null) {
      String endpoint = subscriptionJson.getString("endpoint");
      JsonObject keys = subscriptionJson.getObject("keys");
      String p256dh = keys.getString("p256dh");
      String auth = keys.getString("auth");

      // Either the API below is weird or I'm using it wrong. Keys should be a static class.
      subscription = new Subscription(endpoint, new Subscription().new Keys(p256dh, auth));
    }

    listener.subscriptionUpdated(subscription);
  }

}
