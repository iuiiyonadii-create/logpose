import time
import threading
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

class NetflixController:
    def __init__(self, driver):
        self.driver = driver
        self.wait = WebDriverWait(self.driver, 12)
        self._loop_activo = False

    def abrir(self):
        self.driver.get("https://www.netflix.com/browse")

    def buscar_y_reproducir(self, query):
        url = f"https://www.netflix.com/search?q={query.replace(' ', '%20')}"
        self.driver.get(url)
        try:
            card = self.wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, "div.gallery a.slider-refocus, div.title-card a")))
            card.click()
            play = self.wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, "button[data-uia='play-button']")))
            play.click()
        except: pass

    def activar_loop(self):
        if self._loop_activo: return
        self._loop_activo = True
        threading.Thread(target=self._loop_watch, daemon=True).start()

    def _loop_watch(self):
        while self._loop_activo:
            try:
                replay = self.driver.find_element(By.CSS_SELECTOR, "button[data-uia='replay-button']")
                replay.click()
            except: pass
            time.sleep(5)

    def play_pause(self):
        self.driver.find_element(By.TAG_NAME, "body").send_keys(Keys.SPACE)
