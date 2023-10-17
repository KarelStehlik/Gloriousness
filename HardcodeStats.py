import glob
import os


def toClassText(input):
    input = input.replace(" ", "")
    className, baseStats, extraStats = input.replace("="," = ").split("%")
    baseStats = baseStats.split("|")
    extraStats = extraStats.split("|")

    if baseStats==[""]:baseStats=[]
    if extraStats==[""]:extraStats=[]

    hasExtra = len(extraStats) != 0
    hasBase = len(baseStats) != 0
    print(className, baseStats)

    initExtra = "    public void init() {\n"+"      "*hasExtra + "f;\n      ".join(extraStats) + "f;" * hasExtra + "\n    }"
    declaration = "\n    public float " + "f;\n    public float ".join(extraStats) + "f;"

    overrideBaseStats = "      " * hasBase + "f;\n      ".join(baseStats) + "f;" * hasBase + "\n    }"

    endExtra = "  }"

    all = f'''// generated stats
  public static final class ExtraStats [

    public ExtraStats() [
      init();
    ]
{declaration * hasExtra}
{initExtra}
  ]

  public static final class Stats extends BaseStats [

    public Stats() [
      init();
    ]

    @Override
    public void init() [
{overrideBaseStats}
  ]
  // end of generated stats'''.replace("[","{").replace("]","}")
    return className, all


def replaceStats(input, newStats):
    found = input.find("// generated stats")
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
        with open("stats\\" + filename, "r") as file:
            for line in file.readlines():
                handleStatsText(line.strip())


# handleStatsText("Player|speed=1|health=100|cd=5|projSize=100|projSpeed=30|projPierce=100|projDuration=3|projPower=100")

if __name__=="__main__":
    if False and os.path.exists("build_log.txt") and os.path.getmtime("build_log.txt") > max(os.path.getmtime("stats/"+filename) for filename in os.listdir("stats")):
        print("skipped: no stat changes")
    else:
        main()
