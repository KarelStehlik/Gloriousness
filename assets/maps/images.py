import pyglet
import os

data = {}

class im:
    def __getattr__(self, item):
        if item in data:
            return data[item]
        else:
            print(f"no image \"{item}\"")
            return data["Background"]


images = im()

for e in os.listdir("imageFiles"):
    data[e[:-4]] = pyglet.image.load("imageFiles/" + e)

