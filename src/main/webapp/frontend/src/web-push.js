(() => {
  window.addEventListener('load', e => {
    registerSW();
  });

  async function registerSW() {
    if ('serviceWorker' in navigator) {
      try {
        const swRegistration = await navigator.serviceWorker.register(
          '/frontend/src/sw.js'
        );
        window.Vaadin = window.Vaadin || {};
        window.Vaadin.swRegistration = swRegistration;

        dispatchEvent(new CustomEvent('sw-registered', {
          detail: swRegistration,
          bubbles: true,
          composed: true
        }))
      } catch (e) {
        console.log('ServiceWorker registration failed.', e);
      }
    } else {
      console.log('ServiceWorker is not supported on this browser.')
    }
  }


})();

