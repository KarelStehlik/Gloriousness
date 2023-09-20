import glob
import os


def toClassText(input):
    className, *spl = input.split("|")
    initFunc = "    @Override\n    public void init() {\n      " + "f;\n      ".join(spl) + "f;\n    }"
    declaration = "    public float " + "f;\n    public float ".join(spl) + "f;"
    whole = "public static final class Stats extends BaseStats {\n" + declaration + "\n\n" + initFunc + "\n\n    public Stats() {init();}\n  } // end of generated stats"
    return className, whole


def replaceStats(input, newStats):
    found = input.find("public static final class Stats extends BaseStats {")
    if found == -1:
        return None
    found2 = input.find("// end of generated stats")
    if found2 == -1:
        return None

    input = [*input]
    input[found:found2 + len("// end of generated stats")] = newStats
    input = "".join(input)
    return input


def findAndUpdate(statsText):
    className, stats = toClassText(statsText)
    path = "src\main\java\Game\\" + className + ".java"
    if not os.path.exists(path):
        return -1

    with open(path, "r") as file:
        text = file.read()

    text = replaceStats(text, stats)

    if text is None:
        return -2

    with open(path, "w") as file:
        file.write(text)

    return 1


def handleStatsText(statsText):
    re = findAndUpdate(statsText)
    if re < 0:
        print("failed task for stats: " + statsText.split("|")[0], end=": ")
        if re == -1:
            print("file not found")
        if re == -2:
            print("stats preset not found")
        return

    print("stats handled successfully: " + statsText)


def main():
    for filename in os.listdir("stats"):
        with open("stats\\"+filename, "r") as file:
            for line in file.readlines():
                handleStatsText(line.strip())
   # handleStatsText("Player|speed=1|health=100|cd=5|projSize=100|projSpeed=30|projPierce=100|projDuration=3|projPower=100")


main()
