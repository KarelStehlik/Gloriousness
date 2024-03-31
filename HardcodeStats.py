import glob
import os

def refFloatify(string, statclass):
    name,eq,num=string.split(" ")
    return f"stats[{statclass}.{name}] = {num}f;"

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

    extraNames = [s.split(" ")[0] for s in extraStats]
    extraStats = [refFloatify(x, "ExtraStats") for x in extraStats]
    baseStats = [refFloatify(x, "Stats") for x in baseStats]

    statsAssign = "    " + "\n    ".join(baseStats+extraStats)
    init="  @Override\n  public void clearStats() {\n"+statsAssign+"\n  }\n"

    start = hasExtra * (
'''  @Override
  public int getStatsCount() {\n    return ''' + str(len(extraStats)+len(baseStats)) + ";\n  }\n\n")

    extraS=hasExtra*('''
  public static final class ExtraStats {\n
    '''+
    "\n   ".join("public static final int "+name+" = "+str(len(baseStats)+i)+";" for i,name in enumerate(extraNames))+
    '''\n
    private ExtraStats() {\n    }
  }\n''')

    return className, "// generated stats\n"+start+init+extraS+"  // end of generated stats"


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
    #print(text)
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
