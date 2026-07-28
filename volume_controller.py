import time
import keyboard

class VolumeController:
    def _tecla(self, nombre, veces):
        for _ in range(veces):
            keyboard.send(nombre)
            time.sleep(0.03)

    def bajar_a_cero(self):
        self._tecla("volume down", 60)

    def setear_porcentaje(self, porcentaje):
        self.bajar_a_cero()
        self._tecla("volume up", round(porcentaje / 2))

    def subir(self, delta=10):
        self._tecla("volume up", round(delta / 2))

    def bajar(self, delta=10):
        self._tecla("volume down", round(delta / 2))

    def mute(self):
        keyboard.send("volume mute")
