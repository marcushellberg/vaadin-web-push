self.addEventListener('push', event => {
  const data = event.data.json();

  const title = data.title || 'Important message';
  const options = {
    body: data.body || 'This is a push notification',
    icon: '/frontend/img/icons/icon-128x128.png',
    badge: '/frontend/img/icons/icon-128x128.png'
  };

  event.waitUntil(self.registration.showNotification(title, options));
});

self.addEventListener('notificationclick', event => {
  event.notification.close();

  event.waitUntil(
    self.clients.matchAll().then(clientList => {
      if (clientList.length > 0) {
        return clientList[0].focus();
      }
      return self.clients.openWindow('./');
    })
  );
});