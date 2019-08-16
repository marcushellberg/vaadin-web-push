package org.vaadin.demo.push;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.notification.PushNotificationService;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

@Route("")
@PWA(shortName = "Push it", name = "Push it real hard")
public class WebPushDemo extends VerticalLayout {

  private final PushNotificationService pushService;
  // DEMO ONLY. DON'T STORE KEYS IN SOURCE!
  String vapidPubKey = "BBZaPWWVHE_NDIPuYQpJS0WAo_VrGjCEBYBfvLurP6s3Cc89J2g9HZ9oPnQJopcB4fpJCf9LlZa4rMjVMsPH2o8";
  String vapidPrivateKey = "F7SfQROj-mgtcF3X54mjfR-tqEjRasPpuQ9hCj07Ask";

  private String subscription;

  public WebPushDemo() {
    pushService = new PushNotificationService(vapidPubKey, vapidPrivateKey, "mailto:webmaster@vaadin.com");

    add(new H1("Web Push"));
    add(new Paragraph("Demo app for web push notifications in Vaadin apps. You need to click disable/enable after restart as the subscription is not persisted across deploys on the server."));

    pushService.browserSupportsPushNotifications().thenAccept(support -> {
      if(support) {
        add(createPushToggleButton());
        add(createPushNotificationForm());
      } else {
        add(new Span("Your browser doesn't support push notifications"));
      }
    });
  }

  private Component createPushToggleButton() {
    Button pushButton = new Button();
    pushService.notificationsEnabled().thenAccept(enabled -> {
      pushButton.setText(enabled ? "Disable notifications" : "Enable notifications");
    });

    pushButton.addClickListener(click -> {
      pushService.notificationsEnabled().thenAccept(enabled -> {
        if (!enabled) {
          pushService.subscribeToNotifications().thenAccept(subscription -> {
            this.subscription = subscription;
            pushButton.setText("Disable notifications");
          });
        } else {
          pushService.unsubscribeFromNotifications();
          this.subscription = null;
          pushButton.setText("Enable notifications");
        }
      });
    });
    return pushButton;
  }

  private Component createPushNotificationForm() {
    HorizontalLayout pushMessageLayout = new HorizontalLayout();
    pushMessageLayout.setDefaultVerticalComponentAlignment(Alignment.END);
    TextField titleField = new TextField("Title");
    TextField contentField = new TextField("Body");
    Button sendButton = new Button("Send");

    pushMessageLayout.add(titleField, contentField, sendButton);

    sendButton.addClickListener(click -> {
      if (subscription == null) {
        Notification.show("Enable push first");
        return;
      }
      pushService.sendPushMessage(subscription,
          titleField.getValue(),
          contentField.getValue(),
          "/");
    });


    return pushMessageLayout;
  }

}
