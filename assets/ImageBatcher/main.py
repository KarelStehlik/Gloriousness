from PIL import Image
import os

TEXSIZE = 4096


def sortFunc(image):
    return max(image.size[0], image.size[1])


class TestSetup:
    def __init__(self, original):
        self.POIs = [e for e in original.POIs]
        self.covered = [e for e in original.covered]

    def can_add(self, left, bottom, width, height):
        if bottom + height > TEXSIZE or left + width > TEXSIZE:
            return False
        for e in self.covered:
            if left < e[0] + e[2] and left + width > e[0] and bottom < e[1] + e[3] and bottom + height > e[1]:
                return False
        return True




class Base:
    POIs = [(0, 0)]
    covered = []


files = [Image.open("input/" + e) for e in os.listdir("input")]
files.sort(key=sortFunc, reverse=True)
n_textures = 0
print(str([e.filename[6:-4] for e in files]).replace("\'", "\""))

while len(files) > 0:
    # create a new image
    new = Image.new('RGBA', (TEXSIZE, TEXSIZE), (255, 255, 255, 0))
    setup = TestSetup(Base())
    text = ""
    i = 0
    # loop through all textures, see if they fit on the image
    while i < len(files):
        e = files[i]
        for poi in setup.POIs:
            if setup.can_add(*poi, *e.size):
                # it's fine to modify POIs here, because if we do, we break the loop iterating over them
                setup.POIs.append((poi[0] + e.size[0], poi[1]))
                setup.POIs.append((poi[0], poi[1] + e.size[1]))
                setup.POIs.remove(poi)
                setup.covered.append((*poi, *e.size))
                new.paste(e, poi)
                files.pop(i)
                b, l, t, r = poi[1] / TEXSIZE, poi[0] / TEXSIZE, (poi[1] + e.size[1]) / TEXSIZE, (
                            poi[0] + e.size[0]) / TEXSIZE
                text += f"\n{e.filename[6:-4]} {r} {b} {l} {t} {r} {t} {l} {b}"
                i -= 1
                break
        i += 1
    new.save("result/T" + str(n_textures) + ".png")
    file = open(f"T{n_textures}.txt", "w")
    file.write(text[1::])
    n_textures += 1
