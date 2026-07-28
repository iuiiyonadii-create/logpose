from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

class YoutubeController:
    def __init__(self, driver):
        self.driver = driver
        self.wait = WebDriverWait(self.driver, 12)

    def abrir(self):
        self.driver.get("https://www.youtube.com")

    def buscar_y_reproducir(self, query):
        url = f"https://www.youtube.com/results?search_query={query.replace(' ', '+')}"
        self.driver.get(url)
        try:
            video = self.wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, "a#video-title")))
            video.click()
        except: pass

    def activar_loop(self):
        try:
            self.driver.execute_script("document.querySelector('video').loop = true;")
        except:
            self.driver.find_element(By.TAG_NAME, "body").send_keys("0")

    def play_pause(self):
        self.driver.find_element(By.TAG_NAME, "body").send_keys(Keys.SPACE)
